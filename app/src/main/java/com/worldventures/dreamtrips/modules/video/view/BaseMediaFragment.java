package com.worldventures.dreamtrips.modules.video.view;

import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

public abstract class BaseMediaFragment<V extends Presenter> extends BaseFragment<V> {

    protected void showDialog(@StringRes int title, @StringRes int content, @StringRes int positive, @StringRes int negative, VideoDialogClickListener videoDialogClick) {
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(content)
                .positiveText(positive)
                .negativeText(negative)
                .onPositive((dialog, which) -> videoDialogClick.onClick())
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    public interface VideoDialogClickListener {
        void onClick();
    }
}
