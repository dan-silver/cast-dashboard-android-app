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
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.silver.dan.castdemo.SettingEnums.BackgroundType;
import com.silver.dan.castdemo.databinding.FragmentAppSettingsThemeBinding;
import com.silver.dan.castdemo.settingsFragments.TwoLineSettingItem;
import com.silver.dan.castdemo.util.ImageUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

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
                        BackgroundType oldBackgroundType = bindings.getBackgroundType();
                        BackgroundType newBackgroundType = backgroundTypes.get(which);

                        if (oldBackgroundType != newBackgroundType) {
                            // remove old background
                            if (oldBackgroundType == BackgroundType.PICTURE) {
                                removeBackgroundPictureFromS3();
                            }

                            // first time selecting a background
                            if (newBackgroundType == BackgroundType.PICTURE && (bindings.backgroundImageLocalPath == null || bindings.backgroundImageLocalPath.isEmpty())) {
                                getBackgroundImage();
                            } else {
                                bindings.setBackgroundType(newBackgroundType);
                                if (newBackgroundType == BackgroundType.PICTURE && bindings.backgroundImageLocalPath != null && !bindings.backgroundImageLocalPath.isEmpty()) {
                                    new LoadImageTask().execute(bindings.backgroundImageLocalPath);
                                }
                            }
                        }

                        return true;
                    }


                })
                .show();
    }

    private void removeBackgroundPictureFromS3() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                MainActivity.s3Client.deleteObject(getString(R.string.BACKGROUND_PICTURE_BUCKET), bindings.backgroundImageName);
            }
        });
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

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        final String picturePath = cursor.getString(columnIndex);
                        cursor.close();


                        long imageSize = new File(picturePath).length();
                        Bitmap bm = BitmapFactory.decodeFile(picturePath);
                        try {
                            bm = ImageUtils.modifyOrientation(bm, picturePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // now that we should have the picture, go ahead and change the UI bindings to show the picture option
                        bindings.setBackgroundType(BackgroundType.PICTURE);

                        Drawable d = new BitmapDrawable(getResources(), bm);
                        backgroundPicture.setImageDrawable(d);

                        final String imageName = UUID.randomUUID().toString();
                        bindings.setBackgroundImageName(imageName);
                        bindings.setBackgroundImageLocalPath(picturePath);


                        // start with indeterminate as we're reading the bitmap, then determinate as upload progresses
                        uploadProgressBar.setIndeterminate(true);
                        uploadProgressBar.setVisibility(View.VISIBLE);


                        //upload to s3 async
                        uploadImageToS3(imageName, bm, imageSize, new FileSavedListener() {
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

                                // send secure image URL to receiver
                                bindings.appSettings.mCallback.onSettingChanged(AppSettingsBindings.SECURE_BACKGROUND_URL,
                                        ImageUtils.getS3ImageURL(imageName, getContext(), MainActivity.s3Client));
                            }

                            @Override
                            public void onError(final String err) {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        resetUI();
                                        Toast.makeText(getActivity(), err, Toast.LENGTH_LONG).show();
                                        bindings.setBackgroundImageName(null);
                                        bindings.setBackgroundImageLocalPath(null);
                                        bindings.setBackgroundType(BackgroundType.SLIDESHOW);
                                    }
                                });
                            }
                        });
                    }
                }
        }
    }

    private void uploadImageToS3(final String imageName, final Bitmap image, final long imageSize, final FileSavedListener callback) {
        // start the async upload to S3
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
                final byte[] bitmapdata = bos.toByteArray();
                final ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(bitmapdata.length);

                try {
                    PutObjectRequest por = new PutObjectRequest(getString(R.string.BACKGROUND_PICTURE_BUCKET), imageName, bs, metadata);
                    por.setGeneralProgressListener(new ProgressListener() {
                        @Override
                        public void progressChanged(ProgressEvent progressEvent) {
                                    if(progressEvent.getEventCode() == ProgressEvent.STARTED_EVENT_CODE) {
                                        uploadProgressBar.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                uploadProgressBar.setProgress(0);
                                                uploadProgressBar.setIndeterminate(false);
                                                uploadProgressBar.setVisibility(View.VISIBLE);
                                                uploadProgressBar.setMax(bitmapdata.length);
                                            }
                                        });
                                    } else if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
                                        Log.v(MainActivity.TAG, "Upload complete");
                                        callback.onSaved();
                                    } else if (progressEvent.getEventCode() == 0) { // progress notification
                                        uploadProgressBar.incrementProgressBy((int) progressEvent.getBytesTransferred());
                                    }

                                    // regardless of how the upload was terminated, reset the UI
                                    if (progressEvent.getEventCode() == ProgressEvent.FAILED_EVENT_CODE ||
                                            progressEvent.getEventCode() == ProgressEvent.CANCELED_EVENT_CODE) {
                                        callback.onError("Upload failed, try again later.");
                                        try {
                                            bs.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                        }
                    });
                    MainActivity.s3Client.putObject(por);
                } catch (Exception e) {
                    callback.onError("Error uploading photo");
                }


            }
        });
    }

    @OnClick(R.id.widget_background_color)
    public void openWidgetBackgroundColorDialog() {
        createColorPickerDialog(bindings.widgetBackgroundColor, new ColorPickerClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int selectedColor, Integer[] integers) {
                bindings.setWidgetBackgroundColor(selectedColor);
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
