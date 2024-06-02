package com.example.enrgsavings;

import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class InvoiceActivityTest {

    @Mock
    private DatabaseHelper mockDatabaseHelper;

    @Mock
    private InvoiceAdapter mockInvoiceAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDisplayInvoiceData() {
        // Create the Intent with the required extras and set InvoiceActivity class
        Intent intent = new Intent();
        intent.putExtra("radioGroupEnergyCheckedId", R.id.radio_electricity);
        intent.putExtra("radioGroupDombusiCheckedId", R.id.radio_domestic);
        intent.setClassName("com.example.enrgsavings", InvoiceActivity.class.getName());

        // Launch the activity with the mocked intent
        ActivityScenario<InvoiceActivity> scenario = ActivityScenario.launch(intent);

        scenario.onActivity(activity -> {
            // Use reflection to set the private DatabaseHelper
            try {
                Field databaseHelperField = InvoiceActivity.class.getDeclaredField("databaseHelper");
                databaseHelperField.setAccessible(true);
                databaseHelperField.set(activity, mockDatabaseHelper);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Setup mock data
            List<InvoiceData> mockData = new ArrayList<>();
            // Add mock data to the list if necessary

            // Mock database behavior
            when(mockDatabaseHelper.getDataFromElectricityHome(anyInt(), anyString()))
                    .thenReturn(mockData);

            // Use reflection to call the private displayInvoiceData method
            try {
                Method displayInvoiceDataMethod = InvoiceActivity.class.getDeclaredMethod("displayInvoiceData", int.class, String.class);
                displayInvoiceDataMethod.setAccessible(true);
                displayInvoiceDataMethod.invoke(activity, 1, "asc");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Verify RecyclerView visibility
            View noDataView = activity.findViewById(R.id.textview_no_data);
            if (mockData.isEmpty()) {
                assertEquals(View.VISIBLE, noDataView.getVisibility());
            } else {
                assertEquals(View.GONE, noDataView.getVisibility());
            }
        });
    }
}
