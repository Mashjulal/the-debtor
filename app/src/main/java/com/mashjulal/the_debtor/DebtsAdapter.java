package com.mashjulal.the_debtor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import static com.mashjulal.the_debtor.MainActivity.POSITION;
import static com.mashjulal.the_debtor.TheDebtorUtils.getCurrencyStringFrom;
import static com.mashjulal.the_debtor.TheDebtorUtils.getStringDateFrom;

class DebtsAdapter extends RecyclerView.Adapter<DebtsAdapter.ViewHolder> {
    private Context mContext;
    private List<Debt> mDebts;

    DebtsAdapter(Context context, List<Debt> debts) {
        mContext = context;
        mDebts = debts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.debt_card, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Debt debt = mDebts.get(position);

        holder.tvDebtType.setText(debt.getType());

        if (debt.getReturnDate() != -1L)
            holder.tvReturnDate.setText(getStringDateFrom(debt.getReturnDate()));
        else
            holder.llReturnDate.setVisibility(View.GONE);
        holder.tvRecipientName.setText(debt.getRecipientName());

        String debtType = debt.getType();

        holder.ivPhoto.setImageURI(null);

        switch (debtType){
            case "Вещь":
                Uri photo = debt.getPhotoPath();
                if (photo != null)
                    holder.ivPhoto.setImageURI(photo);
                else
                    holder.ivPhoto.setBackgroundResource(R.mipmap.ic_thing);
                holder.tvThingName.setText(debt.getThingName());
                break;
            case "Сумма":
                Double moneyAmount = debt.getMoneyAmount();
                holder.tvThingName.setText(getCurrencyStringFrom(moneyAmount));
                holder.ivPhoto.setBackgroundResource(R.mipmap.ic_money);
                break;
        }

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DebtPagerActivity.class);
                intent.putExtra(POSITION, holder.id);
                mContext.startActivity(intent);
            }});
    }

    @Override
    public int getItemCount() {
        return mDebts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

         TextView
                tvReturnDate,
                tvRecipientName,
                tvThingName,
                tvDebtType;

         LinearLayout
                llReturnDate;

         ImageView
                ivPhoto;

        int id;


        ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            tvReturnDate = (TextView) mView.findViewById(R.id.textReturnDate);
            tvRecipientName = (TextView) mView.findViewById(R.id.textRecipientName);
            tvThingName = (TextView) mView.findViewById(R.id.textThingNameOrMoneyAmount);
            tvDebtType = (TextView) mView.findViewById(R.id.textDebtType);
            llReturnDate = (LinearLayout) mView.findViewById(R.id.debt_card_ll_return_date);
            ivPhoto = (ImageView) mView.findViewById(R.id.debt_card_iv_photo);
        }

        void setOnClickListener(View.OnClickListener listener){
            mView.setOnClickListener(listener);
        }

        void setId(int position){
            id = position;
        }
    }
}
