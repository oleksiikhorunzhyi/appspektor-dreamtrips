package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCard;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RetryHttpUploadUpdatingCommand extends Command<SmartCard> implements InjectableAction {

   @Inject UpdateProfileManager updateProfileManager;

   @Override
   protected void run(CommandCallback<SmartCard> callback) throws Throwable {
      updateProfileManager.retryUploadData()
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
