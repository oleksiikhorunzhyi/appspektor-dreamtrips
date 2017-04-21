package com.worldventures.dreamtrips.modules.bucketlist.view.adapter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListSuggestionsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketListSuggestion;
import com.worldventures.dreamtrips.api.bucketlist.model.BucketType;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.Suggestion;

import java.util.List;

import io.techery.janet.Janet;

public class SuggestionLoader extends AutoCompleteAdapter.Loader<Suggestion> {

   private Janet apiJanet;

   private BucketItem.BucketType type;

   public SuggestionLoader(BucketItem.BucketType type, Janet apiJanet) {
      this.type = type;
      this.apiJanet = apiJanet;
   }

   @Override
   protected List<Suggestion> request(String query) {
      return Queryable.from(getApiSuggestions(query))
            .map(apiSuggestion -> new Suggestion(apiSuggestion.name()))
            .toList();
   }

   private List<BucketListSuggestion> getApiSuggestions(String query) {
      return apiJanet.createPipe(GetBucketListSuggestionsHttpAction.class)
            .createObservableResult(new GetBucketListSuggestionsHttpAction(getBucketType(), query))
            .toBlocking().first().response();
   }

   private BucketType getBucketType() {
      return BucketType.valueOf(type.toString().toUpperCase());
   }
}
