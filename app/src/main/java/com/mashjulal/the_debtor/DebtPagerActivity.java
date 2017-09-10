package com.mashjulal.the_debtor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import static com.mashjulal.the_debtor.MainActivity.POSITION;
import static com.mashjulal.the_debtor.TheDebtorUtils.removeFile;
import static com.mashjulal.the_debtor.database.DebtsDBSchema.DebtTable.Columns.ID;

public class DebtPagerActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Debt> debts;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_pager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_debt_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        debts = DebtLab.getInstance(this).getDebts();
        viewPager = (ViewPager) findViewById(R.id.activity_debt_pager_view_pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Debt debt = debts.get(position);
                return DebtFragment.newInstance(debt);
            }

            @Override
            public int getCount() {
                return debts.size();
            }
        });

        if (getIntent().getExtras() != null){
            int i = getIntent().getIntExtra(POSITION, -1);
            if (i != -1)
                viewPager.setCurrentItem(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_debt_profile, menu);
        return true;
    }

    public void edit(MenuItem menuItem){
        Intent intent = new Intent(this, EditDebtActivity.class);
        intent.putExtra("EDIT_DEBT", true);
        intent.putExtra(ID, debts.get(viewPager.getCurrentItem()).getId());

        startActivityForResult(intent, 10);
    }

    public void returnDebt(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Распрощаться с долгом?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = viewPager.getCurrentItem();
                        Debt debt = debts.get(i);
                        if (debt.getPhotoPath() != null){
                            removeFile(debt.getPhotoPath(), null);
                        }

                        DebtLab.getInstance(getApplicationContext()).removeDebt(debt.getId());
                        NotificationReceiver.replaceAlarm(getApplicationContext(), debt);
                        finish();
                    }
                })
                .setNegativeButton("Нет", null);
        builder.show();
    }
}
