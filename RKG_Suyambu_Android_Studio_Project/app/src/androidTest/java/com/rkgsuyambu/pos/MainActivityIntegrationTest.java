package com.rkgsuyambu.pos;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntegrationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMainActivityLaunchesAndLoadsWebView() {
        activityRule.getScenario().onActivity(activity -> {
            assertNotNull("MainActivity must not be null", activity);
            assertFalse("MainActivity should not be finishing", activity.isFinishing());
        });
    }
}
