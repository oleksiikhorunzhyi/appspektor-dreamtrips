package com.worldventures.wallet.analytics.locatecard;

import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.locatecard.action.BaseLocateSmartCardAction;
import com.worldventures.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.wallet.service.lostcard.LostCardRepository;
import com.worldventures.wallet.util.WalletLocationsUtil;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class LocateCardAnalyticsCommand extends WalletAnalyticsCommand {

   @Inject LostCardRepository lostCardRepository;

   private final BaseLocateSmartCardAction baseLocateSmartCardAction;

   public LocateCardAnalyticsCommand(BaseLocateSmartCardAction baseLocateSmartCardAction) {
      super(baseLocateSmartCardAction);
      this.baseLocateSmartCardAction = baseLocateSmartCardAction;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      attachLocation();
      super.run(callback);
   }

   private void attachLocation() {
      List<WalletLocation> locations = lostCardRepository.getWalletLocations();
      WalletLocation lastKnownLocation = WalletLocationsUtil.getLatestLocation(locations);
      if (lastKnownLocation != null) {
         baseLocateSmartCardAction.setLocation(lastKnownLocation.coordinates());
      }
   }
}
