package com.worldventures.wallet.ui.settings.security.clear.default_card.impl;

import android.support.annotation.Nullable;

import com.worldventures.wallet.R;
import com.worldventures.wallet.ui.settings.security.clear.common.base.WalletBaseClearDelayScreenImpl;
import com.worldventures.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardPresenter;
import com.worldventures.wallet.ui.settings.security.clear.default_card.WalletDisableDefaultCardScreen;

import javax.inject.Inject;

public class WalletDisableDefaultCardScreenImpl extends WalletBaseClearDelayScreenImpl<WalletDisableDefaultCardScreen, WalletDisableDefaultCardPresenter> implements WalletDisableDefaultCardScreen {

   @Inject WalletDisableDefaultCardPresenter presenter;

   @Override
   public WalletDisableDefaultCardPresenter getPresenter() {
      return presenter;
   }

   @Override
   protected int getTitle() {
      return R.string.wallet_settings_disable_default_card_title;
   }

   @Override
   protected int getHeader() {
      return R.string.wallet_settings_disable_default_card_description;
   }

   @Override
   protected int getSuccessMessage() {
      return R.string.wallet_settings_disable_default_card_delay_updated;
   }

   @Nullable
   @Override
   protected Object screenModule() {
      return new WalletDisableDefaultCardScreenModule();
   }
}
