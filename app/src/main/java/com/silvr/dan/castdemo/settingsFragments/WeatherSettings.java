package com.silvr.dan.castdemo.settingsFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.silvr.dan.castdemo.MainActivity;
import com.silvr.dan.castdemo.R;
import com.silvr.dan.castdemo.Widget;
import com.silvr.dan.castdemo.WidgetOption;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherSettings extends WidgetSettingsFragment {

    public static String WEATHER_LAT = "WEATHER_LAT";
    public static String WEATHER_LNG = "WEATHER_LNG";
    public static String WEATHER_CITY = "WEATHER_CITY";

    // @todo shouldn't need to store WEATHER_CITY anymore

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    WidgetOption weatherLat;
    WidgetOption weatherLng;

    WidgetOption weatherCity;

    @Bind(R.id.weather_city)
    EditText tvWeatherCity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_settings, container, false);
        ButterKnife.bind(this, view);

        weatherLng = loadOrInitOption(WEATHER_LNG);
        weatherLat = loadOrInitOption(WEATHER_LAT);
        weatherCity = loadOrInitOption(WEATHER_CITY);


        tvWeatherCity.setText(getNameFromCoordinates(getContext(), widget));

        return view;
    }

    @OnClick(R.id.weather_city)
    public void selectCity() {

        //restrict to cities
        AutocompleteFilter cityFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(cityFilter)
                            .build(getActivity());
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


                weatherLat.value = String.valueOf(place.getLatLng().latitude);
                weatherLng.value = String.valueOf(place.getLatLng().longitude);
                weatherCity.value = place.getName().toString();

                weatherLat.save();
                weatherLng.save();
                weatherCity.save();

                tvWeatherCity.setText(getNameFromCoordinates(getContext(), widget));
                refreshWidget();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.i(MainActivity.TAG, status.getStatusMessage());

            }
        }
    }

    public static String getNameFromCoordinates(Context context, Widget widget) {
//        double lat = Double.parseDouble(widget.getOption(WEATHER_LAT).value);
//        double lng = Double.parseDouble(widget.getOption(WEATHER_LNG).value);
//
//        Geocoder gcd = new Geocoder(context, Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = gcd.getFromLocation(lat, lng, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (addresses != null && addresses.size() > 0) {
//            Address addr = addresses.get(0);
//            return addr.getAddressLine(addr.getMaxAddressLineIndex()-1);
//        }

        //fall back to the city name from the Places API
        return widget.getOption(WeatherSettings.WEATHER_CITY).value;
    }


}
