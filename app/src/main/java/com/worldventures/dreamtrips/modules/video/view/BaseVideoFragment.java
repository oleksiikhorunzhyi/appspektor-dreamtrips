package com.worldventures.dreamtrips.modules.video.view;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

public abstract class BaseVideoFragment<V extends Presenter> extends BaseFragment<V> {

    protected void showCancelDialog(VideoDialogClickListener videoDialogClick) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.cancel_cached_video_title)
                .content(R.string.cancel_cached_video_text)
                .positiveText(R.string.cancel_photo_positiove)
                .negativeText(R.string.cancel_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        videoDialogClick.onClick();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

    }

    protected void showDeleteDialog(VideoDialogClickListener videoDialogClick) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.delete_cached_video_title)
                .content(R.string.delete_cached_video_text)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.delete_photo_negative)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        videoDialogClick.onClick();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public interface VideoDialogClickListener {
        void onClick();
    }
}
