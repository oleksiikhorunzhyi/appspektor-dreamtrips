package com.worldventures.dreamtrips.wallet.analytics.locatecard;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

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
      baseLocateSmartCardAction.setLocation(fetchLastKnownLocation());
      super.run(callback);
   }

   @Nullable
   private WalletCoordinates fetchLastKnownLocation() {
      List<WalletLocation> locations = lostCardRepository.getWalletLocations();
      WalletLocation lastKnownLocation = (locations == null || locations.isEmpty()) ?
            null : locations.get(locations.size() - 1);
      return (lastKnownLocation == null) ? null : lastKnownLocation.coordinates();
   }
}
