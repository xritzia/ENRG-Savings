package com.example.enrgsavings;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * The main activity class that initializes the user interface for the energy savings application.
 */
public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    RadioGroup radioGroupEnergy;
    RadioGroup radioGroupDombusi;
    private Button button;
    private ProgressBar progressBar;
    private TextView textviewScrapingProgress;

    /**
     * Initializes the activity. Sets up the UI and restores state from savedInstanceState if available.
     *
     * @param savedInstanceState Contains state data from onSaveInstanceState(Bundle) if the activity was previously terminated, otherwise null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTablet(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        progressBar = findViewById(R.id.progressBar);
        textviewScrapingProgress = findViewById(R.id.textview_scraping_progress);

        new DataScraper(this, progressBar, textviewScrapingProgress).execute();

        radioGroupEnergy = findViewById(R.id.radiogroup_energy);
        radioGroupDombusi = findViewById(R.id.radiogroup_domebusi);
        button = findViewById(R.id.button_main);

        radioGroupEnergy.setOnCheckedChangeListener((group, checkedId) -> updateButtonState());

        radioGroupDombusi.setOnCheckedChangeListener((group, checkedId) -> updateButtonState());

        button.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InvoiceActivity.class);
            intent.putExtra("radioGroupEnergyCheckedId", radioGroupEnergy.getCheckedRadioButtonId());
            intent.putExtra("radioGroupDombusiCheckedId", radioGroupDombusi.getCheckedRadioButtonId());
            startActivity(intent);
        });
    }

    /**
     * Gets the DatabaseHelper associated with this activity.
     *
     * @return the DatabaseHelper object for database operations.
     */
    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    /**
     * Called after onStart() when the activity is being re-displayed to the user (not needed in initial display).
     * This method is used to re-query any information that is needed and update the views such as enabling/disabling the button.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateButtonState();
    }

    /**
     * Updates the state of the main button based on the selected options in the radio groups.
     */
    void updateButtonState() {
        if (radioGroupEnergy.getCheckedRadioButtonId() != -1 && radioGroupDombusi.getCheckedRadioButtonId() != -1) {
            button.setEnabled(true);
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.main_button_enabled));
            button.setElevation(getResources().getDimension(R.dimen.button_elevation));
        } else {
            button.setEnabled(false);
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.main_button_disabled));
            button.setElevation(0);
        }
    }

    /**
     * Sets whether the user interface should be touch modal or not touchable based on the enabled parameter.
     *
     * @param enabled If true, the user interface is touch modal (interactive); if false, it is not touchable.
     */
    public void setEnabled(boolean enabled) {
        getWindow().setFlags(enabled ? WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL : WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Determines if the current device is a tablet based on the screen layout configuration.
     *
     * @param context The context of the application or activity, used to access resources.
     * @return {@code true} if the device is classified as a tablet, {@code false} otherwise.
     */
    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}