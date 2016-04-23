package com.silver.dan.castdemo.settingsFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.silver.dan.castdemo.MainActivity;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.SettingEnums.MapType;
import com.silver.dan.castdemo.WidgetOption;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapSettings extends WidgetSettingsFragment implements GoogleApiClient.OnConnectionFailedListener {

    public static String LOCATION_LAT = "LOCATION_LAT";
    public static String LOCATION_LONG = "LOCATION_LONG";
    public static String LOCATION_ADDRESS = "LOCATION_ADDRESS";
    public static String MAP_ZOOM = "MAP_ZOOM";
    public static String SHOW_TRAFFIC = "SHOW_TRAFFIC";
    public static String MAP_TYPE = "MAP_TYPE";


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    WidgetOption locationLat;
    WidgetOption locationLong;
    WidgetOption locationAddrOption;
    WidgetOption mapZoomOption;
    WidgetOption mapShowTraffic;
    WidgetOption mapTypeOption;

    @Bind(R.id.map_location)
    TwoLineSettingItem mapLocation;

    @Bind(R.id.map_zoom)
    SeekBar mapZoom;

    @Bind(R.id.map_traffic)
    Switch mapTraffic;

    @Bind(R.id.map_type)
    TwoLineSettingItem mapType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_settings, container, false);
        ButterKnife.bind(this, view);

        locationLat = loadOrInitOption(MapSettings.LOCATION_LAT);
        locationLong = loadOrInitOption(MapSettings.LOCATION_LONG);
        locationAddrOption = loadOrInitOption(MapSettings.LOCATION_ADDRESS);
        mapZoomOption = loadOrInitOption(MapSettings.MAP_ZOOM);
        mapShowTraffic = loadOrInitOption(MapSettings.SHOW_TRAFFIC);
        mapTypeOption = loadOrInitOption(MapSettings.MAP_TYPE);
        optionWidgetHeight = loadOrInitOption(WidgetSettingsFragment.WIDGET_HEIGHT);


        updateWidgetHeightText();
        updateLocationText();
        updateTypeText();


        mapZoom.setProgress(Integer.parseInt(mapZoomOption.value) - 1);
        mapZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                mapZoomOption.update(progress + 1);
                updateWidgetProperty(MapSettings.MAP_ZOOM, mapZoomOption.getIntValue());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mapTraffic.setChecked(mapShowTraffic.getBooleanValue());
        mapTraffic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mapShowTraffic.update(isChecked);
                updateWidgetProperty(MapSettings.SHOW_TRAFFIC, mapShowTraffic.getBooleanValue());
            }
        });

        return view;
    }

    private void updateTypeText() {
        MapType current = MapType.values()[mapTypeOption.getIntValue()];
        mapType.setSubHeaderText(current.getHumanNameRes());
    }

    public void updateLocationText() {
        mapLocation.setSubHeaderText(locationAddrOption.value);
    }

    @OnClick(R.id.map_type)
    public void mapType() {
        final ArrayList<MapType> mapTypes = new ArrayList<MapType>() {{
            add(MapType.ROADMAP);
            add(MapType.HYBRID);
            add(MapType.TERRAIN);
            add(MapType.SATELLITE);
        }};

        final MapType current = MapType.getMapType(mapTypeOption.getIntValue());

        new MaterialDialog.Builder(getContext())
                .items(R.array.mapTypeList)
                .itemsCallbackSingleChoice(mapTypes.indexOf(current), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mapTypeOption.update(mapTypes.get(which).getValue());
                        updateTypeText();
                        updateWidgetProperty(MapSettings.MAP_TYPE, mapTypes.get(which).toString());
                        return true;
                    }
                })
                .show();
    }

    @OnClick(R.id.map_location)
    public void getLocation() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                Log.i(MainActivity.TAG, "Place: " + place.getName());
                LatLng location = place.getLatLng();

                locationLat.update(location.latitude);
                locationLong.update(location.longitude);
                locationAddrOption.update(place.getAddress().toString());

                updateLocationText();

                updateWidgetProperty(MapSettings.LOCATION_LAT, locationLat.value);
                updateWidgetProperty(MapSettings.LOCATION_LONG, locationLong.value);

            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
