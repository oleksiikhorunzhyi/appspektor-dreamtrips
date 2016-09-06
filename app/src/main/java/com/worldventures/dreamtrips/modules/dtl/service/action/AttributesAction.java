package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.api.dtl.attributes.AttributesHttpAction;
import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.AttributeMapper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class AttributesAction extends Command<List<Attribute>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   @Nullable private final String ll;
   @Nullable private final Double radius;

   private AttributesAction(String ll, Double radius) {
      this.ll = ll;
      this.radius = radius;
   }

   public static AttributesAction load(String ll, Double radius) {
      return new AttributesAction(ll, radius);
   }

   @Override
   protected void run(CommandCallback<List<Attribute>> callback) throws Throwable {
      janet.createPipe(AttributesHttpAction.class)
            .createObservableResult(
                  new AttributesHttpAction(ll, radius, AttributeType.AMENITY.toString().toLowerCase()))
            .map(AttributesHttpAction::attributes)
            .compose(new AttributeMapper())
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
