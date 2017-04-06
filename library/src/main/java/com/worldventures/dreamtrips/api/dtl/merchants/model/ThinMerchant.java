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
public interface ThinMerchant extends Identifiable<String> {

    @SerializedName("type") MerchantType type();
    @SerializedName("partner_status") PartnerStatus partnerStatus();
    @SerializedName("display_name") String displayName();
    @Nullable @SerializedName("coordinates") Coordinates coordinates();
    @Nullable @SerializedName("city") String city();
    @Nullable @SerializedName("state") String state();
    @Nullable @SerializedName("country") String country();
    @Nullable @SerializedName("budget") Integer budget();
    @Nullable @SerializedName("rating") Double rating();
    @Nullable @SerializedName("distance") Double distance();
    @Nullable @SerializedName("offers") List<Offer> offers();
    @Nullable @SerializedName("time_zone") String timeZone();
    @Nullable @SerializedName("categories") List<ThinAttribute> categories();
    @Nullable @SerializedName("images") List<MerchantMedia> images();
    @Nullable @SerializedName("operation_days") List<OperationDay> operationDays();
    @Nullable @SerializedName("reviewSummary") ReviewSummary reviewSummary();
}
