package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.dtl.attributes.AttributesHttpAction;
import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.AttributesSortTransformer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class AttributesAction extends Command<List<Attribute>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private final String ll;
   private final double radius;
   private final List<String> attributeTypes;

   public AttributesAction(String ll, double radius) {
      this.ll = ll;
      this.radius = radius;
      this.attributeTypes = Arrays.asList(AttributeType.AMENITY.toString().toLowerCase(Locale.US));
   }

   @Override
   protected void run(CommandCallback<List<Attribute>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(AttributesHttpAction.class)
            .createObservableResult(new AttributesHttpAction(ll, radius, attributeTypes))
            .map(AttributesHttpAction::attributes)
            .map(attributes -> mapperyContext.convert(attributes, Attribute.class))
            .compose(new AttributesSortTransformer())
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
