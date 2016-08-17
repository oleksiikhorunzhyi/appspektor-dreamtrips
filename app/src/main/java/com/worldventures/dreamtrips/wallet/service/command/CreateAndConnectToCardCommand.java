package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.ImmutableSmartCard;
import com.worldventures.dreamtrips.wallet.domain.entity.Provision;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.util.WalletValidateHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateToActionTransformer;

@CommandAction
public class CreateAndConnectToCardCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;
   @Inject SnappyRepository snappyRepository;
   @Inject SmartCardInteractor smartCardInteractor;

   private String code;

   public CreateAndConnectToCardCommand(String code) {
      this.code = code;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      WalletValidateHelper.validateSCIdOrThrow(code);
      janet.createPipe(CreateCardHttpAction.class)
            .createObservable(new CreateCardHttpAction(code))
            .compose(new ActionStateToActionTransformer<>())
            .map(httpAction -> createSmartCard(httpAction.getResponse()))
            .doOnNext(snappyRepository::saveSmartCard)
            .flatMap(smartCard -> smartCardInteractor.connectActionPipe()
                  .createObservableResult(new ConnectSmartCardCommand(smartCard)))
            .subscribe(connectCommand -> callback.onSuccess(null), callback::onFail);
   }

   public SmartCard createSmartCard(Provision provision) {
      return ImmutableSmartCard.builder()
            .deviceName(provision.memberId())
            .deviceAddress(provision.userSecret())
            .smartCardId(code)
            .cardStatus(SmartCard.CardStatus.DRAFT)
            .build();
   }

   public String getCode() {
      return code;
   }
}