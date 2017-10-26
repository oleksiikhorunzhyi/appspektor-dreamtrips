package com.worldventures.wallet.ui.settings.general.firmware.newavailable;

import android.content.Context;
import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.api.smart_card.firmware.model.FirmwareInfo;
import com.worldventures.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletNewFirmwareAvailableScreen extends WalletScreen {

   OperationView<FetchFirmwareUpdateDataCommand> provideOperationView();

   void currentFirmwareInfo(@Nullable SmartCardFirmware version, FirmwareInfo firmwareInfo, boolean isCompatible);

   void insufficientSpace(long missingByteSpace);

   Context getViewContext();
}
