package com.silver.dan.castdemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.silver.dan.castdemo.SettingEnums.BackgroundType;
import com.silver.dan.castdemo.databinding.FragmentAppSettingsThemeBinding;
import com.silver.dan.castdemo.settingsFragments.TwoLineSettingItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppSettingsTheme extends AppSettingsHelperFragment {

    @BindView(R.id.background_type)
    TwoLineSettingItem backgroundType;

    @BindView(R.id.widget_transparency)
    SeekBar widgetTransparency;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings_theme, container, false);
        ButterKnife.bind(this, view);

        viewModel = FragmentAppSettingsThemeBinding.bind(view);
        bindings = MainActivity.dashboard.settings;
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

        return view;
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

    interface OnScopeGranted {
        void success();
    }


    @OnClick(R.id.albumName)
    protected void selectGooglePhotosAlbum() {
        AuthHelper.getGoogleAccessToken(getContext(), new SimpleCallback<String>() {
            @Override
            public void onComplete(String googleAccessToken) {

                CharSequence text = "Fetching your albums, one sec";
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                Ion.with(getContext())
                    .load("https://picasaweb.google.com/data/feed/api/user/default?alt=json")
                    .setHeader("Authorization", "Bearer " + googleAccessToken)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e != null) {
                                // todo error to firebase
                                CharSequence text = "Hit a problem finding your albums";
                                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            final List<String> titles = new ArrayList<>();
                            final List<String> albumIds = new ArrayList<>();

                            JsonArray albums = result.get("feed").getAsJsonObject().get("entry").getAsJsonArray();
                            for (int i=0;i<albums.size();i++) {
                                JsonObject album = albums.get(i).getAsJsonObject();
                                titles.add(album.get("title").getAsJsonObject().get("$t").getAsString());
                                albumIds.add(album.get("id").getAsJsonObject().get("$t").getAsString());
                            }

                            new MaterialDialog.Builder(getContext())
                                    .title(R.string.google_photos_album)
                                    .items(titles)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            bindings.setBackgroundGooglePhotosAlbum(titles.get(which), albumIds.get(which));
                                            bindings.setBackgroundType(BackgroundType.PICASA_ALBUM.getValue());
                                        }
                                    })
                                    .show();

                            // display dialog


                            // display dialog
                        }
                    });
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    OnScopeGranted scopeGrantedCallback;

    @OnClick(R.id.background_type)
    public void setBackgroundType() {
        // it should be safe to rearrange and add items to this list
        final ArrayList<BackgroundType> backgroundTypes = new ArrayList<BackgroundType>() {{
            add(BackgroundType.SLIDESHOW);
            add(BackgroundType.SOLID_COLOR);
            add(BackgroundType.PICASA_ALBUM);
        }};

        new MaterialDialog.Builder(getContext())
                .title(R.string.background)
                .items(R.array.backgroundTypeList)
                .itemsCallbackSingleChoice(backgroundTypes.indexOf(bindings.getBackgroundType()), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        BackgroundType newBackgroundType = backgroundTypes.get(which);

                        if (newBackgroundType.equals(BackgroundType.PICASA_ALBUM)) {
                            // check for pro

                            if (!BillingHelper.hasPurchased) {
                                ((MainActivity) getActivity()).upgrade();
                                return false;
                            }

                            // get permission
                            final String requiredScope = "https://picasaweb.google.com/data/feed/api/user/default";

                            scopeGrantedCallback = new OnScopeGranted() {
                                @Override
                                public void success() {
                                    selectGooglePhotosAlbum();
                                }

                            };

                            if (!AuthHelper.grantedScopes.contains(new Scope(requiredScope))) {
                                AuthHelper authHelper = new AuthHelper(getContext());

                                Set<Scope> scopes = new HashSet<>();
                                scopes.addAll(AuthHelper.grantedScopes);
                                scopes.add(new Scope(requiredScope));

                                GoogleApiClient mGoogleApiClient = new GoogleApiClient
                                        .Builder(getContext())
                                        .addApi(Auth.GOOGLE_SIGN_IN_API, authHelper.getGoogleGSO(scopes))
                                        .build();

                                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                                getActivity().startActivityForResult(signInIntent, AppSettingsTheme.PERMISSION_RESULT_CODE_GOOGLE_ALBUMS);
                            } else {
                                scopeGrantedCallback.success();
                            }



                        } else {
                            bindings.setBackgroundType(newBackgroundType.getValue());
                        }


                        return true;
                    }


                })
                .show();
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
