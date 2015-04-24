package com.worldventures.dreamtrips.modules.common.view.activity;


import android.os.Bundle;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

public abstract class ActivityWithPresenter<PM extends Presenter> extends BaseActivity implements Presenter.View {
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
        Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void informUser(int stringId) {
        Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(this) && ViewUtils.isLandscapeOrientation(this);
    }

    @Override
    public void alert(String s) {
        runOnUiThread(() -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.alert).content(s).positiveText(R.string.OK).show();
        });
    }

    @Override
    public void onDestroy() {
        if (getPresentationModel() != null) {
            getPresentationModel().destroyView();
        }
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        DreamSpiceManager dreamSpiceManager = getPresentationModel().getDreamSpiceManager();
        if (!dreamSpiceManager.isStarted()) {
            dreamSpiceManager.start(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DreamSpiceManager dreamSpiceManager = getPresentationModel().getDreamSpiceManager();
        if (dreamSpiceManager.isStarted()) {
            dreamSpiceManager.shouldStop();
        }
    }
}
