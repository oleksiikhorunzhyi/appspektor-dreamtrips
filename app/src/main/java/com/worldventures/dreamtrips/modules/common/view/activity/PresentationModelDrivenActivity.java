package com.worldventures.dreamtrips.modules.common.view.activity;


import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;

public abstract class PresentationModelDrivenActivity<PM extends BasePresenter> extends BaseActivity implements BasePresenter.View {
    private PM presentationModel;

    public PM getPresentationModel() {
        return presentationModel;
    }

    abstract protected PM createPresentationModel(Bundle savedInstanceState);

    @Override
    protected void beforeCreateView(Bundle savedInstanceState) {
        this.presentationModel = createPresentationModel(savedInstanceState);
        inject(this.presentationModel);
        this.presentationModel.init();
    }

    public void informUser(String st) {
        SnackBar snackbar = new SnackBar(this, st);
        snackbar.show();
    }

    @Override
    public void alert(String s) {
        runOnUiThread(() -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title("Alert").content(s).positiveText("Ok").show();
        });
    }

    @Override
    public void onDestroy() {
        if (getPresentationModel() != null)
            getPresentationModel().destroyView();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        DreamSpiceManager dreamSpiceManager = getPresentationModel().getDreamSpiceManager();
        if (!dreamSpiceManager.isStarted()) dreamSpiceManager.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DreamSpiceManager dreamSpiceManager = getPresentationModel().getDreamSpiceManager();
        if (dreamSpiceManager.isStarted()) dreamSpiceManager.shouldStop();
    }
}