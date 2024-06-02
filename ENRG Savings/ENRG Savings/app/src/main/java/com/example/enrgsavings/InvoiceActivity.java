package com.example.enrgsavings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activity class that displays invoices filtered by energy type and user category (domestic or business).
 * Provides options to sort and filter invoice data based on month and sorting preferences.
 */
public class InvoiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InvoiceAdapter adapter;
    private List<InvoiceData> invoiceDataList;
    private Spinner monthSpinner;
    private Spinner sortSpinner;
    private int radioGroupEnergyCheckedId;
    private int radioGroupDombusiCheckedId;

    /**
     * Sets up the activity interface, initializes UI components, and prepares data display.
     *
     * @param savedInstanceState Bundle for restoring state if the activity is re-initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_gradient));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        monthSpinner = findViewById(R.id.spinner_month);
        setupMonthSpinner();
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        sortSpinner = findViewById(R.id.spinner_sort);
        setupSortSpinner();

        radioGroupEnergyCheckedId = getIntent().getIntExtra("radioGroupEnergyCheckedId", -1);
        radioGroupDombusiCheckedId = getIntent().getIntExtra("radioGroupDombusiCheckedId", -1);

        displayInvoiceData(currentMonth, "");
    }

    /**
     * Handles action bar item clicks, including the home (up) button.
     *
     * @param item The menu item that was selected.
     * @return true if the home button was selected, false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configures the month selection spinner and sets its item selection listener.
     */
    private void setupMonthSpinner() {
        ArrayAdapter<CharSequence> monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                displayInvoiceData(position, sortSpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Configures the sorting options spinner and sets its item selection listener.
     */
    private void setupSortSpinner() {
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                displayInvoiceData(Calendar.getInstance().get(Calendar.MONTH), selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Displays invoice data based on the selected month and sorting option.
     * It adjusts the display according to user selections on energy type and user category.
     *
     * @param selectedMonth The month for which invoices should be displayed.
     * @param sortingOption The sorting option to organize invoice display.
     */
    private void displayInvoiceData(int selectedMonth, String sortingOption) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        invoiceDataList = new ArrayList<>();

        if (radioGroupEnergyCheckedId == R.id.radio_electricity && radioGroupDombusiCheckedId == R.id.radio_domestic) {
            invoiceDataList = databaseHelper.getDataFromElectricityHome(selectedMonth + 1, sortingOption);
            getSupportActionBar().setTitle(getString(R.string.electricity) + " - " + getString(R.string.domestic));
        } else if (radioGroupEnergyCheckedId == R.id.radio_electricity && radioGroupDombusiCheckedId == R.id.radio_business) {
            invoiceDataList = databaseHelper.getDataFromElectricityBusiness(selectedMonth + 1, sortingOption);
            getSupportActionBar().setTitle(getString(R.string.electricity) + " - " + getString(R.string.business));
        } else if (radioGroupEnergyCheckedId == R.id.radio_gas && radioGroupDombusiCheckedId == R.id.radio_domestic) {
            getSupportActionBar().setTitle(getString(R.string.gas) + " - " + getString(R.string.domestic));
        } else if (radioGroupEnergyCheckedId == R.id.radio_gas && radioGroupDombusiCheckedId == R.id.radio_business) {
            getSupportActionBar().setTitle(getString(R.string.gas) + " - " + getString(R.string.business));
        }

        if (invoiceDataList.isEmpty()) {
            findViewById(R.id.textview_no_data).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.textview_no_data).setVisibility(View.GONE);
        }

        adapter = new InvoiceAdapter(invoiceDataList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Enables or disables user interaction with the activity based on the specified flag.
     *
     * @param enabled true to enable interaction, false to disable it.
     */
    public void setEnabled(boolean enabled) {
        getWindow().setFlags(enabled ? WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL : WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}