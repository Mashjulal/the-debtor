package com.mashjulal.the_debtor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.mashjulal.the_debtor.TheDebtorUtils.getCurrencyStringFrom;
import static com.mashjulal.the_debtor.TheDebtorUtils.getStringDateFrom;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.ID;


public class DebtFragment extends Fragment {

    private Debt debt;
    private TextView
            tvBorrowDate,
            tvReturnDate,
            tvRecipientName,
            tvDescription,
            tvThingName,
            tvMoneyAmount;

    private LinearLayout
            llMoneyType,
            llThingType,
            llDescription,
            llReturnDate;

    private ImageView
            ivPhoto;


    public DebtFragment() {
        // Required empty public constructor
    }
    
    public static DebtFragment newInstance(Debt debt) {
        DebtFragment fragment = new DebtFragment();
        Bundle args = new Bundle();
        args.putLong(ID, debt.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            debt = DebtLab.getInstance(getContext()).getDebt(getArguments().getLong(ID));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        debt = DebtLab.getInstance(getContext()).getDebt(debt.getId());
        populateFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_debt_profile, container, false);

        tvBorrowDate = (TextView) v.findViewById(R.id.fragment_debt_profile_tv_borrow_date);
        tvReturnDate = (TextView) v.findViewById(R.id.fragment_debt_profile_tv_return_date);
        tvRecipientName = (TextView) v.findViewById(R.id.fragment_debt_profile_tv_recipient_name);
        tvDescription = (TextView) v.findViewById(R.id.fragment_debt_profile_tv_description);
        tvThingName = (TextView) v.findViewById(R.id.fragment_debt_profile_tv_thing_name);
        tvMoneyAmount = (TextView) v.findViewById(R.id.fragment_debt_profile_tv_money_amount);

        llMoneyType = (LinearLayout) v.findViewById(R.id.fragment_debt_profile_ll_money_type);
        llThingType = (LinearLayout) v.findViewById(R.id.fragment_debt_profile_ll_thing_type);
        llDescription = (LinearLayout) v.findViewById(R.id.fragment_debt_profile_ll_description);
        llReturnDate = (LinearLayout) v.findViewById(R.id.fragment_debt_profile_ll_return_date);

        ivPhoto = (ImageView) v.findViewById(R.id.fragment_debt_profile_iv_photo);

        populateFragment();
        
        return v;
    }

    private void populateFragment(){
        tvBorrowDate.setText(getStringDateFrom(debt.getBorrowDate()));
        tvRecipientName.setText(debt.getRecipientName());


        if (debt.getReturnDate() != -1L)
            tvReturnDate.setText(getStringDateFrom(debt.getReturnDate()));
        else
            llReturnDate.setVisibility(View.GONE);

        if (!debt.getDescription().equals(""))
            tvDescription.setText(debt.getDescription());
        else
            llDescription.setVisibility(View.GONE);

        switch (debt.getType()) {
            case "Вещь":
                llMoneyType.setVisibility(View.GONE);
                Uri uriPhotoPath = debt.getPhotoPath();
                if (uriPhotoPath != null) {
                    ivPhoto.setImageURI(uriPhotoPath);
                    ivPhoto.setOnClickListener(onImageClick);
                }
                tvThingName.setText(debt.getThingName());
                break;
            case "Сумма":
                llThingType.setVisibility(View.GONE);
                tvMoneyAmount.setText(getCurrencyStringFrom(debt.getMoneyAmount()));
                break;
        }
    }

    private View.OnClickListener onImageClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(debt.getPhotoPath(), "image/*");
            startActivity(intent);
        }
    };
}
