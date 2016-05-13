package com.messenger.ui.module.flagging;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.messenger.ui.module.ModuleViewImpl;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

public class FlaggingViewImpl extends ModuleViewImpl<FlaggingPresenter> implements FlaggingView {

    private ProgressDialog progressDialog;

    public FlaggingViewImpl(View view) {
        super(view);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Flagging flow
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void showFlagsLoadingDialog() {
        showProgressDialog(R.string.chat_flag_dialog_progress_loading_flags);
    }

    @Override
    public void hideFlagsLoadingDialog() {
        hideProgressDialog();
    }

    @Override
    public void showFlagsListDialog(List<Flag> flags) {
        String[] items = Queryable.from(flags).map(Flag::getName).toArray();
        new AlertDialog.Builder(getContext())
                .setItems(items, (dialog, i) -> {
                    getPresenter().onFlagTypeChosen(flags.get(i));
                })
                .show();
    }

    @Override
    public void showFlagReasonDialog(Flag flag) {
        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_messenger_input, null);
        EditText reasonEditText = (EditText) dialogView.findViewById(R.id.et_input);
        reasonEditText.setHint(R.string.type_your_reason);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setNegativeButton(R.string.chat_flag_dialog_reason_negative_button, null)
                .setPositiveButton(R.string.chat_flag_dialog_reason_positive_button, (d, i) -> {
                    SoftInputUtil.hideSoftInputMethod(reasonEditText);
                    getPresenter().onFlagReasonProvided(reasonEditText.getText().toString());
                })
                .setTitle(R.string.chat_flag_dialog_reason_title)
                .create();
        dialog.show();
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        RxTextView.textChangeEvents(reasonEditText)
                .subscribe(event -> positiveButton.setEnabled(event.count() > 0));
    }

    @Override
    public void showFlagConfirmationDialog(Flag flag) {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.chat_flag_dialog_confirmation_title)
                .setMessage(String.format(getContext()
                        .getString(R.string.chat_flag_dialog_confirmation_message_format), flag.getName()))
                .setNegativeButton(R.string.chat_flag_dialog_confirmation_negative_button, null)
                .setPositiveButton(R.string.chat_flag_dialog_confirmation_positive_button, (dialog, which) -> {
                    getPresenter().onFlagMessageConfirmation();
                })
                .show();
    }

    @Override
    public void showFlaggingProgressDialog() {
        showProgressDialog(R.string.chat_flag_dialog_flagging_progress);
    }

    @Override
    public void hideFlaggingProgressDialog() {
        hideProgressDialog();
    }

    @Override
    protected void onParentViewDetachedFromWindow() {
        super.onParentViewDetachedFromWindow();
        hideProgressDialog();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Helpers
    ///////////////////////////////////////////////////////////////////////////

    private void showProgressDialog(@StringRes int message) {
        progressDialog = ProgressDialog.show(getContext(), "",
                getContext().getString(message), true);
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void showFlaggingSuccess() {
        Snackbar.make(getParentView(), R.string.flag_sent_success_msg, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showFlaggingError() {
        Snackbar.make(getParentView(), R.string.chat_flag_failed, Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showError(String message) {
        Snackbar.make(getParentView(), message, Snackbar.LENGTH_SHORT)
                .show();
    }
}
