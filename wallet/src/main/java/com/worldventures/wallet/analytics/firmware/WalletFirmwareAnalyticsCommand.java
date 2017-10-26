package com.worldventures.wallet.analytics.firmware;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.analytics.firmware.action.FirmwareAnalyticsAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.firmware.FirmwareRepository;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class WalletFirmwareAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject FirmwareRepository firmwareRepository;
   @Inject WalletAnalyticsInteractor analyticsInteractor;

   private final FirmwareAnalyticsAction action;

   public WalletFirmwareAnalyticsCommand(FirmwareAnalyticsAction action) {
      this.action = action;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      FirmwareUpdateData data = firmwareRepository.getFirmwareUpdateData();
      if (data != null) {
         action.setFirmwareData(data);
      }
      analyticsInteractor.analyticsActionPipe().send(action);
   }
}