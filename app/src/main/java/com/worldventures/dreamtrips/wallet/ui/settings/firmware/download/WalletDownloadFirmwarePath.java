package com.worldventures.dreamtrips.wallet.ui.settings.firmware.download;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_download_firmware)
public class WalletDownloadFirmwarePath extends StyledPath {

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }
}