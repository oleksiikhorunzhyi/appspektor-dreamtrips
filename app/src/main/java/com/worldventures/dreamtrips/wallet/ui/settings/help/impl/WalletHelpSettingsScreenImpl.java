package com.worldventures.dreamtrips.wallet.ui.settings.help.impl;


import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.BR;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.settings.help.WalletHelpSettingsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.WalletHelpSettingsScreen;

import javax.inject.Inject;

public class WalletHelpSettingsScreenImpl extends WalletBaseController<WalletHelpSettingsScreen, WalletHelpSettingsPresenter> implements WalletHelpSettingsScreen {

   @Inject WalletHelpSettingsPresenter presenter;

   private BottomSheetDialog bottomSheetFeedbackDialog;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      final View itemDocumentsView = view.findViewById(R.id.item_documents);
      itemDocumentsView.setOnClickListener(documents -> getPresenter().openDocumentsScreen());
      final View itemVideosView = view.findViewById(R.id.item_videos);
      itemVideosView.setOnClickListener(videos -> getPresenter().openVideoScreen());
      final View itemSendFeedbackView = view.findViewById(R.id.item_send_feedback);
      itemSendFeedbackView.setOnClickListener(sendFeedback -> getPresenter().handleVariantFeedback());
      final View itemCustomerSupportView = view.findViewById(R.id.item_customer_support);
      itemCustomerSupportView.setOnClickListener(customerSupport -> getPresenter().openCustomerSupportScreen());
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      initBottomSheetFeedbackDialog();
   }

   private void initBottomSheetFeedbackDialog() {
      bottomSheetFeedbackDialog = new BottomSheetDialog(getContext());

      ViewDataBinding bottomSheetView = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_wallet_bottom_sheet_feedback, (ViewGroup) getView(), false);
      bottomSheetView.setVariable(BR.presenter, getPresenter());
      bottomSheetView.setVariable(BR.bottomSheetDialog, bottomSheetFeedbackDialog);

      bottomSheetFeedbackDialog.setContentView(bottomSheetView.getRoot());
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public void showBottomFeedbackMenu() {
      bottomSheetFeedbackDialog.show();
   }

   @Override
   public void hideBottomFeedbackMenu() {
      bottomSheetFeedbackDialog.cancel();
   }

   @Override
   public WalletHelpSettingsPresenter getPresenter() {
      return presenter;
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_settings_help, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return true;
   }
}
