package com.example.enrgsavings;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.WindowManager;
import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    private MainActivity activity;

    @Before
    public void setUp() {
        // Launch the MainActivity
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activityInstance -> activity = activityInstance);
    }

    @Test
    public void testUpdateButtonState() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Set up the radio button groups with actual IDs
            activity.radioGroupEnergy.check(R.id.radiogroup_energy); // Replace with actual ID
            activity.radioGroupDombusi.check(R.id.radiogroup_domebusi); // Replace with actual ID

            // Call updateButtonState directly
            activity.updateButtonState();

            // Validate the button state
            Button button = activity.findViewById(R.id.button_main);
            assertTrue(button.isEnabled());
        });
    }

    @Test
    public void testSetEnabled_true() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Check the window flags after calling setEnabled(true)
            activity.setEnabled(true);

            // Verify the expected flags on the window
            int flags = activity.getWindow().getAttributes().flags;
            assertTrue((flags & WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL) != 0);
        });
    }

    @Test
    public void testSetEnabled_false() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity -> {
            // Check the window flags after calling setEnabled(false)
            activity.setEnabled(false);

            // Verify the expected flags on the window
            int flags = activity.getWindow().getAttributes().flags;
            assertTrue((flags & WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) != 0);
        });
    }

    @Test
    public void testIsTablet_true() {
        // Mock the context and resources for a tablet device
        Context mockContext = mock(Context.class);
        Resources mockResources = mock(Resources.class);
        Configuration mockConfig = new Configuration();
        mockConfig.screenLayout = Configuration.SCREENLAYOUT_SIZE_LARGE;

        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getConfiguration()).thenReturn(mockConfig);

        // Validate that the device is detected as a tablet
        assertTrue(activity.isTablet(mockContext));
    }

    @Test
    public void testIsTablet_false() {
        // Mock the context and resources for a non-tablet device
        Context mockContext = mock(Context.class);
        Resources mockResources = mock(Resources.class);
        Configuration mockConfig = new Configuration();
        mockConfig.screenLayout = Configuration.SCREENLAYOUT_SIZE_NORMAL;

        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getConfiguration()).thenReturn(mockConfig);

        // Validate that the device is not detected as a tablet
        assertFalse(activity.isTablet(mockContext));
    }
}
