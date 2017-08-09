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

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletHelpSettingsScreenImpl extends WalletBaseController<WalletHelpSettingsScreen, WalletHelpSettingsPresenter> implements WalletHelpSettingsScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   @Inject WalletHelpSettingsPresenter presenter;

   private BottomSheetDialog bottomSheetFeedbackDialog;

   public WalletHelpSettingsScreenImpl() {
   }

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      initBottomSheetFeedbackDialog();
   }

   private void initBottomSheetFeedbackDialog() {
      bottomSheetFeedbackDialog = new BottomSheetDialog(getContext());

      ViewDataBinding bottomSheetView = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.wallet_bottom_sheet_feedback_dialog, (ViewGroup) getView(), false);
      bottomSheetView.setVariable(BR.presenter, getPresenter());
      bottomSheetView.setVariable(BR.bottomSheetDialog, bottomSheetFeedbackDialog);

      bottomSheetFeedbackDialog.setContentView(bottomSheetView.getRoot());
   }

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @OnClick(R.id.item_documents)
   void onClickDocuments() {
      getPresenter().openDocumentsScreen();
   }

   @OnClick(R.id.item_videos)
   void onClickVideos() {
      getPresenter().openVideoScreen();
   }

   @OnClick(R.id.item_send_feedback)
   void onClickSendFeedback() {
      getPresenter().handleVariantFeedback();
   }

   @OnClick(R.id.item_customer_support)
   void onClickCustomerSupport() {
      getPresenter().openCustomerSupportScreen();
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
