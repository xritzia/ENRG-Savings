package com.example.enrgsavings;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * AsyncTask that handles the background scraping of data from specific web pages.
 * This task fetches electricity usage data from different categories and saves it to a local database.
 */
public class DataScraper extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<MainActivity> activityReference;
    private final ProgressBar progressBar;
    private final TextView textviewScrapingProgress;
    private ImageButton buttonScrapingInfo;
    private PopupWindow popupWindow;
    private boolean isPopupShowing = false;

    /**
     * Constructor for DataScraper.
     *
     * @param context                  the context of the calling activity, used to update UI components
     * @param progressBar              the progress bar to show task progress
     * @param textviewScrapingProgress the text view to display task progress messages
     */
    DataScraper(MainActivity context, ProgressBar progressBar, TextView textviewScrapingProgress) {
        activityReference = new WeakReference<>(context);
        this.progressBar = progressBar;
        this.textviewScrapingProgress = textviewScrapingProgress;
    }

    /**
     * Executes the data scraping from predefined URLs in the background.
     * This method sets up a secure SSL context to securely fetch data from HTTPS URLs,
     * then parses and stores the data in the database.
     *
     * @return Boolean true if scraping and data insertion were successful, false otherwise.
     */
    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            MainActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) {
                return false;
            }

            // Load the SSL certificate from raw resources
            InputStream certInputStream = activity.getResources().openRawResource(R.raw.rae_certification);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(certInputStream);
            certInputStream.close();

            // Initialize a KeyStore with the loaded certificate
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("rae_certification", cert);

            // Set up a TrustManagerFactory that uses our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Connect to the URL with the configured SSL context
            Connection homeConnection = Jsoup.connect("https://invoices.rae.gr/oikiako/");
            homeConnection.sslSocketFactory(sslContext.getSocketFactory());
            Document homeDocument = homeConnection.get();
            insertDataFromDocument(activity, homeDocument, "electricity_home");
            Connection businessConnection = Jsoup.connect("https://invoices.rae.gr/epaggelmatiko/");
            businessConnection.sslSocketFactory(sslContext.getSocketFactory());
            Document businessDocument = businessConnection.get();
            insertDataFromDocument(activity, businessDocument, "electricity_business");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Parses and inserts data from a scraped document into the specified database table.
     * Iterates through each row in the HTML table of the document to extract and insert billing data.
     *
     * @param activity  The MainActivity instance, used to access the database helper.
     * @param document  The JSoup Document object representing the HTML page.
     * @param tableName The name of the database table where data will be inserted.
     */
    void insertDataFromDocument(MainActivity activity, Document document, String tableName) {
        Element table = document.selectFirst("#billing_table");
        for (Element row : table.select("tr")) {
            String provider = row.select("td:eq(0)").text();
            int year = parseIntSafe(row.select("td:eq(1)").text());
            int month = parseIntSafe(row.select("td:eq(2)").text());
            Element invoiceElement = row.selectFirst("td:eq(3)");
            if (invoiceElement != null) {
                String invoiceName = invoiceElement.text();
                String colorClass = invoiceElement.className();
                double fixedRate = parseDoubleSafe(row.select("td:eq(6)").text());
                String fixedDiscount = row.select("td:eq(7)").text();
                double finalPrice = parseDoubleSafe(row.select("td:eq(8)").text());
                String finalDiscount = row.select("td:eq(9)").text();
                String comments = row.select("td:eq(10)").text();
                String formattedFixedRate = String.format("%.2f", fixedRate);
                String formattedFinalPrice = String.format("%.2f", finalPrice);

                if (!activity.getDatabaseHelper().dataExists(tableName, provider, year, month, invoiceName, finalPrice)) {
                    activity.getDatabaseHelper().insertData(tableName, provider, year, month, invoiceName, colorClass,
                            Double.parseDouble(formattedFixedRate), fixedDiscount, Double.parseDouble(formattedFinalPrice), finalDiscount, comments);
                }
            }
        }
    }

    /**
     * Safely parses an integer from a string, returning 0 if the string is not a valid integer.
     *
     * @param str The string to parse.
     * @return The integer value of the string, or 0 if the string cannot be parsed.
     */
    int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Safely parses a double from a string, returning 0.0 if the string is not a valid double.
     *
     * @param str The string to parse.
     * @return The double value of the string, or 0.0 if the string cannot be parsed.
     */
    double parseDoubleSafe(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Invoked on the UI thread before the task is executed, used to setup the UI components.
     */
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        textviewScrapingProgress.setVisibility(View.VISIBLE);
        textviewScrapingProgress.setText(R.string.data_check);
    }

    /**
     * Invoked on the UI thread after the task is executed.
     *
     * @param success the result of the task, indicating if the operation was successful
     */
    @Override
    protected void onPostExecute(Boolean success) {
        progressBar.setVisibility(View.GONE);
        textviewScrapingProgress.setText(success ? R.string.data_success : R.string.data_fail);

        MainActivity activity = activityReference.get();
        if (activity != null) {
            buttonScrapingInfo = activity.findViewById(R.id.button_scraping_info);
            if (buttonScrapingInfo != null && !success) {
                buttonScrapingInfo.setOnClickListener(v -> showPopupWindow());
            }
        }

        if (success) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setDuration(1000);
                textviewScrapingProgress.startAnimation(fadeOut);
                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        textviewScrapingProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }, 1500);
        } else {
            buttonScrapingInfo.setVisibility(View.VISIBLE);
            textviewScrapingProgress.setText(R.string.data_fail);
        }
    }

    /**
     * Shows a popup window with error information if scraping fails.
     */
    private void showPopupWindow() {
        MainActivity activity = activityReference.get();
        if (activity != null) {
            View popupView = LayoutInflater.from(activity).inflate(R.layout.popup_data_error, null);

            Button closeButton = popupView.findViewById(R.id.button_close_popup);
            closeButton.setOnClickListener(v -> dismissPopupWindow());

            popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            popupWindow.setElevation(10f);
            popupWindow.setOnDismissListener(() -> {
                isPopupShowing = false;
                enableMainActivity();
            });

            popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            isPopupShowing = true;
            enableMainActivity();
        }
    }

    /**
     * Dismisses the currently shown popup window.
     */
    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * Re-enables interaction with MainActivity when the popup is dismissed or not showing.
     */
    private void enableMainActivity() {
        MainActivity activity = activityReference.get();
        if (activity != null) {
            activity.setEnabled(!isPopupShowing);
        }
    }

}