package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;
import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
public interface ThinMerchantsActionParams {

    @SerializedName("coordinates")
    String coordinates();

    @SerializedName("radius")
    @Nullable Double radius();

    @SerializedName("search")
    @Nullable String search();

    @SerializedName("partner_status")
    @Nullable List<String> partnerStatuses();

    @SerializedName("sort_field")
    @Nullable String sortField();

    @SerializedName("sort_dir")
    @Nullable String sortDirection();

    @SerializedName("budget_min")
    @Nullable Integer budgetMin();

    @SerializedName("budget_max")
    @Nullable Integer budgetMax();

    @SerializedName("filter_attrib")
    @Nullable List<String> filterAttributes();

    @SerializedName("offset")
    @Nullable Integer offset();

    @SerializedName("limit")
    @Nullable Integer limit();
}
