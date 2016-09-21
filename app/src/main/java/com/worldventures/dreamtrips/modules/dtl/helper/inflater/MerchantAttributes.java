package com.worldventures.dreamtrips.modules.dtl.helper.inflater;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.Coordinates;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.MerchantMedia;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
public interface MerchantAttributes {

   String displayName();
   @Nullable String address();
   @Nullable String city();
   @Nullable String state();
   @Nullable String country();
   @Nullable Coordinates coordinates();
   @Nullable String description();
   @Nullable Integer budget();
   @Nullable Double distance();
   @Nullable String zip();
   @Nullable Double rating();
   @Nullable String phone();
   @Nullable String email();
   @Nullable String website();
   @Nullable List<Offer> offers();
   @Nullable String timeZone();
   @Nullable List<ThinAttribute> categories();
   @Nullable List<ThinAttribute> amenities();
   @Nullable List<MerchantMedia> images();
   @Nullable List<OperationDay> operationDays();
   @Nullable List<Disclaimer> disclaimers();

}
