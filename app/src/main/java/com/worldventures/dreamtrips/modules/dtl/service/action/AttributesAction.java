package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.dtl.attributes.AttributesHttpAction;
import com.worldventures.dreamtrips.api.dtl.attributes.model.AttributeType;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.AttributeMapper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class AttributesAction extends Command<List<Attribute>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject DtlFilterMerchantInteractor filterInteractor;
   @Inject DtlLocationInteractor dtlLocationInteractor;

   @Override
   protected void run(CommandCallback<List<Attribute>> callback) throws Throwable {
      callback.onProgress(0);
      Observable.combineLatest(
            dtlLocationInteractor.locationPipe()
                  .observeSuccessWithReplay()
                  .take(1)
                  .map(dtlLocationCommand -> dtlLocationCommand.getResult().getCoordinates()),
            filterInteractor.filterDataPipe()
                  .observeSuccessWithReplay()
                  .take(1)
                  .map(DtlFilterDataAction::getResult),
            (location, dtlFilterData) ->
                     new AttributesHttpAction(location.getLat() + "," + location.getLng(),
                        dtlFilterData.getMaxDistance(), AttributeType.AMENITY.toString().toLowerCase()))
            .flatMap(attributesHttpAction -> janet.createPipe(AttributesHttpAction.class)
                  .createObservableResult(attributesHttpAction)
                  .map(AttributesHttpAction::attributes)
                  .compose(new AttributeMapper()))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
