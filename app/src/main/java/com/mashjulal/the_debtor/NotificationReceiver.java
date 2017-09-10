package com.mashjulal.the_debtor;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.ID;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String START_TIME = "start_time";

    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TheDebtorNotification.notify(context, intent.getLongExtra(ID, -1L));

        if (intent.getLongExtra(START_TIME, -1L) > new Date().getTime()) {
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).cancel(pi);
        }
    }

    static void setupAlarm(Context context, Long id){
        Debt debt = DebtLab.getInstance(context).getDebt(id);
        Log.d("LOG", debt.getReturnDate().toString());
        Long returnDate = debt.getReturnDate();

        Long startMillis = returnDate - AlarmManager.INTERVAL_HALF_DAY - AlarmManager.INTERVAL_HOUR * 3;
        Long repeatMillis = AlarmManager.INTERVAL_HALF_DAY;

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(START_TIME, returnDate.toString());
        intent.putExtra(ID, id);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, startMillis, repeatMillis, pi);
    }

    static void replaceAlarm(Context context, Debt debt){
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(START_TIME, debt.getReturnDate().toString());
        intent.putExtra(ID, debt.getId());
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }
}
