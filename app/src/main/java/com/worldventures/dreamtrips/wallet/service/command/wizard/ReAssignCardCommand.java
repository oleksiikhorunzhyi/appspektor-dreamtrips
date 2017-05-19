package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.api.smart_card.user_association.UpdateDeviceIdHttpAction;
import com.worldventures.dreamtrips.api.smart_card.user_association.model.SmartCardInfo;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.record.SyncRecordsStatus;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordStatusCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class ReAssignCardCommand extends Command<Void> implements InjectableAction {

   @Inject Janet janet;
   @Inject RecordInteractor recordInteractor;
   @Inject @Named(JanetModule.JANET_WALLET) Janet janetWallet;
   @Inject SystemPropertiesProvider systemPropertiesProvider;
   @Inject SnappyRepository snappyRepository;
   @Inject MapperyContext mappery;

   private final String scId;

   public ReAssignCardCommand(String scId) {
      this.scId = scId;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      updateDeviceId()
            .flatMap(smartCardInfo -> new ProcessSmartCardInfoDelegate(snappyRepository, janetWallet, mappery)
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
