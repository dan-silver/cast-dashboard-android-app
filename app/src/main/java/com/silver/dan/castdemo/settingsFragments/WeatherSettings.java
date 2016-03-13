package com.silver.dan.castdemo.settingsFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.silver.dan.castdemo.MainActivity;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.Widget;
import com.silver.dan.castdemo.WidgetOption;
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
    public static String WEATHER_UNITS = "WEATHER_UNITS";

    // @todo remove WEATHER_CITY and calculate it from lat/lng

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    WidgetOption weatherLat;
    WidgetOption weatherLng;
    WidgetOption weatherCity;
    WidgetOption weatherTempUnits;

    @Bind(R.id.weather_city)
    TwoLineSettingItem sWeatherCity;

    @Bind(R.id.weather_degrees_unit)
    TwoLineSettingItem tempUnits;

    String weatherUnitsText[] = new String[]{"Fahrenheit", "Celsius"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_settings, container, false);
        ButterKnife.bind(this, view);

        weatherLng         = loadOrInitOption(WEATHER_LNG);
        weatherLat         = loadOrInitOption(WEATHER_LAT);
        weatherCity        = loadOrInitOption(WEATHER_CITY);
        weatherTempUnits   = loadOrInitOption(WEATHER_UNITS);

        sWeatherCity.setHeaderText("Location");
        sWeatherCity.setSubHeaderText(getNameFromCoordinates(getContext(), widget));

        tempUnits.setHeaderText("Temperature Units");
        updateWeatherUnitsTextView();

        return view;
    }

    @OnClick(R.id.weather_degrees_unit)
    public void showTemperatureUnitsCallback() {
        new MaterialDialog.Builder(getContext())
                .title("Calendar Duration")
                .items(weatherUnitsText)
                .itemsCallbackSingleChoice(weatherTempUnits.getIntValue(), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        weatherTempUnits.setIntValue(which);
                        weatherTempUnits.save();
                        updateWeatherUnitsTextView();
                        updateWidgetProperty("units", weatherTempUnits.getIntValue());
                        return true;
                    }
                })
                .show();
    }

    private void updateWeatherUnitsTextView() {
        tempUnits.setSubHeaderText(weatherUnitsText[weatherTempUnits.getIntValue()]);
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

                sWeatherCity.setSubHeaderText(getNameFromCoordinates(getContext(), widget));
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
