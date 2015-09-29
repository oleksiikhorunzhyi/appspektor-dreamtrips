package com.worldventures.dreamtrips.modules.bucketlist.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.event.BucketItemDeleteConfirmedEvent;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;

import icepick.Icepick;
import icepick.State;

public class DeleteBucketDialog extends BaseDialogFragment {

    @State
    String bucketItemId;

    public DeleteBucketDialog create(String bucketItemId) {
        DeleteBucketDialog dialog = new DeleteBucketDialog();
        dialog.setBucketItemId(bucketItemId);
        return dialog;
    }

    public DeleteBucketDialog() {
        super();
        injectCustomLayout = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Icepick.restoreInstanceState(this, savedInstanceState);

        return new MaterialDialog.Builder(getActivity())
                .content(R.string.bucket_delete_dialog)
                .positiveText(R.string.delete_photo_positiove)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        eventBus.post(new BucketItemDeleteConfirmedEvent(bucketItemId));
                    }
                })
                .build();
    }

    public void setBucketItemId(String bucketItemId) {
        this.bucketItemId = bucketItemId;
    }
}
