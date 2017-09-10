package com.mashjulal.the_debtor;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import static com.mashjulal.the_debtor.TheDebtorUtils.getCurrencyStringFrom;
import static com.mashjulal.the_debtor.TheDebtorUtils.getStringDateFrom;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.ID;


class TheDebtorNotification {

    private static final String NOTIFICATION_TAG = "TheDebtor";

    static void notify(final Context context, final Long id) {
        final Resources res = context.getResources();

        final Debt debt = DebtLab.getInstance(context).getDebt(id);
        Uri photoPath = debt.getPhotoPath();

        Bitmap picture = null;
        switch (debt.getType()){
            case "Вещь":
                if (photoPath == null)
                    picture = BitmapFactory.decodeResource(res, R.mipmap.ic_thing);
                else
                    picture = BitmapFactory.decodeFile(photoPath.getPath());
                break;
            case "Сумма":
                picture = BitmapFactory.decodeResource(res, R.mipmap.ic_money);
        }
        String debtName = (debt.getType().equals("Вещь")) ? debt.getThingName() :
                getCurrencyStringFrom(debt.getMoneyAmount());

        final String title = res.getString(R.string.notification_title_template, debtName);
        final String text = res.getString(R.string.notification_text_template,
                debt.getRecipientName(), getStringDateFrom(debt.getReturnDate()));


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)

                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(picture)


                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, DebtFragment.class).putExtra(ID, id),
                                PendingIntent.FLAG_ONE_SHOT))
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }
}
