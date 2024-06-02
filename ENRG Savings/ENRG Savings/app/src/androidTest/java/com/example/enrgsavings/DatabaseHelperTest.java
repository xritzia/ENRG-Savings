package com.example.enrgsavings;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        // Get the application context to initialize DatabaseHelper
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new DatabaseHelper(context);

        // Open the writable database to perform operations
        db = dbHelper.getWritableDatabase();
    }

    @After
    public void tearDown() {
        // Close the database and the helper to release resources
        dbHelper.close();
        db.close();
    }

    @Test
    public void testInsertData() {
        // Insert a sample data into the 'electricity_home' table
        long id = dbHelper.insertData("electricity_home", "Provider1", 2024, 5, "Invoice1",
                "Blue", 10.0, "No discount", 100.0, "No final discount", "Test comments");
        // Verify that the insertion was successful
        assertTrue(id != -1);
    }

    @Test
    public void testDataExists() {
        dbHelper.insertData("electricity_home", "Provider1", 2024, 5, "Invoice1",
                "Blue", 10.0, "No discount", 100.0, "No final discount", "Test comments");

        // Check if the inserted data exists in the database
        boolean exists = dbHelper.dataExists("electricity_home", "Provider1", 2024, 5, "Invoice1", 100.0);
        // Assert that the data exists
        assertTrue(exists);
    }

    @Test
    public void testGetDataFromElectricityHome() {
        dbHelper.insertData("electricity_home", "Provider1", 2024, 5, "Invoice1",
                "Blue", 10.0, "No discount", 100.0, "No final discount", "Test comments");

        // Retrieve data based on the inserted data
        List<InvoiceData> dataList = dbHelper.getDataFromElectricityHome(5, "Τιμή Αύξουσα");

        // Verify that the retrieved data matches the inserted data
        assertEquals(1, dataList.size());
        assertEquals("Provider1", dataList.get(0).getProvider());
    }
}
