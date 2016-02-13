package com.example.dan.castdemo.settingsFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dan.castdemo.CastCommunicator;
import com.example.dan.castdemo.MainActivity;
import com.example.dan.castdemo.R;
import com.example.dan.castdemo.Widget;
import com.example.dan.castdemo.WidgetOption;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapSettings extends WidgetSettingsFragment implements GoogleApiClient.OnConnectionFailedListener {

    public static String LOCATION_LAT = "LOCATION_LAT";
    public static String LOCATION_LONG = "LOCATION_LONG";
    public static String LOCATION_NAME = "LOCATION_NAME";
    public static String LOCATION_ADDRESS = "LOCATION_ADDRESS";
    public static String MAP_ZOOM = "MAP_ZOOM";

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    WidgetOption locationLat;
    WidgetOption locationLong;
    WidgetOption locationNameOption;
    WidgetOption locationAddrOption;
    WidgetOption mapZoomOption;

    @Bind(R.id.location_name)
    TextView locationName;

    @Bind(R.id.location_addr)
    TextView locationAddr;

    @Bind(R.id.map_zoom)
    SeekBar mapZoom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_settings, container, false);
        ButterKnife.bind(this, view);

        locationLat = widget.getOption(MapSettings.LOCATION_LAT);
        locationLong = widget.getOption(MapSettings.LOCATION_LONG);
        locationNameOption = widget.getOption(MapSettings.LOCATION_NAME);
        locationAddrOption = widget.getOption(MapSettings.LOCATION_ADDRESS);
        mapZoomOption = widget.getOption(MapSettings.MAP_ZOOM);

        updateLocationText();


        mapZoom.setProgress(Integer.parseInt(mapZoomOption.value)-1);
        mapZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) return;
                mapZoomOption.value = String.valueOf(progress+1);
//                refreshWidget();
                mapZoomOption.save();
                CastCommunicator.sendWidgetProperty(widget.id, "zoom", mapZoomOption.value);
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

    public void updateLocationText() {
        locationName.setText(locationNameOption.value);
        locationAddr.setText(locationAddrOption.value);
    }

    public static void init(Widget widget) {
        widget.initOption(LOCATION_LAT, "47.6166143");
        widget.initOption(LOCATION_LONG, "-122.6558899");
        widget.initOption(LOCATION_NAME, "Somewhere I forgot"); //@todo
        widget.initOption(LOCATION_ADDRESS, "line 2"); //@todo
        widget.initOption(MAP_ZOOM, "5");
    }

    @OnClick(R.id.get_map_location)
    public void getLocation() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
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

                locationLat.value = Double.toString(location.latitude);
                locationLong.value = Double.toString(location.longitude);
                locationNameOption.value = place.getName().toString();
                locationAddrOption.value = place.getAddress().toString();

                updateLocationText();

                locationLat.save();
                locationLong.save();
                locationNameOption.save();
                locationAddrOption.save();
                refreshWidget();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
