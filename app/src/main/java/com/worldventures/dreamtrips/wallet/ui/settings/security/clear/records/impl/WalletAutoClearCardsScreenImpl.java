package com.worldventures.dreamtrips.wallet.ui.settings.security.clear.records.impl;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.common.base.WalletBaseClearDelayScreenImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.records.WalletAutoClearCardsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.security.clear.records.WalletAutoClearCardsScreen;

import javax.inject.Inject;

public class WalletAutoClearCardsScreenImpl extends WalletBaseClearDelayScreenImpl<WalletAutoClearCardsScreen, WalletAutoClearCardsPresenter> implements WalletAutoClearCardsScreen {

   @Inject WalletAutoClearCardsPresenter presenter;

   @Override
   public WalletAutoClearCardsPresenter getPresenter() {
      return presenter;
   }

   @Override
   protected int getTitle() {
      return R.string.wallet_settings_clear_flye_card_title;
   }

   @Override
   protected int getHeader() {
      return R.string.wallet_settings_clear_flye_card_description;
   }

   @Override
   protected int getSuccessMessage() {
      return R.string.wallet_settings_clear_flye_card_delay_updated;
   }
}