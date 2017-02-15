package com.worldventures.dreamtrips.wallet.ui.settings.newcard.poweron;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;

import butterknife.InjectView;

public class NewCardPowerOnScreen extends WalletLinearLayout<NewCardPowerOnPresenter.Screen, NewCardPowerOnPresenter, NewCardPowerOnPath> implements NewCardPowerOnPresenter.Screen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.tv_new_card_power_on_label) TextView powerOnLabel;

   public NewCardPowerOnScreen(Context context) {
      super(context);
   }

   public NewCardPowerOnScreen(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   protected boolean hasToolbar() {
      return false;
   }

   @Override
   public NewCardPowerOnPresenter createPresenter() {
      return new NewCardPowerOnPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   public void setTitleWithSmartCard(String scID) {
      powerOnLabel.setText(getString(R.string.wallet_new_card_power_on, scID));
   }
}
