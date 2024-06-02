package com.example.enrgsavings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to manage database creation and version management.
 * Handles the storage and retrieval of invoice data.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "scraped_data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_HOME = "electricity_home";
    private static final String TABLE_NAME_BUSINESS = "electricity_business";
    private static final String COLUMN_ID = "id";

    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS %1$s (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            "provider TEXT," +
            "year INTEGER," +
            "month INTEGER," +
            "invoice_name TEXT," +
            "color_class TEXT," +
            "fixed_rate REAL," +
            "fixed_discount TEXT," +
            "final_price REAL," +
            "final_discount TEXT," +
            "comments TEXT" +
            ")";

    /**
     * Constructs a new DatabaseHelper.
     *
     * @param context The context to use, usually your application or activity context.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format(CREATE_TABLE_QUERY, TABLE_NAME_HOME));
        db.execSQL(String.format(CREATE_TABLE_QUERY, TABLE_NAME_BUSINESS));
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_HOME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BUSINESS);
        onCreate(db);
    }

    /**
     * Inserts data into the specified table.
     *
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
    public long insertData(String tableName, String provider, int year, int month, String invoiceName,
                           String colorClass, double fixedRate, String fixedDiscount, double finalPrice,
                           String finalDiscount, String comments) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("provider", provider);
        values.put("year", year);
        values.put("month", month);
        values.put("invoice_name", invoiceName);
        values.put("color_class", colorClass);
        values.put("fixed_rate", fixedRate);
        values.put("fixed_discount", fixedDiscount);
        values.put("final_price", finalPrice);
        values.put("final_discount", finalDiscount);
        values.put("comments", comments);
        long id = db.insert(tableName, null, values);
        db.close();
        return id;
    }

    /**
     * Checks if specific invoice data already exists in the database to avoid duplicates.
     *
     * @return true if data exists, false otherwise.
     */
    public boolean dataExists(String tableName, String provider, int year, int month, String invoiceName, double finalPrice) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID};
        String selection = "provider = ? AND year = ? AND month = ? AND invoice_name = ? AND final_price = ?";
        String[] selectionArgs = {provider, String.valueOf(year), String.valueOf(month), invoiceName, String.valueOf(finalPrice)};
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    /**
     * Converts a cursor from a database query into a list of InvoiceData objects.
     *
     * @param cursor The cursor from which to extract data.
     * @return A list of InvoiceData objects populated with the data from the cursor.
     */
    public List<InvoiceData> getInvoiceDataFromCursor(Cursor cursor) {
        List<InvoiceData> invoiceDataList = new ArrayList<>();
        if (cursor != null) {
            int providerIndex = cursor.getColumnIndex("provider");
            int yearIndex = cursor.getColumnIndex("year");
            int monthIndex = cursor.getColumnIndex("month");
            int invoiceNameIndex = cursor.getColumnIndex("invoice_name");
            int colorClassIndex = cursor.getColumnIndex("color_class");
            int fixedRateIndex = cursor.getColumnIndex("fixed_rate");
            int fixedDiscountIndex = cursor.getColumnIndex("fixed_discount");
            int finalPriceIndex = cursor.getColumnIndex("final_price");
            int finalDiscountIndex = cursor.getColumnIndex("final_discount");
            int commentsIndex = cursor.getColumnIndex("comments");

            while (cursor.moveToNext()) {
                InvoiceData invoiceData = new InvoiceData();
                if (providerIndex >= 0) {
                    invoiceData.setProvider(cursor.getString(providerIndex));
                }
                if (yearIndex >= 0) {
                    invoiceData.setYear(cursor.getInt(yearIndex));
                }
                if (monthIndex >= 0) {
                    invoiceData.setMonth(cursor.getInt(monthIndex));
                }
                if (invoiceNameIndex >= 0) {
                    invoiceData.setInvoiceName(cursor.getString(invoiceNameIndex));
                }
                if (colorClassIndex >= 0) {
                    invoiceData.setcolorClass(cursor.getString(colorClassIndex));
                }
                if (fixedRateIndex >= 0) {
                    invoiceData.setFixedRate(cursor.getDouble(fixedRateIndex));
                }
                if (fixedDiscountIndex >= 0) {
                    invoiceData.setFixedDiscount(cursor.getString(fixedDiscountIndex));
                }
                if (finalPriceIndex >= 0) {
                    invoiceData.setFinalPrice(cursor.getDouble(finalPriceIndex));
                }
                if (finalDiscountIndex >= 0) {
                    invoiceData.setFinalDiscount(cursor.getString(finalDiscountIndex));
                }
                if (commentsIndex >= 0) {
                    invoiceData.setComments(cursor.getString(commentsIndex));
                }
                invoiceDataList.add(invoiceData);
            }
            cursor.close();
        }
        return invoiceDataList;
    }

    /**
     * Determines the SQL ORDER BY clause based on the sorting option provided.
     *
     * @param sortingOption A string representing the sorting preference selected by the user.
     * @return An SQL ORDER BY clause as a String that can be appended to a query.
     */
    private String getSortingOrder(String sortingOption) {
        switch (sortingOption) {
            case "Ανά Πάροχο":
                return " ORDER BY provider ASC";
            case "Τιμή Αύξουσα":
                return " ORDER BY final_price ASC";
            case "Τιμή Φθίνουσα":
                return " ORDER BY final_price DESC";
            case "Ανά Χρώμα":
                return " ORDER BY color_class ASC";
            default:
                return " ORDER BY id ASC";
        }
    }

    /**
     * Retrieves a list of InvoiceData objects from the database for the 'electricity_home' table
     * filtered by the specified month and sorted according to the specified option.
     *
     * @param month         The month for which data is to be retrieved.
     * @param sortingOption The sorting criteria to apply to the query.
     * @return A list of InvoiceData objects containing the requested data.
     */
    public List<InvoiceData> getDataFromElectricityHome(int month, String sortingOption) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_HOME + " WHERE final_price > 0 AND month = ?";
        query += getSortingOrder(sortingOption);

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(month)});
        return getInvoiceDataFromCursor(cursor);
    }

    /**
     * Retrieves a list of InvoiceData objects from the database for the 'electricity_business' table
     * filtered by the specified month and sorted according to the specified option.
     *
     * @param month         The month for which data is to be retrieved.
     * @param sortingOption The sorting criteria to apply to the query.
     * @return A list of InvoiceData objects containing the requested data.
     */
    public List<InvoiceData> getDataFromElectricityBusiness(int month, String sortingOption) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME_BUSINESS + " WHERE final_price > 0 AND month = ?";
        query += getSortingOrder(sortingOption);

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(month)});
        return getInvoiceDataFromCursor(cursor);
    }

}