package com.worldventures.dreamtrips.modules.dtl.view.dialog;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.worldventures.dreamtrips.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DialogFactory {

   private DialogFactory() {}

   public static SweetAlertDialog createRetryDialog(Activity activity, String content) {
      return createDialog(activity, SweetAlertDialog.ERROR_TYPE, R.string.dtl_load_error, R.string.dtl_try_again, content, true);
   }

   public static SweetAlertDialog createErrorDialog(Activity activity, String content) {
      return createDialog(activity, SweetAlertDialog.ERROR_TYPE, R.string.alert, R.string.ok, content, true);
   }

   public static SweetAlertDialog createDialog(Activity activity, int type, @StringRes int titleRes, @StringRes int textButtonRes, @Nullable String content, boolean cancellable) {
      String title = activity.getString(titleRes);
      String textButton = activity.getString(textButtonRes);
      return create(activity, type, title, textButton, content, cancellable);
   }

   public static SweetAlertDialog create(Activity activity, int type, String title, String textButton, @Nullable String content, boolean cancellable) {
      final SweetAlertDialog dialog = new SweetAlertDialog(activity, type);
      dialog.setTitleText(title);
      dialog.setConfirmText(textButton);
      dialog.setCancelable(cancellable);
      dialog.setCanceledOnTouchOutside(true);
      if (content != null) dialog.setContentText(content);
      return dialog;
   }
}
