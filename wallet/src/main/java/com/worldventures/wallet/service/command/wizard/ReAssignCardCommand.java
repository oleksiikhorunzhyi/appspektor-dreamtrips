package com.worldventures.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_association.UpdateDeviceIdHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SystemPropertiesProvider;
import com.worldventures.wallet.service.command.record.SyncRecordStatusCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@CommandAction
public class ReAssignCardCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject RecordInteractor recordInteractor;
   @Inject @Named(JANET_WALLET) Janet janetWallet;
   @Inject SystemPropertiesProvider systemPropertiesProvider;
   @Inject MapperyContext mappery;

   private final String scId;

   public ReAssignCardCommand(String scId) {
      this.scId = scId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      updateDeviceId()
            .flatMap(smartCardInfo -> new ProcessSmartCardInfoDelegate(janetWallet, mappery)
                  .processSmartCardInfo(smartCardInfo))
            .subscribe(result -> {
               recordInteractor.syncRecordStatusPipe() // set default value for this flow
                     .send(SyncRecordStatusCommand.save(SyncRecordsStatus.FAIL_AFTER_PROVISION));
               callback.onSuccess(null);
            }, callback::onFail);
   }

   private Observable<SmartCardInfo> updateDeviceId() {
      return janet.createPipe(UpdateDeviceIdHttpAction.class)
            .createObservableResult(new UpdateDeviceIdHttpAction(scId, systemPropertiesProvider.deviceId()))
            .map(UpdateDeviceIdHttpAction::response);
   }
}
