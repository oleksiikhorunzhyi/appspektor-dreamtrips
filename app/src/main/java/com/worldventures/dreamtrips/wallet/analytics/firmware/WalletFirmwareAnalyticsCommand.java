package com.worldventures.dreamtrips.wallet.analytics.firmware;

import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.analytics.firmware.action.FirmwareAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

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