package com.worldventures.dreamtrips.wallet.service.command.http;

import com.worldventures.dreamtrips.api.smart_card.association_info.GetAssociatedCardsHttpAction;
import com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardInfo;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class FetchAssociatedSmartCardCommand extends Command<FetchAssociatedSmartCardCommand.AssociatedCard> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject @Named(JanetModule.JANET_WALLET) Janet janetWallet;

   @Inject SystemPropertiesProvider propertiesProvider;
   @Inject SnappyRepository snappyRepository;
   @Inject MapperyContext mappery;

   @Override
   protected void run(CommandCallback<FetchAssociatedSmartCardCommand.AssociatedCard> callback) throws Throwable {
      SmartCard smartCard = getSmartCardFromCache();
      if (smartCard != null && smartCard.cardStatus() == SmartCard.CardStatus.ACTIVE) {
         callback.onSuccess(createAssociatedCard(smartCard, snappyRepository.getSmartCardDetails()));
         return;
      }
      janet.createPipe(GetAssociatedCardsHttpAction.class)
            .createObservableResult(new GetAssociatedCardsHttpAction(propertiesProvider.deviceId()))
            .flatMap(action -> handleResponse(action.response()))
            .doOnNext(result -> {
               if (result.exist()) {
                  janetWallet.createPipe(ConnectSmartCardCommand.class)
                        .send(new ConnectSmartCardCommand(result.smartCard().smartCardId(), true));
               }
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private SmartCard getSmartCardFromCache() {
      return snappyRepository.getSmartCard();
   }

   private Observable<FetchAssociatedSmartCardCommand.AssociatedCard> handleResponse(List<SmartCardInfo> listSmartCardInfo) {
      if (listSmartCardInfo.isEmpty()) return Observable.just(ImmutableAssociatedCard.of(false));
      final SmartCardInfo smartCardInfo = listSmartCardInfo.get(0);

      final SmartCardDetails smartCardDetails = mappery.convert(smartCardInfo, SmartCardDetails.class);
      final SmartCard smartCard = createSmartCard(smartCardInfo);

      return createSmartCardUser(smartCardInfo)
            .flatMap(user -> save(smartCard, user, smartCardDetails));
   }

   private Observable<SmartCardUser> createSmartCardUser(SmartCardInfo smartCardInfo) {
      final com.worldventures.dreamtrips.api.smart_card.association_info.model.SmartCardUser user = smartCardInfo.user();
      final SmartCardUser scUser = ImmutableSmartCardUser.builder()
            .firstName(user.firstName())
            .middleName(user.middleName() != null ? user.middleName() : "")
            .lastName(user.lastName() != null ? user.lastName() : "")
            .build();

      if (user.displayPhoto() != null) {
         final String photoUrl = user.displayPhoto();
         return janetWallet.createPipe(CompressImageForSmartCardCommand.class)
               .createObservableResult(new CompressImageForSmartCardCommand(photoUrl))
               .map(command -> changeUserPhoto(scUser, command.getResult()));
      } else {
         return Observable.just(scUser);
      }
   }

   private SmartCard createSmartCard(SmartCardInfo smartCardInfo) {
      return ImmutableSmartCard.builder()
            .smartCardId(String.valueOf(smartCardInfo.scId()))
            .deviceId(smartCardInfo.deviceId())
            .cardStatus(SmartCard.CardStatus.ACTIVE)
            .build();
   }

   private SmartCardUser changeUserPhoto(SmartCardUser user, SmartCardUserPhoto smartCardUserPhoto) {
      final File monochromeFile = smartCardUserPhoto.monochrome();
      if (monochromeFile != null) {
         return ImmutableSmartCardUser.builder()
               .from(user)
               .userPhoto(smartCardUserPhoto)
               .build();
      } else {
         return user;
      }
   }

   private Observable<AssociatedCard> save(SmartCard smartCard, SmartCardUser user, SmartCardDetails smartCardDetails) {
      snappyRepository.saveSmartCard(smartCard);
      snappyRepository.saveSmartCardDetails(smartCardDetails);
      snappyRepository.saveSmartCardUser(user);

      return Observable.just(createAssociatedCard(smartCard, smartCardDetails));
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
