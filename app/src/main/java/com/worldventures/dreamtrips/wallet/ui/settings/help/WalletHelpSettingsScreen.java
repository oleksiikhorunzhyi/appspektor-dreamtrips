package com.worldventures.dreamtrips.wallet.ui.settings.help;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.delegate.DialogOperationScreen;

import butterknife.InjectView;
import butterknife.OnClick;

public class WalletHelpSettingsScreen extends WalletLinearLayout<WalletHelpSettingsPresenter.Screen, WalletHelpSettingsPresenter, WalletHelpSettingsPath> implements WalletHelpSettingsPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;

   public WalletHelpSettingsScreen(Context context) {
      this(context, null);
   }

   public WalletHelpSettingsScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
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
      return new DialogOperationScreen(this);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @OnClick(R.id.item_documents)
   void onClickDocuments() {
      presenter.openDocuments();
   }

   @OnClick(R.id.item_videos)
   void onClickVideos() {
      getPresenter().openVideoSection();
   }

   @OnClick(R.id.item_send_feedback)
   void onClickSendFeedback() {
      getPresenter().openSendFeedbackSection();
   }

   @OnClick(R.id.item_customer_support)
   void onClickCustomerSupport() {

   }
}
