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
import android.widget.LinearLayout;
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
import com.silver.dan.castdemo.SettingEnums.MapMode;
import com.silver.dan.castdemo.SettingEnums.MapType;
import com.silver.dan.castdemo.SettingEnums.TravelMode;
import com.silver.dan.castdemo.WidgetOption;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapSettings extends WidgetSettingsFragment implements GoogleApiClient.OnConnectionFailedListener {

    public static String LOCATION_LAT = "LOCATION_LAT";
    public static String LOCATION_LONG = "LOCATION_LONG";
    public static String LOCATION_ADDRESS = "LOCATION_ADDRESS";
    public static String MAP_ZOOM = "MAP_ZOOM";
    public static String SHOW_TRAFFIC = "SHOW_TRAFFIC";
    public static String MAP_TYPE = "MAP_TYPE"; // hybrid, terrain, satellite, etc.
    public static String MAP_MODE = "MAP_MODE";
    public static String DESTINATION_LAT = "DESTINATION_LAT";
    public static String DESTINATION_LONG = "DESTINATION_LONG";
    public static String DESTINATION_TEXT = "DESTINATION_TEXT";
    public static String TRAVEL_MODE = "TRAVEL_MODE";

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION = 2;

    WidgetOption locationLat;
    WidgetOption locationLong;
    WidgetOption locationAddrOption;
    WidgetOption mapZoomOption;
    WidgetOption mapShowTraffic;
    WidgetOption mapTypeOption;
    WidgetOption mapModeOption;
    WidgetOption destinationLat;
    WidgetOption destinationLng;
    WidgetOption destinationText;
    WidgetOption travelModeOption;

    @BindView(R.id.map_location)
    TwoLineSettingItem mapLocation;

    @BindView(R.id.map_destination)
    TwoLineSettingItem mapDestination;

    @BindView(R.id.map_zoom)
    SeekBar mapZoom;

    @BindView(R.id.map_traffic)
    Switch mapTraffic;

    @BindView(R.id.map_type)
    TwoLineSettingItem mapType;

    @BindView(R.id.map_mode)
    TwoLineSettingItem mapMode;

    @BindView(R.id.map_travel_mode)
    TwoLineSettingItem travelMode;

    @BindView(R.id.map_directions_options)
    LinearLayout mapDirectionsOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    // this.widget must exist
    public void initView() {
        locationLat = loadOrInitOption(MapSettings.LOCATION_LAT);
        locationLong = loadOrInitOption(MapSettings.LOCATION_LONG);
        locationAddrOption = loadOrInitOption(MapSettings.LOCATION_ADDRESS);
        mapZoomOption = loadOrInitOption(MapSettings.MAP_ZOOM);
        mapShowTraffic = loadOrInitOption(MapSettings.SHOW_TRAFFIC);
        mapTypeOption = loadOrInitOption(MapSettings.MAP_TYPE);
        mapModeOption = loadOrInitOption(MapSettings.MAP_MODE);
        destinationLat = loadOrInitOption(MapSettings.DESTINATION_LAT);
        destinationLng = loadOrInitOption(MapSettings.DESTINATION_LONG);
        destinationText = loadOrInitOption(MapSettings.DESTINATION_TEXT);
        travelModeOption = loadOrInitOption(MapSettings.TRAVEL_MODE);


        updateLocationText();
        updateDestinationText();
        updateTypeText();
        mapModeChanged();
        updateTravelModeText();

        supportWidgetHeightOption();


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

    }

    private void updateTypeText() {
        MapType current = MapType.getMapType(mapTypeOption.getIntValue());
        mapType.setSubHeaderText(current.getHumanNameRes());
    }

    private void mapModeChanged() {
        MapMode current = MapMode.getMapMode(mapModeOption.getIntValue());
        mapMode.setSubHeaderText(current.getHumanNameRes());

        if (current == MapMode.STANDARD) {
            mapDirectionsOptions.setVisibility(View.GONE);
            mapLocation.setHeaderText(R.string.location);
        } else {
            mapDirectionsOptions.setVisibility(View.VISIBLE);
            mapLocation.setHeaderText(R.string.starting_point);
        }
    }

    private void updateTravelModeText() {
        TravelMode current = TravelMode.getMode(travelModeOption.getIntValue());
        travelMode.setSubHeaderText(current.getHumanNameRes());
    }

    public void updateLocationText() {
        mapLocation.setSubHeaderText(locationAddrOption.value);
    }

    public void updateDestinationText() {
        mapDestination.setSubHeaderText(destinationText.value);
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
                        //send the actual enum name for the gmaps api to consume
                        updateWidgetProperty(MapSettings.MAP_TYPE, mapTypes.get(which).toString());
                        return true;
                    }
                })
                .show();
    }

    @OnClick(R.id.map_mode)
    public void mapMode() {
        final ArrayList<MapMode> mapModes = new ArrayList<MapMode>() {{
            add(MapMode.STANDARD);
            add(MapMode.DIRECTIONS);
        }};

        final MapMode current = MapMode.getMapMode(mapModeOption.getIntValue());

        new MaterialDialog.Builder(getContext())
                .items(R.array.mapModeList)
                .itemsCallbackSingleChoice(mapModes.indexOf(current), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mapModeOption.update(mapModes.get(which).getValue());
                        mapModeChanged();
                        updateWidgetProperty(MapSettings.MAP_MODE, mapModes.get(which).getValue());
                        return true;
                    }
                })
                .show();
    }

    @OnClick(R.id.map_travel_mode)
    public void travelMode() {
        final ArrayList<TravelMode> travelModes = new ArrayList<TravelMode>() {{
            add(TravelMode.DRIVING);
            add(TravelMode.BICYCLING);
            add(TravelMode.WALKING);
            add(TravelMode.TRANSIT);
        }};

        final TravelMode current = TravelMode.getMode(travelModeOption.getIntValue());

        new MaterialDialog.Builder(getContext())
                .items(R.array.travelModeList)
                .itemsCallbackSingleChoice(travelModes.indexOf(current), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        travelModeOption.update(travelModes.get(which).getValue());
                        updateTravelModeText();
                        //send the actual enum name for the gmaps api to consume
                        updateWidgetProperty(MapSettings.TRAVEL_MODE, travelModes.get(which).toString());
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
            Log.e(MainActivity.TAG, e.toString());
        }
    }


    @OnClick(R.id.map_destination)
    public void getDestination() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(MainActivity.TAG, e.toString());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                Log.i(MainActivity.TAG, "Place: " + place.getName());
                LatLng location = place.getLatLng();

                locationLat.update(location.latitude);
                locationLong.update(location.longitude);
                locationAddrOption.update(place.getAddress().toString());

                updateLocationText();

                updateWidgetProperty(MapSettings.LOCATION_LAT, locationLat.value);
                updateWidgetProperty(MapSettings.LOCATION_LONG, locationLong.value);

            } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_DESTINATION) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);

                LatLng location = place.getLatLng();

                destinationLat.update(location.latitude);
                destinationLng.update(location.longitude);
                destinationText.update(place.getAddress().toString());

                updateDestinationText();

                updateWidgetProperty(MapSettings.DESTINATION_LAT, destinationLat.value);
                updateWidgetProperty(MapSettings.DESTINATION_LONG, destinationLng.value);
            }
        } else {
            Log.e(MainActivity.TAG, "Google Maps API error");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
