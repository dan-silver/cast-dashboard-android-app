package com.silver.dan.castdemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.google.common.base.Splitter;
import com.silver.dan.castdemo.SettingEnums.BackgroundType;
import com.silver.dan.castdemo.databinding.FragmentAppSettingsThemeBinding;
import com.silver.dan.castdemo.settingsFragments.TwoLineSettingItem;
import com.silver.dan.castdemo.util.ImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppSettingsTheme extends AppSettingsHelperFragment {

    private static final int SELECT_PHOTO = 0;
    private static final int MB = 1000000;

    @Bind(R.id.background_type)
    TwoLineSettingItem backgroundType;

    @Bind(R.id.widget_transparency)
    SeekBar widgetTransparency;

    @Bind(R.id.dashboard_background_picture)
    ImageView backgroundPicture;

    @Bind(R.id.upload_progress)
    ProgressBar uploadProgressBar;
    private BackgroundType oldBackgroundType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings_theme, container, false);
        ButterKnife.bind(this, view);

        viewModel = FragmentAppSettingsThemeBinding.bind(view);
        bindings = new AppSettingsBindings();
        bindings.init(this);
        ((FragmentAppSettingsThemeBinding) viewModel).setSettings(bindings);

        widgetTransparency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    bindings.setWidgetTransparency(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if (bindings.getBackgroundType() == BackgroundType.PICTURE) {
            new LoadImageTask().execute(bindings.backgroundImageLocalPath);
        }


        return view;
    }

    public static void sendBackgroundImage(File imageFile) {
        // read the image and send to the TV
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        long imageSize = imageFile.length();
        Bitmap bm = BitmapFactory.decodeFile(imageFile.getPath());
        try {
            bm = ImageUtils.modifyOrientation(bm, imageFile.getPath());
            sendImageToTV(bm, imageSize, null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            if (urls[0].length() > 0)
                return BitmapFactory.decodeFile(urls[0]);
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            backgroundPicture.setImageBitmap(result);
        }
    }

    @OnClick(R.id.slideshowInterval)
    public void setSlideShowInterval() {
        final ArrayList<Integer> options = new ArrayList<>(Arrays.asList(10, 20, 30, 40, 50, 60));
        ArrayList<String> optionLabels = new ArrayList<>();
        for (Integer option : options) {
            optionLabels.add(option + " " + getString(R.string.seconds));
        }
        new MaterialDialog.Builder(getContext())
                .title(R.string.slideshowSpeed)
                .items(optionLabels)
                .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        bindings.setSlideshowInterval(options.get(which));
                        return true;
                    }
                })
                .show();
    }

    @OnClick(R.id.background_type)
    public void setBackgroundType() {
        // it should be safe to rearrange and add items to this list
        final ArrayList<BackgroundType> backgroundTypes = new ArrayList<BackgroundType>() {{
            add(BackgroundType.SLIDESHOW);
            add(BackgroundType.SOLID_COLOR);
            add(BackgroundType.PICTURE);
        }};

        new MaterialDialog.Builder(getContext())
                .title(R.string.background)
                .items(R.array.backgroundTypeList)
                .itemsCallbackSingleChoice(backgroundTypes.indexOf(bindings.getBackgroundType()), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        oldBackgroundType = bindings.getBackgroundType();
                        BackgroundType newBackgroundType = backgroundTypes.get(which);

                        if (oldBackgroundType != newBackgroundType) {
                            // remove old background
                            if (oldBackgroundType == BackgroundType.PICTURE) {
                                removeBackgroundPicture();
                            }

                            // first time selecting a background
                            if (newBackgroundType == BackgroundType.PICTURE && (bindings.backgroundImageLocalPath == null || bindings.backgroundImageLocalPath.isEmpty())) {
                                getBackgroundImage();
                            } else {
                                bindings.setBackgroundType(newBackgroundType);
                                if (newBackgroundType == BackgroundType.PICTURE && bindings.backgroundImageLocalPath != null && !bindings.backgroundImageLocalPath.isEmpty()) {
                                    new LoadImageTask().execute(bindings.getBackgroundImageLocalPath());
                                }
                            }
                        }

                        return true;
                    }


                })
                .show();
    }

    private void removeBackgroundPicture() {
        bindings.setBackgroundImageLocalPath(null);

    }

    @OnClick(R.id.dashboard_background_picture)
    public void getBackgroundImage() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {

                    // remove the old background image, if one exists
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        if (picturePath == null) {
                            Log.e(MainActivity.TAG, "picturePath was null");
                            Toast.makeText(getActivity(), "Error uploading picture", Toast.LENGTH_LONG).show();
                            return;
                        }

                        long imageSize = new File(picturePath).length();
                        Bitmap bm = BitmapFactory.decodeFile(picturePath);
                        try {
                            bm = ImageUtils.modifyOrientation(bm, picturePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Error uploading picture", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // now that we should have the picture, go ahead and change the UI bindings to show the picture option
                        bindings.setBackgroundType(BackgroundType.PICTURE);

                        Drawable d = new BitmapDrawable(getResources(), bm);
                        backgroundPicture.setImageDrawable(d);

                        bindings.setBackgroundImageLocalPath(picturePath);


                        // start with indeterminate as we're reading the bitmap, then determinate as upload progresses
                        uploadProgressBar.setIndeterminate(true);
                        uploadProgressBar.setVisibility(View.VISIBLE);


                        sendImageToTV(bm, imageSize, new FileSavedListener() {
                            private void resetUI() {
                                uploadProgressBar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        uploadProgressBar.setVisibility(View.GONE);
                                    }
                                });
                            }

                            @Override
                            public void onSaved() {
                                resetUI();

                            }

                            @Override
                            public void onError(final String err) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        resetUI();
                                        Toast.makeText(getActivity(), err, Toast.LENGTH_LONG).show();
                                        bindings.setBackgroundImageLocalPath(null);
                                        if (oldBackgroundType != null && oldBackgroundType != BackgroundType.PICTURE)
                                            bindings.setBackgroundType(oldBackgroundType);
                                        else
                                            bindings.setBackgroundType(BackgroundType.SLIDESHOW);

                                    }
                                });
                            }

                            @Override
                            public void onProgress(final int progress, final int total) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        uploadProgressBar.setIndeterminate(false);
                                        uploadProgressBar.setMax(total);
                                        uploadProgressBar.setProgress(progress);
                                    }
                                });
                            }
                        });
                    }
                }
        }
    }

    private static void sendImageToTV(final Bitmap image, final long imageSize, final FileSavedListener callback) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int quality = 100;
                int MAX_IMAGE_SIZE = (int) (0.8 * MB);
                if (imageSize > MAX_IMAGE_SIZE) {
                    quality = (int) (MAX_IMAGE_SIZE / (float) imageSize * 100);
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, quality, bos);

                String imgBase64 = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP);

                int length = imgBase64.length();


                int i = 0;
                int chunkSize = 15 * 1000;
                final int numMessages = (int) Math.ceil((float) length / chunkSize);
                for (String chunk : Splitter.fixedLength(chunkSize).split(imgBase64)) {
                    CastCommunicator.sendText("base64:" + i + "/" + numMessages + ":::" + chunk);
                    i += 1;
                    if (i % 5 == 0 && callback != null) {
                        callback.onProgress(i, numMessages);
                    }

                    SystemClock.sleep(50); // don't ask :)
                }
                if (callback != null)
                    callback.onSaved();

            }
        });
    }

    @OnClick(R.id.widget_background_color)
    public void openWidgetBackgroundColorDialog() {
        createColorPickerDialog(bindings.dashBackgroundColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setDashBackgroundColor(selectedColor);
            }
        });
    }


    @OnClick(R.id.text_color_setting_item)
    public void openTextColorDialog() {
        createColorPickerDialog(bindings.textColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setTextColor(selectedColor);
            }
        });
    }

    @OnClick(R.id.widget_color_setting_item)
    public void openWidgetColorDialog() {
        createColorPickerDialog(bindings.widgetColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setWidgetColor(selectedColor);
            }
        });
    }

    @Override
    public void onResume() {
        final MainActivity activity = (MainActivity) getActivity();

        activity.setDrawerItemChecked(MainActivity.NAV_VIEW_OPTIONS_THEME_ITEM);
        super.onResume();
    }
}
