package com.worldventures.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_association.GetAssociatedCardsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.SmartCard;
import com.worldventures.wallet.domain.entity.SmartCardDetails;
import com.worldventures.wallet.domain.entity.SmartCardUser;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.service.SystemPropertiesProvider;
import com.worldventures.wallet.service.command.ConnectSmartCardCommand;

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

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class FetchAssociatedSmartCardCommand extends Command<FetchAssociatedSmartCardCommand.AssociatedCard> implements InjectableAction {

   @Inject Janet janet;
   @Inject @Named(JANET_WALLET) Janet janetWallet;

   @Inject SystemPropertiesProvider propertiesProvider;
   @Inject WalletStorage walletStorage;
   @Inject MapperyContext mappery;

   @Override
   protected void run(CommandCallback<FetchAssociatedSmartCardCommand.AssociatedCard> callback) throws Throwable {
      SmartCard smartCard = getSmartCardFromCache();
      SmartCardUser user = getSmartCardUserFromCache();
      if (smartCard != null && smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE && user != null
            // TODO: this check for backward compatibility 1.21 -> 1.23
            && (user.userPhoto() != null && user.userPhoto().uri() != null)) {
         callback.onSuccess(createAssociatedCard(smartCard, walletStorage.getSmartCardDetails()));
         return;
      }
      janet.createPipe(GetAssociatedCardsHttpAction.class)
            .createObservableResult(new GetAssociatedCardsHttpAction(propertiesProvider.deviceId()))
            .flatMap(action -> handleResponse(action.response()))
            .doOnNext(result -> {
               if (result.exist()) {
                  janetWallet.createPipe(ConnectSmartCardCommand.class)
                        .send(new ConnectSmartCardCommand(result.smartCard().smartCardId()));
               }
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SmartCard getSmartCardFromCache() {
      return walletStorage.getSmartCard();
   }

   private SmartCardUser getSmartCardUserFromCache() {
      return walletStorage.getSmartCardUser();
   }


   private Observable<FetchAssociatedSmartCardCommand.AssociatedCard> handleResponse(List<SmartCardInfo> listSmartCardInfo) {
      if (listSmartCardInfo.isEmpty()) {
         return Observable.just(ImmutableAssociatedCard.of(false));
      }
      final SmartCardInfo smartCardInfo = listSmartCardInfo.get(0);

      return new ProcessSmartCardInfoDelegate(walletStorage, janetWallet, mappery)
            .processSmartCardInfo(smartCardInfo)
            .map(result -> createAssociatedCard(result.smartCard, result.details));
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
