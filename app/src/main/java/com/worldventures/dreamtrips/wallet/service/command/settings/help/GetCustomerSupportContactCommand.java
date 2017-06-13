package com.worldventures.dreamtrips.wallet.service.command.settings.help;

import com.worldventures.dreamtrips.api.smart_card.documents.customer_support.GetCustomerSupportContactsHttpAction;
import com.worldventures.dreamtrips.core.janet.cache.CacheOptions;
import com.worldventures.dreamtrips.core.janet.cache.CachedAction;
import com.worldventures.dreamtrips.core.janet.cache.ImmutableCacheOptions;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.settings.customer_support.Contact;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.ActionHolder;
import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetCustomerSupportContactCommand extends Command<Contact> implements InjectableAction, CachedAction<Contact> {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   private Contact cachedResult;

   @Override
   protected void run(CommandCallback<Contact> callback) throws Throwable {
      if (!needApiRequest()) {
         callback.onSuccess(cachedResult);
         return;
      }

      janet.createPipe(GetCustomerSupportContactsHttpAction.class)
            .createObservableResult(new GetCustomerSupportContactsHttpAction())
            .map(GetCustomerSupportContactsHttpAction::response)
            .map(this::convert)
            .map(contacts -> {
               if (contacts != null && contacts.size() > 0) {
                  return contacts.get(0);
               }
               throw new NullPointerException("Server response must contain at least one contact");
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   protected List<Contact> convert(Iterable<?> itemsToConvert) {
      return mapperyContext.convert(itemsToConvert, Contact.class);
   }

   private boolean needApiRequest() {
      return cachedResult == null;
   }

   @Override
   public Contact getCacheData() {
      return getResult();
   }

   @Override
   public void onRestore(ActionHolder holder, Contact cache) {
      cachedResult = cache;
   }

   @Override
   public CacheOptions getCacheOptions() {
      return ImmutableCacheOptions.builder()
            .saveToCache(needApiRequest())
            .build();
   }
}