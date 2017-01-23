package com.worldventures.dreamtrips.modules.dtl.service.action;

import android.util.Log;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.AttributesSortTransformer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.AttributesActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.AttributesActionCreator;

import java.util.List;
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
   @Inject AttributesActionCreator actionCreator;

   private AttributesActionParams actionParams;
   private List<String> merchantTypes;

   public static AttributesAction create(AttributesActionParams params) {
      return new AttributesAction(params);
   }

   public static AttributesAction create(AttributesActionParams params, List<String> merchantTypes) {
      return new AttributesAction(params, merchantTypes);
   }

   public AttributesAction(AttributesActionParams params) {
      this.actionParams = params;
   }

   public AttributesAction(AttributesActionParams params, List<String> merchantTypes) {
      this.actionParams = params;
      this.merchantTypes = merchantTypes;
   }

   @Override
   protected void run(CommandCallback<List<Attribute>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(CategoryAttributesHttpAction.class)
            .createObservableResult(actionCreator.createAction(actionParams, merchantTypes))
            .map(CategoryAttributesHttpAction::attributes)
            .map(attributes -> mapperyContext.convert(attributes, Attribute.class))
            .compose(new AttributesSortTransformer())
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
