package com.worldventures.dreamtrips.modules.trips.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CheckTripsByUidCommand extends Command<Boolean> implements InjectableAction {

   @Inject SnappyRepository snappyRepository;

   private List<String> uids;

   public CheckTripsByUidCommand(List<String> uids) {
      this.uids = uids;
   }

   @Override
   protected void run(CommandCallback<Boolean> callback) throws Throwable {
      callback.onSuccess(snappyRepository.hasTripsDetailsForUids(uids));
   }
}
