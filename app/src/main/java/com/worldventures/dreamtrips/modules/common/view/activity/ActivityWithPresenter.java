package com.worldventures.dreamtrips.modules.common.view.activity;


import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;

import butterknife.ButterKnife;
import icepick.Icepick;

public abstract class ActivityWithPresenter<PM extends Presenter> extends BaseActivity implements Presenter.View {
    private PM presenter;

    public PM getPresentationModel() {
        return presenter;
    }

    abstract protected PM createPresentationModel(Bundle savedInstanceState);

    @Override
    protected void beforeCreateView(Bundle savedInstanceState) {
        this.presenter = createPresentationModel(savedInstanceState);
        inject(this.presenter);
        this.presenter.onInjected();
        Icepick.restoreInstanceState(this, savedInstanceState);
        this.presenter.restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
        if (presenter != null) this.presenter.saveInstanceState(outState);
    }


    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        this.presenter.takeView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.presenter.onResume();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.presenter.onMenuPrepared();
        return super.onPrepareOptionsMenu(menu);
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
    public boolean isVisibleOnScreen() {
        return true;
    }

    @Override
    public void alert(String s) {
        runOnUiThread(() -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.alert).content(s).positiveText(R.string.OK).show();
        });
    }

    @Override
    public void showEditContainer() {
        View container = ButterKnife.findById(this, R.id.container_details_floating);
        if (container != null) container.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresentationModel().onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresentationModel().onStop();
    }

    @Override
    public void onDestroy() {
        if (getPresentationModel() != null) {
            getPresentationModel().dropView();
        }
        super.onDestroy();
    }

}
