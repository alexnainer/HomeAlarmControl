package com.alexnainer.homealarmcontrol;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsPrefActivity.class.getSimpleName();
    private static String restOfSummaryNumberPickerPlural;
    private static String restOfSummaryNumberPickerSingular;
    private Toolbar toolbar;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < 23) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.grey_700));
            getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.background_light));
        }

        getLayoutInflater().inflate(R.layout.toolbar, (ViewGroup)findViewById(android.R.id.content));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setElevation(8);


        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("");


        restOfSummaryNumberPickerPlural = getString(R.string.connection_attempts_summary_plural);
        restOfSummaryNumberPickerSingular = getString(R.string.connection_attempts_summary_singular);


        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();



    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);



            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_ip_address)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_password)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_connection_attempts)));





        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        if (preference instanceof NumberPickerPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), 3));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

    }


    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            String restOfSummary;
            if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("key_ip_address")) {
                    preference.setSummary(newValue.toString());
                }
            } else if (preference instanceof NumberPickerPreference) {
                if ((int) newValue == 1){
                    restOfSummary = restOfSummaryNumberPickerSingular;
                } else {
                    restOfSummary = restOfSummaryNumberPickerPlural;
                }
                preference.setSummary(String.valueOf(newValue) + " " + restOfSummary);
            }
            return true;
        }
    };

}
