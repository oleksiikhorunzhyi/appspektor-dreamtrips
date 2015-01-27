package com.worldventures.dreamtrips.view.activity;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.LoginActivityPresentation;

@Layout(R.layout.activity_login)
public class LoginActivity extends PresentationModelDrivenActivity<LoginActivityPresentation> {

    @Override
    protected LoginActivityPresentation createPresentationModel(Bundle savedInstanceState) {
        return new LoginActivityPresentation(this);
    }

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        this.getPresentationModel().onCreate();
    }
}
