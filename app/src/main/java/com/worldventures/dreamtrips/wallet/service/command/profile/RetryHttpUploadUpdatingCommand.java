package com.worldventures.dreamtrips.wallet.service.command.profile;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class RetryHttpUploadUpdatingCommand extends Command<SmartCardUser> implements InjectableAction {

   @Inject UpdateProfileManager updateProfileManager;

   @Override
   protected void run(CommandCallback<SmartCardUser> callback) throws Throwable {
      updateProfileManager.retryUploadData()
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
