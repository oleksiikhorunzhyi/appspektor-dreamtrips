package com.worldventures.dreamtrips.wallet.service.command.http;


import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.profile.GetCurrentUserAddressHttpAction;
import com.worldventures.dreamtrips.api.profile.model.AddressType;
import com.worldventures.dreamtrips.api.profile.model.ProfileAddress;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_API_LIB;

@CommandAction
public class FetchAndStoreDefaultAddressInfoCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject SnappyRepository snappyRepository;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      janet.createPipe(GetCurrentUserAddressHttpAction.class)
            .createObservableResult(new GetCurrentUserAddressHttpAction())
            .map(this::parse)
            .doOnNext(addressInfo -> snappyRepository.saveDefaultAddress(addressInfo))
            .map(it -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Nullable
   private AddressInfo parse(GetCurrentUserAddressHttpAction it) {
      List<ProfileAddress> response = it.response();
      if (response.isEmpty()) return null;
      ProfileAddress address = Queryable.from(response).firstOrDefault(i -> i.type() == AddressType.BILLING);
      return mapperyContext.convert(address, AddressInfo.class);
   }
}
