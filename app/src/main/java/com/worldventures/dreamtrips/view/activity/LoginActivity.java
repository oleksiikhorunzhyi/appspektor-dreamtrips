package com.worldventures.dreamtrips.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.presentation.activity.LoginActivityPresentation;

public class LoginActivity extends BaseActivity implements LoginActivityPresentation.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginActivityPresentation presentationModel = new LoginActivityPresentation(this,getDataManager());
        initializeContentView(R.layout.activity_login, presentationModel);
    }
    @Override
    public void openMainWindow() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
