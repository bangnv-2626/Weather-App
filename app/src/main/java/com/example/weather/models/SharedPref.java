package com.example.weather.models;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    SharedPreferences mSharedPref;

    public SharedPref(Context context) {
        mSharedPref = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
    }

    //Dark Theme
    //Save the DarkTheme State:
    public void setDarkThemeState(Boolean state) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean("DarkTheme", state);
        editor.apply();
    }

    //Load DarkTheme State
    public Boolean loadDarkThemeState() {
        Boolean state = mSharedPref.getBoolean("DarkTheme", false);
        return state;
    }

    //Light Theme
    //Save the LightTheme State:
    public void setLightThemeState(Boolean state) {
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean("LightTheme", state);
        editor.apply();
    }

    //Load DarkTheme State
    public Boolean loadLightThemeState() {
        Boolean state = mSharedPref.getBoolean("LightTheme", false);
        return state;
    }

}
