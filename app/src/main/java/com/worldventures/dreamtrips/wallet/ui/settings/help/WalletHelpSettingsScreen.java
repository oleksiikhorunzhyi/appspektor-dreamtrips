package com.worldventures.dreamtrips.wallet.ui.settings.help;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.worldventures.dreamtrips.BR;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletHelpSettingsScreen extends WalletLinearLayout<WalletHelpSettingsPresenter.Screen, WalletHelpSettingsPresenter, WalletHelpSettingsPath> implements WalletHelpSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   private BottomSheetDialog bottomSheetFeedbackDialog;

   public WalletHelpSettingsScreen(Context context) {
      this(context, null);
   }

   public WalletHelpSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (isInEditMode()) return;
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      initBottomSheetFeedbackDialog();
   }

   private void initBottomSheetFeedbackDialog() {
      bottomSheetFeedbackDialog = new BottomSheetDialog(getContext());

      ViewDataBinding bottomSheetView = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.wallet_bottom_sheet_feedback_dialog, this, false);
      bottomSheetView.setVariable(BR.presenter, presenter);
      bottomSheetView.setVariable(BR.bottomSheetDialog, bottomSheetFeedbackDialog);

      bottomSheetFeedbackDialog.setContentView(bottomSheetView.getRoot());
   }

   @NonNull
   @Override
   public WalletHelpSettingsPresenter createPresenter() {
      return new WalletHelpSettingsPresenter(getContext(), getInjector());
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @OnClick(R.id.item_documents)
   void onClickDocuments() {
      presenter.openDocumentsScreen();
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
}
