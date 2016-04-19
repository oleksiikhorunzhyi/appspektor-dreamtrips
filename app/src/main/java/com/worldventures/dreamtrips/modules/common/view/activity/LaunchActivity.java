package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;

import butterknife.InjectView;

@Layout(R.layout.activity_launch)
public class LaunchActivity extends ActivityWithPresenter<LaunchActivityPresenter> implements LaunchActivityPresenter.View {

    @InjectView(R.id.pb)
    ProgressBar pb;

    private Snackbar snackbar;

    @Override
    protected LaunchActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new LaunchActivityPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) getPresentationModel().initDtl();
    }

    @Override
    public void alert(String s) {
        runOnUiThread(() -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.alert)
                    .content(s)
                    .positiveText(R.string.OK)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            finish();
                        }
                    })
                    .show();
        });

    }

    @Override
    public void configurationFailed() {
        pb.setVisibility(View.GONE);
        snackbar = Snackbar.make(findViewById(R.id.rootView),
                R.string.configuration_failed,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.configuration_acitve_button, v -> {
                    getPresentationModel().startPreloadChain();
                });
        snackbar.show();
    }

    @Override
    public void configurationStarted() {
        pb.setVisibility(View.VISIBLE);
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }
}
