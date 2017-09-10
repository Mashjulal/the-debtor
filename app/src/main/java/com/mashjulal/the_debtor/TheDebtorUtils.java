package com.mashjulal.the_debtor;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import static com.mashjulal.the_debtor.TheDebtorUtils.TheDebtorConst.CURRENT_CURRENCY;
import static com.mashjulal.the_debtor.TheDebtorUtils.TheDebtorConst.CURRENT_LOCALE;
import static com.mashjulal.the_debtor.TheDebtorUtils.TheDebtorConst.SDF;


class TheDebtorUtils {

    static final class TheDebtorConst {
        static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");
        static final Locale CURRENT_LOCALE = Locale.getDefault();
        static final Currency CURRENT_CURRENCY = Currency.getInstance(CURRENT_LOCALE);
    }

    static Long getDateMillisFrom(String strDate){
        Long millis = -1L;
        try {
            if (!strDate.equals("Бессрочно"))
                millis = SDF.parse(strDate).getTime();
        }
        catch (ParseException e){}

        return millis;
    }

    static String getStringDateFrom(Long millis){
        String strDate ="Бессрочно";
        if (millis != -1L) {
            Date date = new Date(millis);
            strDate = SDF.format(date);
        }

        return strDate;
    }

    static String getCurrencyStringFrom(Double d){
        return NumberFormat.getCurrencyInstance(CURRENT_LOCALE).format(d);
    }

    static Double getDoubleFromCurrency(String currency){
        String dbl = currency.replaceAll("[," + CURRENT_CURRENCY.getSymbol() + "]", "");
        return (!dbl.equals("")) ? Double.valueOf(dbl) : null;
    }

    static void removeFile(Uri uri, Context context){
        if (uri != null){
            File file = new File(uri.getPath());
            try {
                file.getCanonicalFile().delete();
            }
            catch (IOException e){
                if (context != null)
                    Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    static Uri createUriPhotoPath(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "TheDebtorPhotos");
        if (!imagesFolder.exists())
            imagesFolder.mkdirs();

        String imageFileName = "IMG_" + timeStamp + ".png";
        File photoFile = new File(imagesFolder, imageFileName);

        return Uri.fromFile(photoFile);
    }

}
