package com.worldventures.dreamtrips.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.auth.view.LoginActivity;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.robolectric.Robolectric.clickOn;
import static org.robolectric.Robolectric.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LoginActivityTest {

    private LoginActivity activity;
    private EditText etUserName;
    private EditText etPassword;
    private Button btnLogin;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(LoginActivity.class).create().visible().get();
        etUserName = (EditText) activity.findViewById(R.id.et_username);
        etPassword = (EditText) activity.findViewById(R.id.et_password);
        btnLogin = (Button) activity.findViewById(R.id.btn_login);
    }

    @Test
    public void shouldDoLogin() throws Exception {
        String appName = new LoginActivity().getResources().getString(R.string.app_name);
        assertThat(appName, equalTo("DreamTrips"));
        clickOn(btnLogin);

        ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(MainActivity.class.getName()));

    }
}
