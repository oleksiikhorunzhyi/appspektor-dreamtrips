package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public interface Merchant extends Serializable {

   String id();
   MerchantType type();
   PartnerStatus partnerStatus();
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
   @Nullable List<Currency> currencies();
   @Nullable List<Offer> offers();
   @Nullable String timeZone();
   @Nullable List<ThinAttribute> categories();
   @Nullable List<ThinAttribute> amenities();
   @Nullable List<MerchantMedia> images();
   @Nullable List<OperationDay> operationDays();
   @Nullable List<Disclaimer> disclaimers();

}
