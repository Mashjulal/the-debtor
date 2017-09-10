package com.mashjulal.the_debtor;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.mashjulal.the_debtor.TheDebtorUtils.getCurrencyStringFrom;
import static com.mashjulal.the_debtor.TheDebtorUtils.getStringDateFrom;

class DebtAdapter extends ArrayAdapter<Debt> {

    DebtAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Debt> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Debt debt = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.debt_item, null);
        }

        ((TextView) convertView.findViewById(R.id.textDebtType)).setText(debt.getType());

        TextView tvReturnDate = (TextView) convertView.findViewById(R.id.textReturnDate);
        if (debt.getReturnDate() != -1L)
            tvReturnDate.setText(getStringDateFrom(debt.getReturnDate()));
        else
            convertView.findViewById(R.id.debt_item_ll_return_date).setVisibility(View.GONE);
        ((TextView) convertView.findViewById(R.id.textRecipientName)).setText(debt.getRecipientName());

        String debtType = debt.getType();

        ImageView iv = (ImageView) convertView.findViewById(R.id.imageDebtType);
        iv.setBackgroundResource(0);

        switch (debtType){
            case "Вещь":
                Uri photo = debt.getPhotoPath();
                if (photo != null)
                    iv.setImageURI(photo);
                else
                    iv.setBackgroundResource(R.mipmap.ic_thing);
                ((TextView) convertView.findViewById(R.id.textThingNameOrMoneyAmount)).setText(debt.getThingName());
                break;
            case "Сумма":
                Double moneyAmount = debt.getMoneyAmount();
                ((TextView) convertView.findViewById(R.id.textThingNameOrMoneyAmount)).setText(getCurrencyStringFrom(moneyAmount));
                iv.setBackgroundResource(R.mipmap.ic_money);
                break;
        }
        return convertView;
    }

}
