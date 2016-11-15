package com.worldventures.dreamtrips.modules.dtl.model.mapping;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Attribute;

import java.util.List;

import rx.Observable;

public class AttributesSortTransformer implements Observable.Transformer<List<Attribute>, List<Attribute>> {

   public static AttributesSortTransformer create() {
      return new AttributesSortTransformer();
   }

   @Override
   public Observable<List<Attribute>> call(Observable<List<Attribute>> sourceList) {
      return sourceList
            .flatMap(Observable::from)
            .toSortedList((attributeLeft, attributeRight) ->
                  attributeLeft.displayName().compareToIgnoreCase(attributeRight.displayName()));
   }
}
