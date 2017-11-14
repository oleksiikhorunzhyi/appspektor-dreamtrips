package com.worldventures.wallet.ui.settings.general.about;

import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

public interface AboutScreen extends WalletScreen {

   void onProvidePayCardInfo(final int cardStored, final int cardAvailable);

   void setSmartCardId(String smartCardId);

   void setSmartCardFirmware(SmartCardFirmware smartCardFirmware);

   void setSmartCardUser(SmartCardUser smartCardUser);
}