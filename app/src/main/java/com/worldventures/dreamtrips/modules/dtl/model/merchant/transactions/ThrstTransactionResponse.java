package com.worldventures.dreamtrips.modules.dtl.model.merchant.transactions;

import android.support.annotation.Nullable;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.google.gson.annotations.SerializedName;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.io.Serializable;
import java.util.List;

@DefaultSerializer(CompatibleFieldSerializer.class)
@Value.Immutable
@Gson.TypeAdapters
public interface ThrstTransactionResponse extends Serializable {
   @SerializedName("Results") @Nullable List<DetailTransactionThrst> results();
   @SerializedName("TotalRecords") @Nullable Integer totalRecords();
   @SerializedName("RowsPerPage") @Nullable Integer rowsPerPage();
   @SerializedName("PageNumber") @Nullable Integer pageNumber();
   @SerializedName("TotalPages") @Nullable Integer totalPages();
   @SerializedName("IsFirstPage") @Nullable Boolean isFirstPage();
   @SerializedName("IsLastPage") @Nullable Boolean isLastPage();
   @SerializedName("HasPreviousPage") @Nullable Boolean hasPreviousPage();
   @SerializedName("HasNextPage") @Nullable Boolean hasNextPage();
}
