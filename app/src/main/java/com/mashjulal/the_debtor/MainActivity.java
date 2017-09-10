package com.mashjulal.the_debtor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    public static final String POSITION = "position";

    private static final int PERMISSION_SUCCESS_CODE = 100;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private RecyclerView rvDebts;
    private View emptyView;
    private DebtsAdapter da;
    private List<Debt> debts;
    private DebtLab debtLab;
    private Toolbar toolbar;

    private boolean areAllPermissions;
    private SharedPreferences preferences;

    public static final String ARE_ALL_PERMISSIONS = "are_all_permissions";
    public static final String IS_FIRST_RUN = "is_first_run";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        areAllPermissions = preferences.getBoolean(ARE_ALL_PERMISSIONS, false);

        if (isFirstRun() && !areAllPermissions) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_SUCCESS_CODE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addDebtActivity = new Intent(MainActivity.this, EditDebtActivity.class);
                startActivity(addDebtActivity);
            }
        });

        debtLab = DebtLab.getInstance(this);
        debts = debtLab.getDebts();
        da = new DebtsAdapter(this, debts);

        rvDebts = (RecyclerView) findViewById(R.id.rv_debts);
        emptyView = findViewById(R.id.tv_empty_list);

        rvDebts.setAdapter(da);
        rvDebts.setLayoutManager(new LinearLayoutManager(this));

        populateList();
    }

    private boolean isFirstRun() {
        boolean isFirstRun = preferences.getBoolean(IS_FIRST_RUN, true);

        if (isFirstRun)
            preferences.edit().putBoolean(IS_FIRST_RUN, false).apply();
        return isFirstRun;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        populateList();
        super.onResume();
    }

    private void populateList(){
        debts.clear();
        da.notifyDataSetChanged();
        debts.addAll(debtLab.getDebts());
        if (debts.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            rvDebts.setVisibility(View.GONE);
        }
        else {
            emptyView.setVisibility(View.GONE);
            rvDebts.setVisibility(View.VISIBLE);
        }
        da.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_SUCCESS_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    preferences.edit().putBoolean(ARE_ALL_PERMISSIONS, true).apply();
        }
    }

    public void edit(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
