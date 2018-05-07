package com.worldventures.dreamtrips.modules.dtl_flow.parts.common;

import android.content.Context;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SimpleSweetDialogErrorView<T> extends DialogErrorView<T> {

   private final SweetDialogParams<T> params;

   public SimpleSweetDialogErrorView(Context context, SweetDialogParams<T> params) {
      super(context);
      this.params = params;
   }

   @Override
   protected SweetAlertDialog createDialog(T t, Throwable throwable, Context context) {
      final SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(params.title())
            .setContentText(params.content())
            .setConfirmText(params.positiveText())
            .setConfirmClickListener(listener -> {
               listener.dismissWithAnimation();
               if (params.positiveAction() != null) {
                  params.positiveAction().action(t);
               }
            });
      if (params.isCancelable()) {
         dialog.setCancelText(params.negativeText()).setCancelClickListener(SweetAlertDialog::dismissWithAnimation);
      }
      return dialog;
   }
}
