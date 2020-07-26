package com.example.weather.activites;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.weather.models.SharedPref;
import com.example.weather.R;

public class SettingActivity extends AppCompatActivity {

    RadioButton rbLight, rbDark, rbSetByBatterySaver;
    Button btnConfirm;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mapping();
        setCheckedRBTheme();
        selectTheme();
    }

    private void setCheckedRBTheme() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            rbDark.setChecked(true);
        } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            rbLight.setChecked(true);
        } else {
            rbSetByBatterySaver.setChecked(true);
        }
    }

    public void selectTheme() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rbDark.isChecked()) {
                    sharedPref.setDarkThemeState(true);
                    sharedPref.setLightThemeState(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(SettingActivity.this,
                            String.format("%s %s", getString(R.string.str_chosen), rbDark.getText()), Toast.LENGTH_SHORT).show();
                } else if (rbLight.isChecked()) {
                    sharedPref.setDarkThemeState(false);
                    sharedPref.setLightThemeState(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(SettingActivity.this,
                            String.format("%s %s", getString(R.string.str_chosen), rbLight.getText()), Toast.LENGTH_SHORT).show();
                } else {
                    sharedPref.setDarkThemeState(false);
                    sharedPref.setLightThemeState(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    Toast.makeText(SettingActivity.this,
                            String.format("%s %s", getString(R.string.str_chosen), rbSetByBatterySaver.getText()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void mapping() {
        rbLight = findViewById(R.id.rb_light);
        rbDark = findViewById(R.id.rb_dark);
        rbSetByBatterySaver = findViewById(R.id.rb_set_by_battery_saver);
        btnConfirm = findViewById(R.id.btn_confirm);
        sharedPref = new SharedPref(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}