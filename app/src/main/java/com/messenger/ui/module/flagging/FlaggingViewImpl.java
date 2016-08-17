package com.messenger.ui.module.flagging;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.innahema.collections.query.queriables.Queryable;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.messenger.ui.module.ModuleStatefulViewImpl;
import com.techery.spares.module.Injector;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;


public class FlaggingViewImpl extends ModuleStatefulViewImpl<FlaggingPresenter> implements FlaggingView {

   private Dialog activeDialog;

   private PublishSubject<Void> canceledDialogsStream = PublishSubject.create();

   public FlaggingViewImpl(View view, Injector injector) {
      super(view);
      presenter = new FlaggingPresenterImpl(this, injector);
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
      hideActiveDialog();
   }

   @Override
   public void showFlagsListDialog(List<Flag> flags) {
      String[] items = Queryable.from(flags).map(Flag::getName).toArray();
      activeDialog = new AlertDialog.Builder(getContext()).setItems(items, (dialog, i) -> {
         getPresenter().onFlagTypeChosen(flags.get(i));
      }).setOnCancelListener(this::onDialogCanceled).show();
   }

   @Override
   public Observable<CharSequence> showFlagReasonDialog(Flag flag, String prefilledReason) {
      final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_messenger_input, null);
      EditText reasonEditText = (EditText) dialogView.findViewById(R.id.et_input);
      if (!TextUtils.isEmpty(prefilledReason)) {
         reasonEditText.setText(prefilledReason);
         reasonEditText.setSelection(prefilledReason.length());
      }
      reasonEditText.setHint(R.string.type_your_reason);
      AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView)
            .setNegativeButton(R.string.chat_flag_dialog_reason_negative_button, this::onDialogCanceled)
            .setPositiveButton(R.string.chat_flag_dialog_reason_positive_button, (d, i) -> {
               SoftInputUtil.hideSoftInputMethod(reasonEditText);
               getPresenter().onFlagReasonProvided(reasonEditText.getText().toString());
            })
            .setTitle(R.string.chat_flag_dialog_reason_title)
            .setOnCancelListener(this::onDialogCanceled)
            .create();
      dialog.show();
      Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
      Observable<CharSequence> reasonObservable = RxTextView.textChangeEvents(reasonEditText)
            .map(event -> event.text());
      reasonObservable.subscribe(text -> positiveButton.setEnabled(text.toString().trim().length() > 0));
      activeDialog = dialog;
      return reasonObservable;
   }

   @Override
   public void showFlagConfirmationDialog(Flag flag) {
      activeDialog = new AlertDialog.Builder(getContext()).setTitle(R.string.chat_flag_dialog_confirmation_title)
            .setMessage(String.format(getContext().getString(R.string.chat_flag_dialog_confirmation_message_format), flag
                  .getName()))
            .setNegativeButton(R.string.chat_flag_dialog_confirmation_negative_button, this::onDialogCanceled)
            .setPositiveButton(R.string.chat_flag_dialog_confirmation_positive_button, (dialog, which) -> {
               getPresenter().onFlagMessageConfirmation();
            })
            .setOnCancelListener(this::onDialogCanceled)
            .show();
   }

   @Override
   public void showFlaggingProgressDialog() {
      showProgressDialog(R.string.chat_flag_dialog_flagging_progress);
   }

   @Override
   public void hideFlaggingProgressDialog() {
      hideActiveDialog();
   }

   @Override
   protected void onParentViewDetachedFromWindow() {
      super.onParentViewDetachedFromWindow();
      hideActiveDialog();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Helpers
   ///////////////////////////////////////////////////////////////////////////

   private void showProgressDialog(@StringRes int message) {
      activeDialog = ProgressDialog.show(getContext(), "", getContext().getString(message), true);
   }

   private void hideActiveDialog() {
      if (activeDialog != null && activeDialog.isShowing()) {
         activeDialog.dismiss();
         activeDialog = null;
      }
   }

   @Override
   public void showFlaggingSuccess() {
      Snackbar.make(getParentView(), R.string.flag_sent_success_msg, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showFlaggingError() {
      Snackbar.make(getParentView(), R.string.chat_flag_failed, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showError(String message) {
      Snackbar.make(getParentView(), message, Snackbar.LENGTH_SHORT).show();
   }

   @Override
   public void showError(@StringRes int message) {
      Snackbar.make(getParentView(), message, Snackbar.LENGTH_SHORT).show();
   }

   private void onDialogCanceled(DialogInterface dialog, int which) {
      canceledDialogsStream.onNext(null);
   }

   private void onDialogCanceled(DialogInterface dialog) {
      canceledDialogsStream.onNext(null);
   }

   @Override
   public Observable<Void> getCanceledDialogsStream() {
      return canceledDialogsStream;
   }
}
