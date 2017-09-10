package com.mashjulal.the_debtor;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;


public class SettingsActivity extends AppCompatPreferenceActivity{

    static final String NOTIFICATION = "notification";

    SwitchPreference spNotification;
    SharedPreferences prefs;

    private boolean isNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spNotification = (SwitchPreference) findPreference(NOTIFICATION);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        isNotify = prefs.getBoolean(NOTIFICATION, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (isNotify != spNotification.isChecked()){
            if (spNotification.isChecked()){
                for (Debt debt: DebtLab.getInstance(getApplicationContext()).getDebts())
                    NotificationReceiver.setupAlarm(getApplicationContext(), debt.getId());
            }
            else
                am.cancel(pi);
        }
        super.onDestroy();
    }
}
