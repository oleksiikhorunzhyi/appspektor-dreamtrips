package com.worldventures.dreamtrips.wallet.analytics.tokenization;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletCoordinates;
import com.worldventures.dreamtrips.wallet.domain.entity.lostcard.WalletLocation;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardRepository;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class TokenizationAnalyticsLocationCommand extends WalletAnalyticsCommand {

   @Inject LostCardRepository lostCardRepository;

   private final TokenizationCardAction tokenizationCardAction;

   public TokenizationAnalyticsLocationCommand(TokenizationCardAction tokenizationCardAction) {
      super(tokenizationCardAction);
      this.tokenizationCardAction = tokenizationCardAction;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      tokenizationCardAction.setCoordinates(getLastKnownCoordinates());
      super.run(callback);
   }

   @Nullable
   private WalletCoordinates getLastKnownCoordinates() {
      List<WalletLocation> locations = lostCardRepository.getWalletLocations();
      WalletLocation lastKnownLocation = (locations == null || locations.isEmpty()) ?
            null : locations.get(locations.size() - 1);
      return (lastKnownLocation == null) ? null : lastKnownLocation.coordinates();
   }

}