package com.worldventures.wallet.ui.settings.general.newcard.detection;

import com.worldventures.wallet.domain.entity.ConnectionStatus;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.general.reset.delegate.FactoryResetView;

public interface ExistingCardDetectScreen extends WalletScreen, FactoryResetView {

   void setSmartCardId(String scId);

   void setSmartCardConnection(ConnectionStatus connection);

   void showConfirmationUnassignDialog();

   void showConfirmationUnassignWhioutCard();
}
