package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;
import com.worldventures.dreamtrips.api.api_common.model.Identifiable;
import com.worldventures.dreamtrips.api.dtl.locations.model.Coordinates;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface Merchant extends Identifiable<String> {

    @SerializedName("type") MerchantType type();
    @SerializedName("partner_status") PartnerStatus partnerStatus();
    @SerializedName("display_name") String displayName();
    @Nullable @SerializedName("address1")  String address();
    @Nullable @SerializedName("city") String city();
    @Nullable @SerializedName("state") String state();
    @Nullable @SerializedName("country") String country();
    @Nullable @SerializedName("coordinates") Coordinates coordinates();
    @Nullable @SerializedName("description") String description();
    @Nullable @SerializedName("budget") Integer budget();
    @Nullable @SerializedName("distance") Double distance();
    @Nullable @SerializedName("zip") String zip();
    @Nullable @SerializedName("rating") Double rating();
    @Nullable @SerializedName("phone") String phone();
    @Nullable @SerializedName("email") String email();
    @Nullable @SerializedName("website") String website();
    @Nullable @SerializedName("currencies") List<Currency> currencies();
    @Nullable @SerializedName("offers") List<Offer> offers();
    @Nullable @SerializedName("time_zone") String timeZone();
    @Nullable @SerializedName("categories") List<ThinAttribute> categories();
    @Nullable @SerializedName("amenities") List<ThinAttribute> amenities();
    @Nullable @SerializedName("images") List<MerchantMedia> images();
    @Nullable @SerializedName("operation_days") List<OperationDay> operationDays();
    @Nullable @SerializedName("disclaimers") List<Disclaimer> disclaimers();
    @Nullable @SerializedName("reviews") Reviews reviews();
}
