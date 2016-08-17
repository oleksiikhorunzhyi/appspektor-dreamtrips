package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;

public class ProgressDialogFragment extends BaseDialogFragment {

   @StringRes private final int DEFAULT_MESSAGE_RES = R.string.loading;

   @StringRes private int messageRes = -1;

   public static ProgressDialogFragment create() {
      return new ProgressDialogFragment();
   }

   public static ProgressDialogFragment create(@StringRes int messageRes) {
      ProgressDialogFragment dialog = ProgressDialogFragment.create();
      dialog.setMessageRes(messageRes);
      return dialog;
   }

   public static ProgressDialogFragment createAndShow(FragmentManager manager) {
      ProgressDialogFragment dialog = ProgressDialogFragment.create();
      dialog.show(manager);
      return dialog;
   }

   public static ProgressDialogFragment createAndShow(FragmentManager manager, @StringRes int messageRes) {
      ProgressDialogFragment dialog = ProgressDialogFragment.create();
      dialog.setMessageRes(messageRes);
      dialog.show(manager);
      return dialog;
   }

   @Override
   public void onResume() {
      super.onResume();
      OrientationUtil.lockOrientation(getActivity());
   }

   @Override
   public void onPause() {
      super.onPause();
      OrientationUtil.unlockOrientation(getActivity());
   }

   public ProgressDialogFragment() {
      super();
      injectCustomLayout = false;
   }

   public void setMessageRes(@StringRes int messageRes) {
      this.messageRes = messageRes;
   }

   @NonNull
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      return new MaterialDialog.Builder(getActivity()).content(messageRes == -1 ? DEFAULT_MESSAGE_RES : messageRes)
            .progress(true, 0)
            .cancelable(false)
            .build();
   }
}
