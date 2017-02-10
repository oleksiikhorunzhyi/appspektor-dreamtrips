package com.worldventures.dreamtrips.modules.dtl.model.merchant;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.worldventures.dreamtrips.api.dtl.merchants.model.MerchantType;
import com.worldventures.dreamtrips.api.dtl.merchants.model.PartnerStatus;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributes;
import com.worldventures.dreamtrips.modules.dtl.helper.inflater.MerchantAttributesFactory;
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
public abstract class Merchant implements Serializable {

   public abstract String id();
   public abstract MerchantType type();
   public abstract PartnerStatus partnerStatus();
   public abstract String displayName();
   @Nullable public abstract String address();
   @Nullable public abstract String city();
   @Nullable public abstract String state();
   @Nullable public abstract String country();
   @Nullable public abstract Coordinates coordinates();
   @Nullable public abstract String description();
   @Nullable public abstract Integer budget();
   @Nullable public abstract Double distance();
   @Nullable public abstract String zip();
   @Nullable public abstract Double rating();
   @Nullable public abstract String phone();
   @Nullable public abstract String email();
   @Nullable public abstract String website();
   @Nullable public abstract List<Currency> currencies();
   @Nullable public abstract List<Offer> offers();
   @Nullable public abstract String timeZone();
   @Nullable public abstract List<ThinAttribute> categories();
   @Nullable public abstract List<ThinAttribute> amenities();
   @Nullable public abstract List<MerchantMedia> images();
   @Nullable public abstract List<OperationDay> operationDays();
   @Nullable public abstract List<Disclaimer> disclaimers();

   @Value.Derived public MerchantAttributes asMerchantAttributes() {
      return MerchantAttributesFactory.create(this);
   }
}
