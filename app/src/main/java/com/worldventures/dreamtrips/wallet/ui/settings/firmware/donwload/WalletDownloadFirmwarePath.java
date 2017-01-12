package com.worldventures.dreamtrips.wallet.ui.settings.firmware.donwload;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

@Layout(R.layout.screen_wallet_download_firmware)
public class WalletDownloadFirmwarePath extends StyledPath {

   private final FirmwareInfo firmwareInfo;
   private final String filePath;
   private final SmartCard smartCard;
   private final FirmwareUpdateData firmwareUpdateData;

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   public WalletDownloadFirmwarePath(SmartCard smartCard, FirmwareUpdateData firmwareUpdateData, FirmwareInfo firmwareInfo, String filePath) {
      this.firmwareInfo = firmwareInfo;
      this.filePath = filePath;
      this.smartCard = smartCard;
      this.firmwareUpdateData = firmwareUpdateData;
   }

   public FirmwareUpdateData firmwareUpdateData() {
      return firmwareUpdateData;
   }

   public FirmwareInfo firmwareInfo() {
      return firmwareInfo;
   }

   public String filePath() {
      return filePath;
   }

   public SmartCard smartCard() {
      return smartCard;
   }
}