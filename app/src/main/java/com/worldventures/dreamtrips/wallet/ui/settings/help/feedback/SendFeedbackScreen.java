package com.worldventures.dreamtrips.wallet.ui.settings.help.feedback;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

public class SendFeedbackScreen extends WalletLinearLayout<SendFeedbackPresenter.Screen, SendFeedbackPresenter, SendFeedbackPath> implements SendFeedbackPresenter.Screen {

   public SendFeedbackScreen(Context context) {
      super(context);
   }

   public SendFeedbackScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return true;
   }

   @Override
   public SendFeedbackPresenter createPresenter() {
      return new SendFeedbackPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }
}
