package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RetryHttpUploadUpdatingCommand extends Command<Void> implements InjectableAction {

   @Inject UpdateProfileManager updateProfileManager;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      updateProfileManager.retryUploadData()
            .subscribe(smartCard -> callback.onSuccess(null), callback::onFail);
   }
}
