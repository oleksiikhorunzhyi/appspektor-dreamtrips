package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.api.api_common.AuthorizedHttpAction;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchant;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsActionParams;
import com.worldventures.dreamtrips.api.dtl.merchants.model.ThinMerchantsHolder;
import java.util.List;
import io.techery.janet.http.annotations.HttpAction;
import io.techery.janet.http.annotations.Query;
import io.techery.janet.http.annotations.Response;


@HttpAction(value = "api/dtl/v2/thin_merchants")
public class CategoryMerchantsHttpAction extends AuthorizedHttpAction {

   @Query("ll") final String coordinates;
   @Query("radius") final Double radius;
   @Query("search") final String search;
   @Query("partner_status") final List<String> partnerStatuses;
   @Query("sort_field") final String sortField;
   @Query("sort_dir") final String sortDirection;
   @Query("budget_min") final Integer budgetMin;
   @Query("budget_max") final Integer budgetMax;
   @Query("filter_attrib") final List<String> filterAttributes;
   @Query("offset") final Integer offset;
   @Query("limit") final Integer limit;
   @Query("merchant_types") final List<String> merchantTypes;

   @Response ThinMerchantsHolder merchantsHolder;

   public CategoryMerchantsHttpAction(ThinMerchantsActionParams params, List<String> keyCategories) {
      this.coordinates = params.coordinates();
      this.radius = params.radius();
      this.search = params.search();
      this.partnerStatuses = params.partnerStatuses();
      this.sortField = params.sortField();
      this.sortDirection = params.sortDirection();
      this.budgetMin = params.budgetMin();
      this.budgetMax = params.budgetMax();
      this.offset = params.offset();
      this.limit = params.limit();
      this.filterAttributes = params.filterAttributes();
      this.merchantTypes = keyCategories;
   }

   public List<ThinMerchant> merchants() {
      return merchantsHolder.merchants();
   }
}
