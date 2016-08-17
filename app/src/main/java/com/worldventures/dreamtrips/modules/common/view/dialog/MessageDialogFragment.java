package com.worldventures.dreamtrips.modules.common.view.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.utils.ui.OrientationUtil;
import com.worldventures.dreamtrips.R;

public class MessageDialogFragment extends BaseDialogFragment {

   @StringRes private int messageRes = R.string.smth_went_wrong;

   public static MessageDialogFragment create() {
      return new MessageDialogFragment();
   }

   public static MessageDialogFragment create(@StringRes int messageRes) {
      MessageDialogFragment dialog = MessageDialogFragment.create();
      dialog.setMessageRes(messageRes);
      return dialog;
   }

   public static MessageDialogFragment createAndShow(FragmentManager manager) {
      MessageDialogFragment dialog = MessageDialogFragment.create();
      dialog.show(manager);
      return dialog;
   }

   public static MessageDialogFragment createAndShow(FragmentManager manager, @StringRes int messageRes) {
      MessageDialogFragment dialog = MessageDialogFragment.create();
      dialog.setMessageRes(messageRes);
      dialog.show(manager);
      return dialog;
   }

   public MessageDialogFragment() {
      super();
      injectCustomLayout = false;
   }

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (savedInstanceState != null) {
         messageRes = savedInstanceState.getInt("message", messageRes);
      }
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      outState.putInt("message", messageRes);
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      if (!getShowsDialog()) {
         TextView textView = (TextView) inflater.inflate(R.layout.fragment_message_fallback, container, false);
         textView.setText(messageRes);
         return textView;
      }
      return super.onCreateView(inflater, container, savedInstanceState);
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

   public void setMessageRes(@StringRes int messageRes) {
      this.messageRes = messageRes;
   }

   @NonNull
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      return new MaterialDialog.Builder(getActivity()).content(messageRes).positiveText(R.string.ok).build();
   }
}
