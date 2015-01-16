package com.worldventures.dreamtrips.view.activity;


import android.os.Bundle;

import com.nispok.snackbar.Snackbar;
import com.worldventures.dreamtrips.presentation.BasePresentation;

public abstract class PresentationModelDrivenActivity<PM extends BasePresentation> extends BaseActivity implements BasePresentation.View {
    private PM presentationModel;

    public PM getPresentationModel() {
        return presentationModel;
    }

    abstract protected PM createPresentationModel(Bundle savedInstanceState);

    @Override
    protected void beforeCreateView(Bundle savedInstanceState) {
        this.presentationModel = createPresentationModel(savedInstanceState);
        inject(this.presentationModel);
    }

    public void handleError(Exception e) {
        getPresentationModel().handleError(e);
    }

    public void informUser(String st) {
        Snackbar.with(getApplicationContext()).text(st).show(this);
    }
}
