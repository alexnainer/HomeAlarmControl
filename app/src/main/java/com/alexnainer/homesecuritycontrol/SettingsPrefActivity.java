package com.alexnainer.homesecuritycontrol;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsPrefActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsPrefActivity.class.getSimpleName();
    private static String restOfSummaryNumberPickerPlural;
    private static String restOfSummaryNumberPickerSingular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        restOfSummaryNumberPickerPlural = getString(R.string.connection_attempts_summary_plural);
        restOfSummaryNumberPickerSingular = getString(R.string.connection_attempts_summary_singular);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
