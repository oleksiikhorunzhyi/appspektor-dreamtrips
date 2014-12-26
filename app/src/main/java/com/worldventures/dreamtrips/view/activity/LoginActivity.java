package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.presentation.LoginActivityPresentation;

public class LoginActivity extends BaseActivity {
    LoginActivityPresentation lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lp = new LoginActivityPresentation(this,this);
        lp.onCreate();
    }
}
