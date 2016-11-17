package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.association_info.GetAssociatedCardsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardInfo;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class FetchAssociatedSmartCard extends Command<FetchAssociatedSmartCard.AssociatedCard> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject @Named(JanetModule.JANET_WALLET) Janet janetWallet;

   @Inject SystemPropertiesProvider propertiesProvider;
   @Inject SnappyRepository snappyRepository;
   @Inject MapperyContext mappery;

   @Override
   protected void run(CommandCallback<FetchAssociatedSmartCard.AssociatedCard> callback) throws Throwable {
      SmartCard smartCard = getSmartCardFromCache();
      if (smartCard != null) {
         callback.onSuccess(createAssociatedCard(smartCard, snappyRepository.getSmartCardDetails(smartCard.smartCardId())));
         return;
      }
      janet.createPipe(GetAssociatedCardsHttpAction.class)
            .createObservableResult(new GetAssociatedCardsHttpAction(propertiesProvider.deviceId()))
            .map(action -> handleResponse(action.response()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SmartCard getSmartCardFromCache() {
      String activeSmartCardId = snappyRepository.getActiveSmartCardId();
      if (activeSmartCardId == null) return null;
      return snappyRepository.getSmartCard(activeSmartCardId);
   }

   private FetchAssociatedSmartCard.AssociatedCard handleResponse(List<SmartCardInfo> listSmartCardInfo) {
      if (listSmartCardInfo.isEmpty()) return ImmutableAssociatedCard.of(false);
      SmartCardInfo smartCardInfo = listSmartCardInfo.get(0);

      final SmartCard smartCard = mappery.convert(smartCardInfo, SmartCard.class);
      final SmartCardDetails smartCardDetails = mappery.convert(smartCardInfo, SmartCardDetails.class);

      snappyRepository.setActiveSmartCardId(smartCard.smartCardId());
      snappyRepository.saveSmartCard(smartCard);
      snappyRepository.saveSmartCardDetails(smartCardDetails);

      janetWallet.createPipe(ConnectSmartCardCommand.class).send(new ConnectSmartCardCommand(smartCard, true, true));
      return createAssociatedCard(smartCard, smartCardDetails);
   }

   private AssociatedCard createAssociatedCard(SmartCard smartCard, SmartCardDetails smartCardDetails) {
      return ImmutableAssociatedCard.builder()
            .exist(true)
            .smartCard(smartCard)
            .smartCardDetails(smartCardDetails)
            .build();
   }

   @Value.Immutable
   public static abstract class AssociatedCard {

      @Nullable
      @Value.Default
      public SmartCard smartCard() {
         return null;
      }

      @Nullable
      @Value.Default
      public SmartCardDetails smartCardDetails() {
         return null;
      }

      @Value.Parameter
      public abstract boolean exist();
   }
}
