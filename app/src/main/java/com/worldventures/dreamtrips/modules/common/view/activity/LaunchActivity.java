package com.worldventures.dreamtrips.modules.common.view.activity;

import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.LaunchActivityPresenter;

@Layout(R.layout.activity_launch)
public class LaunchActivity extends ActivityWithPresenter<LaunchActivityPresenter> {

    @Override
    protected LaunchActivityPresenter createPresentationModel(Bundle savedInstanceState) {
        return new LaunchActivityPresenter();
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
}
