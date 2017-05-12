package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardDetails;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhone;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUserPhoto;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;

import java.io.File;

import io.techery.janet.Janet;
import io.techery.mappery.MapperyContext;
import rx.Observable;

class ProcessSmartCardInfoDelegate {

   private final SnappyRepository snappyRepository;
   private final Janet janetWallet;
   private MapperyContext mappery;

   ProcessSmartCardInfoDelegate(SnappyRepository snappyRepository, Janet janetWallet, MapperyContext mappery) {
      this.snappyRepository = snappyRepository;
      this.janetWallet = janetWallet;
      this.mappery = mappery;
   }

   Observable<Result> processSmartCardInfo(SmartCardInfo smartCardInfo) {
      final SmartCardDetails details = mappery.convert(smartCardInfo, SmartCardDetails.class);
      final SmartCard smartCard = createSmartCard(smartCardInfo);

      return createSmartCardUser(smartCardInfo.user())
            .map(user -> new Result(smartCard, user, details))
            .flatMap(result -> save(result).map(aVoid -> result));
   }

   private Observable<SmartCardUser> createSmartCardUser(com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardUser user) {
      final ImmutableSmartCardUser.Builder userBuilder = ImmutableSmartCardUser.builder()
            .firstName(user.firstName() != null ? user.firstName() : "")
            .middleName(user.middleName() != null ? user.middleName() : "")
            .lastName(user.lastName() != null ? user.lastName() : "");

      if (user.phone() != null) userBuilder.phoneNumber(mappery.convert(user.phone(), SmartCardUserPhone.class));

      final SmartCardUser scUser = userBuilder.build();

      if (user.displayPhoto() != null) {
         final String photoUrl = user.displayPhoto();
         return janetWallet.createPipe(SmartCardAvatarCommand.class)
               .createObservableResult(SmartCardAvatarCommand.fromUrl(photoUrl))
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
      final File originalFile = smartCardUserPhoto.original();
      if (originalFile != null) {
         return ImmutableSmartCardUser.builder()
               .from(user)
               .userPhoto(smartCardUserPhoto)
               .build();
      } else {
         return user;
      }
   }

   private Observable<Void> save(Result result) {
      snappyRepository.saveSmartCardDetails(result.details);
      snappyRepository.saveSmartCardUser(result.user);

      //saving smart card
      return janetWallet.createPipe(ActiveSmartCardCommand.class)
            .createObservableResult(new ActiveSmartCardCommand(result.smartCard))
            .map(command -> null);
   }

   static final class Result {
      public final SmartCard smartCard;
      public final SmartCardUser user;
      public final SmartCardDetails details;

      Result(SmartCard smartCard, SmartCardUser user, SmartCardDetails details) {
         this.smartCard = smartCard;
         this.user = user;
         this.details = details;
      }
   }
}
