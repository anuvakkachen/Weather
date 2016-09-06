package phunware.weather;


import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import phunware.weather.app.WeatherMainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test for main weather activity. Checks zipcode list exist and check details exist.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WeatherMainActivityTest {

    @Rule
    public ActivityTestRule<WeatherMainActivity> mActivityRule = new ActivityTestRule<>(WeatherMainActivity.class);


    //Verifies zip list exist and has contents
    @Test
    public void verifyZipList() {
        //Verify zip list is shown
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
    }

    //Verifies detail view shows when you click on zip
    @Test
    public void verifyDetailView() {
        //Verify zip list contents exist and clicking shows detail view
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Verify detail pane shows up now
        onView(withId(R.id.detail_pane)).check(matches(isDisplayed()));

        //Verify high temp shows up now
        onView(withId(R.id.highTemp)).check(matches(isDisplayed()));
    }

}
