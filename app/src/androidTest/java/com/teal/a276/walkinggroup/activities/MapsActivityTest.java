package com.teal.a276.walkinggroup.activities;

import android.graphics.Rect;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.teal.a276.walkinggroup.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapsActivityTest {

    @Rule
    public ActivityTestRule<MapsActivity> activityTestRule = new ActivityTestRule<MapsActivity>(MapsActivity.class);

    private  MapsActivity mapsActivity = null;

    @Before
    public void setUp() throws Exception {
        mapsActivity = activityTestRule.getActivity();
    }

    @Test
    public void testLaunch(){
        View view = mapsActivity.findViewById(R.id.map);
        assertNotNull(view);
    }

    @Test
    public void visibility(){
        View view = mapsActivity.findViewById(R.id.map);

        Rect bounds = new Rect();
        view.getHitRect(bounds);

        assertTrue(view.getLocalVisibleRect(bounds));
        assertEquals(view.getVisibility(), View.VISIBLE);
    }

    @After
    public void tearDown() throws Exception {
        mapsActivity = null;
    }
}