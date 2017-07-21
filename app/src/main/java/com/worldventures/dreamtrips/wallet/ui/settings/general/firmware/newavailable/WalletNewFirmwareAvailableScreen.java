package com.worldventures.dreamtrips.wallet.ui.settings.general.firmware.newavailable;

import android.content.Context;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletNewFirmwareAvailableScreen extends WalletScreen {

   OperationView<FetchFirmwareUpdateDataCommand> provideOperationView();

   void currentFirmwareInfo(@Nullable SmartCardFirmware version, FirmwareInfo firmwareInfo, boolean isCompatible);

   void insufficientSpace(long missingByteSpace);

   Context getViewContext();
}
