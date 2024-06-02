package com.example.enrgsavings;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class InvoiceAdapterTest {

    private InvoiceAdapter adapter;
    private List<InvoiceData> invoiceDataList;

    @Before
    public void setUp() {
        // Initialize with a sample list of InvoiceData
        invoiceDataList = new ArrayList<>();

        // Create a new InvoiceData object and use setters to populate data
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setInvoiceName("Invoice1");
        invoiceData.setProvider("ΔΕΗ");
        invoiceData.setFixedRate(10.0);
        invoiceData.setFinalPrice(100.0);
        invoiceData.setcolorClass("color_blue");
        invoiceData.setFixedDiscount("Fixed Discount");
        invoiceData.setFinalDiscount("Final Discount");
        invoiceData.setComments("Sample Comment");

        // Add the object to the list
        invoiceDataList.add(invoiceData);

        // Initialize the adapter
        adapter = new InvoiceAdapter(invoiceDataList);
    }

    @Test
    public void testGetItemCount() {
        // Verify that the item count matches the size of the list
        assertEquals(invoiceDataList.size(), adapter.getItemCount());
    }

    @Test
    public void testOnCreateViewHolder() {
        // Create a ViewGroup to pass as the parent
        ViewGroup parent = (ViewGroup) LayoutInflater.from(ApplicationProvider.getApplicationContext())
                .inflate(R.layout.card_view_item, null);

        // Verify that a new ViewHolder is created
        InvoiceAdapter.InvoiceViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);
        assertNotNull(viewHolder);
    }

    @Test
    public void testOnBindViewHolder() {
        // Create a ViewGroup to pass as the parent
        ViewGroup parent = (ViewGroup) LayoutInflater.from(ApplicationProvider.getApplicationContext())
                .inflate(R.layout.card_view_item, null);

        // Create a new ViewHolder
        InvoiceAdapter.InvoiceViewHolder viewHolder = adapter.onCreateViewHolder(parent, 0);

        // Bind the first item to the ViewHolder
        adapter.onBindViewHolder(viewHolder, 0);

        // Verify the data binding (check if expected text is set on the view holder)
        assertEquals("Invoice1", viewHolder.invoiceName.getText().toString());
        assertEquals("10.00", viewHolder.fixedRate.getText().toString());
        assertEquals("100.00", viewHolder.finalPrice.getText().toString());
    }
}
