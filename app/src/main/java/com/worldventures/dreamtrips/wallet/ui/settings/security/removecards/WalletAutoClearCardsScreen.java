package com.worldventures.dreamtrips.wallet.ui.settings.security.removecards;

import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SettingsRadioModel;

import java.util.List;

public interface WalletAutoClearCardsScreen extends WalletScreen {

   void setItems(List<SettingsRadioModel> items);

   void setSelectedPosition(int position);

   int getSelectedPosition();

   String getTextBySelectedModel(SettingsRadioModel settingsRadioModel);

}