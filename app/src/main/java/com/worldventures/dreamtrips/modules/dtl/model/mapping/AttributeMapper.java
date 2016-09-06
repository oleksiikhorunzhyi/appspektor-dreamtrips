package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.api.dtl.attributes.model.Attribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ImmutableAttribute;

import java.util.List;

import rx.Observable;

public class AttributeMapper implements Observable.Transformer<List<Attribute>, List<com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute>> {

   @Override
   public Observable<List<com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute>> call(Observable<List<Attribute>> sourceList) {
      return sourceList
            .flatMap(Observable::from)
            .map(this::mapFromApi)
            .toList();
   }

   private com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute mapFromApi(Attribute attribute) {
      return ImmutableAttribute.builder()
            .id(attribute.id())
            .type(attribute.type())
            .name(attribute.displayName())
            .displayName(attribute.displayName())
            .merchantCount(attribute.merchantCount())
            .partnerCount(attribute.partnerCount())
            .build();
   }
}
