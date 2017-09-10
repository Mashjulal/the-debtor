package com.mashjulal.the_debtor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mashjulal.the_debtor.R.id.activity_add_debt_et_thing_name;
import static com.mashjulal.the_debtor.TheDebtorUtils.TheDebtorConst.SDF;
import static com.mashjulal.the_debtor.TheDebtorUtils.createUriPhotoPath;
import static com.mashjulal.the_debtor.TheDebtorUtils.getCurrencyStringFrom;
import static com.mashjulal.the_debtor.TheDebtorUtils.getDateMillisFrom;
import static com.mashjulal.the_debtor.TheDebtorUtils.getDoubleFromCurrency;
import static com.mashjulal.the_debtor.TheDebtorUtils.getStringDateFrom;
import static com.mashjulal.the_debtor.TheDebtorUtils.removeFile;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.ID;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.PHOTO_PATH;

public class EditDebtActivity extends AppCompatActivity {

    private static final String PRESSED_BUTTON = "PRESSED_BUTTON";
    private static final String TIME_MILLIS = "TIME_MILLIS";

    private static final String EMPTY_FIELD_ALERT = "Заполните поле!";

    private static final int CODE_TAKE_PHOTO = 100;

    static Button
            bChangeBorrowDate,
            bChangeReturnDate;

    private RadioGroup
            rgDebtType;

    private LinearLayout
            ltTypeThing,
            ltTypeMoney;

    private EditText
            etDescription,
            etThingName,
            etMoneyAmount,
            etRecipientName;

    private ImageView
            ivPhotoPreview;

    private Uri
            uriCurrentPhotoPath = null;

    private DebtLab
            dl;

    private RadioGroup.OnCheckedChangeListener
            onChecked = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId){
                case R.id.activity_add_debt_rb_thing_type:
                    ltTypeMoney.setVisibility(View.GONE);
                    ltTypeThing.setVisibility(View.VISIBLE);
                    break;
                case R.id.activity_add_debt_rb_money_type:
                    ltTypeThing.setVisibility(View.GONE);
                    ltTypeMoney.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_debt);

        bChangeBorrowDate = (Button) findViewById(R.id.activity_add_debt_b_change_borrow_date);
        bChangeReturnDate = (Button) findViewById(R.id.activity_add_debt_b_change_return_date);

        rgDebtType = (RadioGroup) findViewById(R.id.activity_add_debt_rg_debt_type);
        rgDebtType.setOnCheckedChangeListener(onChecked);

        ltTypeThing = (LinearLayout) findViewById(R.id.activity_add_debt_ll_thing_type);
        ltTypeMoney = (LinearLayout) findViewById(R.id.activity_add_debt_ll_money_type);

        etMoneyAmount = (EditText) findViewById(R.id.activity_add_debt_et_money_amount);

        etDescription = (EditText) findViewById(R.id.activity_add_debt_et_description);
        etThingName = (EditText) findViewById(activity_add_debt_et_thing_name);
        etRecipientName = (EditText) findViewById(R.id.activity_add_debt_et_recipient_name);

        ivPhotoPreview = (ImageView) findViewById(R.id.activity_add_debt_iv_photo_preview);

        bChangeBorrowDate.setText(SDF.format(new Date()));

        dl = DebtLab.getInstance(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(PHOTO_PATH)){
            uriCurrentPhotoPath = Uri.parse(savedInstanceState.getString(PHOTO_PATH));
        }

        if (getIntent().getExtras() != null)
            setData();
    }

    private void setData(){
        Long id = getIntent().getExtras().getLong(ID);
        Debt debt = dl.getDebt(id);

        String type = debt.getType();
        String thingName = debt.getThingName();
        Uri photoPath = debt.getPhotoPath();
        Double moneyAmount = debt.getMoneyAmount();
        Long borrowDate = debt.getBorrowDate();
        Long returnDate = debt.getReturnDate();
        String description = debt.getDescription();
        String recipientName = debt.getRecipientName();

        switch (type){
            case "Вещь":
                rgDebtType.check(R.id.activity_add_debt_rb_thing_type);
                ltTypeThing.setVisibility(View.VISIBLE);
                ltTypeMoney.setVisibility(View.GONE);
                etThingName.setText(thingName);
                uriCurrentPhotoPath = photoPath;
                ivPhotoPreview.setImageURI(uriCurrentPhotoPath);
                break;
            case "Сумма":
                rgDebtType.check(R.id.activity_add_debt_rb_money_type);
                ltTypeMoney.setVisibility(View.VISIBLE);
                ltTypeThing.setVisibility(View.GONE);
                etMoneyAmount.setText(getCurrencyStringFrom(moneyAmount));
                break;
        }

        bChangeBorrowDate.setText(getStringDateFrom(borrowDate));
        bChangeReturnDate.setText(getStringDateFrom(returnDate));
        etDescription.setText(description);
        etRecipientName.setText(recipientName);
    }

    public void addDebt(View view){
        if (!(areFieldsFilled() && areDatesCorrect()))
            return;

        RadioButton rbCheckedId = (RadioButton) findViewById(rgDebtType.getCheckedRadioButtonId());

        String type = rbCheckedId.getText().toString();
        Long borrowDate = getDateMillisFrom(bChangeBorrowDate.getText().toString());
        Long returnDate = getDateMillisFrom(bChangeReturnDate.getText().toString());
        String description = etDescription.getText().toString();
        String recipientName = etRecipientName.getText().toString();
        String thingName = etThingName.getText().toString();
        Double moneyAmount = getDoubleFromCurrency(etMoneyAmount.getText().toString());

        Debt debt = new Debt(type, thingName, uriCurrentPhotoPath, moneyAmount,
                    borrowDate, returnDate, description, recipientName);

        String mes;
        if (getIntent().getExtras() == null) {
            dl.addDebt(debt);
            mes = "Долг успешно добавлен!";

            if (returnDate != -1L) {
                List<Debt> debts = DebtLab.getInstance(getApplicationContext()).getDebts();
                NotificationReceiver.setupAlarm(getApplicationContext(), debts.get(debts.size()-1).getId());
            }
        }
        else {
            debt.setId(getIntent().getLongExtra(ID, -1));
            dl.updateDebt(debt);
            mes = "Долг успешно изменен!";
        }
        Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
        finish();
    }

    private boolean areFieldsFilled() {
        RadioButton rbCheckedId = (RadioButton) findViewById(rgDebtType.getCheckedRadioButtonId());

        Boolean notEmpty = true;
        switch (rbCheckedId.getId()){
            case R.id.activity_add_debt_rb_thing_type:
                boolean thingNameIsEmpty = TextUtils.isEmpty(etThingName.getText().toString());
                if (thingNameIsEmpty)
                    etThingName.setError(EMPTY_FIELD_ALERT);
                notEmpty = !thingNameIsEmpty;
                break;
            case R.id.activity_add_debt_rb_money_type:
                boolean moneyAmountIsEmpty = TextUtils.isEmpty(etMoneyAmount.getText().toString());
                if (moneyAmountIsEmpty)
                    etMoneyAmount.setError(EMPTY_FIELD_ALERT);
                notEmpty = !moneyAmountIsEmpty;
                break;
        }
        boolean recipientNameIsEmpty = TextUtils.isEmpty(etRecipientName.getText().toString());
        if (recipientNameIsEmpty)
            etRecipientName.setError(EMPTY_FIELD_ALERT);
        notEmpty &= !recipientNameIsEmpty;

        return notEmpty;
    }

    private boolean areDatesCorrect(){
        Long borrowMillis = getDateMillisFrom(bChangeBorrowDate.getText().toString());
        Long returnMillis = getDateMillisFrom(bChangeReturnDate.getText().toString());
        boolean areCorrect = returnMillis == -1L || returnMillis >= borrowMillis;
        if (!areCorrect){
            bChangeBorrowDate.setError("Недопустимая дата");
        }

        return areCorrect;
    }

    public void changeBorrowDate(View v){
        DatePickerFragment fragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(PRESSED_BUTTON, "btnChangeBorrowDate");
        bundle.putLong(TIME_MILLIS, getDateMillisFrom(bChangeReturnDate.getText().toString()));
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), "borrowDatePicker");
    }

    public void changeReturnDate(View v){
        DatePickerFragment fragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(PRESSED_BUTTON, "btnChangeReturnDate");

        bundle.putLong(TIME_MILLIS, getDateMillisFrom(bChangeReturnDate.getText().toString()));
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(), "returnDatePicker");
    }

    public void makePhoto(View v){
        uriCurrentPhotoPath = createUriPhotoPath();
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uriCurrentPhotoPath);
        startActivityForResult(intentCamera, CODE_TAKE_PHOTO);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriCurrentPhotoPath != null)
            outState.putString(PHOTO_PATH, uriCurrentPhotoPath.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_TAKE_PHOTO) {
            switch (resultCode) {
                case RESULT_OK:
                    ivPhotoPreview.setImageURI(uriCurrentPhotoPath);
                    break;
                case RESULT_CANCELED:
                    new File(uriCurrentPhotoPath.getPath()).delete();
                    uriCurrentPhotoPath = null;
                    break;
            }
        }
    }

    public void showPhoto(View view) {
        if (uriCurrentPhotoPath != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uriCurrentPhotoPath, "image/*");
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        removeFile(uriCurrentPhotoPath, this);
        super.onBackPressed();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            try {
                String pressedButton = getArguments().getString(PRESSED_BUTTON);

                Date date = SDF.parse(String.format("%d.%d.%d", dayOfMonth, month + 1, year));
                String strDate = SDF.format(date);
                switch (pressedButton){
                    case "btnChangeBorrowDate":
                        bChangeBorrowDate.setText(strDate);
                        break;
                    case "btnChangeReturnDate":
                        bChangeReturnDate.setText(strDate);
                        break;
                }
            }
            catch (ParseException e){}
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

            return datePickerDialog;
        }
    }
}
