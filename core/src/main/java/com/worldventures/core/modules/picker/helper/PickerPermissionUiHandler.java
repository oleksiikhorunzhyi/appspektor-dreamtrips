package com.worldventures.core.modules.picker.helper;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.worldventures.core.R;

import rx.functions.Action1;

public class PickerPermissionUiHandler {

   public void showRational(Context context, Action1<Boolean> userAnswer) {
      new MaterialDialog.Builder(context)
            .content(R.string.picker_rational_message_to_read_storage)
            .positiveText(R.string.action_ok)
            .negativeText(R.string.action_cancel)
            .onPositive((materialDialog, dialogAction) -> userAnswer.call(true))
            .onNegative((materialDialog, dialogAction) -> userAnswer.call(false))
            .cancelable(false)
            .show();
   }

   public void showPermissionDenied(View view) {
      Snackbar.make(view, R.string.picker_no_permission_to_read_storage, Snackbar.LENGTH_SHORT).show();
   }

}
