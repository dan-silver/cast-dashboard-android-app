package com.silver.dan.castdemo.settingsFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.silver.dan.castdemo.MainActivity;
import com.silver.dan.castdemo.R;
import com.silver.dan.castdemo.SettingEnums.WeatherType;
import com.silver.dan.castdemo.WidgetOption;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherSettings extends WidgetSettingsFragment {

    public static String WEATHER_LAT = "WEATHER_LAT";
    public static String WEATHER_LNG = "WEATHER_LNG";
    public static String WEATHER_CITY = "WEATHER_CITY";
    public static String WEATHER_UNITS = "WEATHER_UNITS";
    public static String WEATHER_TYPE = "WEATHER_TYPE";

    // @todo remove WEATHER_CITY and calculate it from lat/lng

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    WidgetOption weatherLat;
    WidgetOption weatherLng;
    WidgetOption weatherCity;
    WidgetOption weatherTempUnits;
    WidgetOption weatherTypeOption;

    @BindView(R.id.weather_city)
    TwoLineSettingItem sWeatherCity;

    @BindView(R.id.weather_degrees_unit)
    TwoLineSettingItem tempUnits;

    @BindView(R.id.weather_type)
    TwoLineSettingItem weatherType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_settings, container, false);
        ButterKnife.bind(this, view);

        weatherLng = loadOrInitOption(WEATHER_LNG);
        weatherLat = loadOrInitOption(WEATHER_LAT);
        weatherCity = loadOrInitOption(WEATHER_CITY);
        weatherTempUnits = loadOrInitOption(WEATHER_UNITS);
        weatherTypeOption = loadOrInitOption(WEATHER_TYPE);

        sWeatherCity.setSubHeaderText(weatherCity.value);

        updateWeatherUnitsTextView();
        updateWeatherModeTextView();

        return view;
    }

    @OnClick(R.id.weather_degrees_unit)
    public void showTemperatureUnitsCallback() {
        new MaterialDialog.Builder(getContext())
                .title(R.string.temperature_units)
                .items(R.array.temperature_units_list)
                .itemsCallbackSingleChoice(weatherTempUnits.getIntValue(), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        weatherTempUnits.update(which);
                        updateWeatherUnitsTextView();
                        updateWidgetProperty("units", weatherTempUnits.getIntValue());
                        return true;
                    }
                })
                .show();
    }


    @OnClick(R.id.weather_type)
    public void weatherTypeChangeCallback() {
        final ArrayList<WeatherType> weatherTypes = new ArrayList<WeatherType>() {{
            add(WeatherType.TODAY);
            add(WeatherType.FIVE_DAY);
        }};

        final WeatherType current = WeatherType.getEnumFromInt(weatherTypeOption.getIntValue());

        new MaterialDialog.Builder(getContext())
                .title(R.string.weather_mode)
                .items(R.array.weather_type_list)
                .itemsCallbackSingleChoice(weatherTypes.indexOf(current), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        weatherTypeOption.update(weatherTypes.get(which).getValue());
                        updateWeatherModeTextView();
                        updateWidgetProperty(WEATHER_TYPE, weatherTypeOption.getIntValue());
                        return true;
                    }
                })
                .show();
    }

    private void updateWeatherUnitsTextView() {
        tempUnits.setSubHeaderText(getResources().getStringArray(R.array.temperature_units_list)[weatherTempUnits.getIntValue()]);
    }

    private void updateWeatherModeTextView() {
        weatherType.setSubHeaderText(getResources().getStringArray(R.array.weather_type_list)[weatherTypeOption.getIntValue()]);
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


                weatherLat.update(place.getLatLng().latitude);
                weatherLng.update(place.getLatLng().longitude);
                weatherCity.update(place.getName().toString());

                sWeatherCity.setSubHeaderText(weatherCity.value);

                // don't change the order of the following two lines
                updateWidgetProperty("lat", weatherLat.value);
                updateWidgetProperty("lng", weatherLng.value);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.i(MainActivity.TAG, status.getStatusMessage());

            }
        }
    }
}