package com.worldventures.dreamtrips.activity;

import android.widget.EditText;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

public class LoginActivityTest extends BaseActivityTest<LoginActivity> {


    @SuppressWarnings("unchecked")
    public LoginActivityTest() {
        super(LoginActivity.class);
    }


    public void testLogin() throws Exception {
        solo.assertCurrentActivity("wrong activity", LoginActivity.class);
        solo.waitForView(R.id.et_username);
        solo.enterText((EditText) solo.getView(R.id.et_username), "John");
        solo.enterText((EditText) solo.getView(R.id.et_password), "Password");
        solo.clickOnView(solo.getView(R.id.btn_login));
        solo.waitForActivity(MainActivity.class);
        solo.assertCurrentActivity("wrong activity", MainActivity.class);
    }
}

