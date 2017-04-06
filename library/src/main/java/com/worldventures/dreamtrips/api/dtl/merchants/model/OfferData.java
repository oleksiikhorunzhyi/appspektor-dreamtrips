package com.worldventures.dreamtrips.api.dtl.merchants.model;

import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;

@Gson.TypeAdapters
@Value.Immutable
public interface OfferData {

    @SerializedName("title")
    String title();
    @SerializedName("description")
    String description();
    @SerializedName("disclaimer")
    String disclaimer();
    @Nullable
    @SerializedName("start_date")
    Date startDate();
    @Nullable
    @SerializedName("end_date")
    Date endDate();
    @SerializedName("images")
    List<MerchantMedia> images();
    @SerializedName("operation_days")
    List<OperationDay> operationDays();
    @Nullable
    @SerializedName("currencies")
    List<Currency> currencies();
}
