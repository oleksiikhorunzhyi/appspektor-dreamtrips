package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.disclaimer.Disclaimer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Currency;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.operational_hour.OperationDay;

import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
public abstract class Merchant {

   public abstract String id();
   public abstract MerchantType type();
   public abstract PartnerStatus partnerStatus();
   public abstract String displayName();
   public abstract @Nullable String address();
   public abstract @Nullable String city();
   public abstract @Nullable String state();
   public abstract @Nullable String country();
   public abstract @Nullable Coordinates coordinates();
   public abstract @Nullable String description();
   public abstract @Nullable Integer budget();
   public abstract @Nullable Double distance();
   public abstract @Nullable String zip();
   public abstract @Nullable Double rating();
   public abstract @Nullable String phone();
   public abstract @Nullable String email();
   public abstract @Nullable String website();
   public abstract @Nullable List<Currency> currencies();
   public abstract @Nullable List<Offer> offers();
   public abstract @Nullable String timeZone();
   public abstract @Nullable List<ThinAttribute> categories();
   public abstract @Nullable List<ThinAttribute> amenities();
   public abstract @Nullable List<MerchantMedia> images();
   public abstract @Nullable List<OperationDay> operationDays();
   public abstract @Nullable List<Disclaimer> disclaimers();

   @Value.Default
   public int timeOffset(){
      try {
         return Integer.valueOf(timeZone());
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   @Value.Default
   public Currency defaultCurrency() {
      return Queryable.from(currencies()).first(Currency::isDefault);
   }

}
