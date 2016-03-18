package com.worldventures.dreamtrips.core.api;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({UploadPurpose.TRIP_IMAGE, UploadPurpose.BUCKET_IMAGE, UploadPurpose.DTL_RECEIPT})
public @interface UploadPurpose {

    String TRIP_IMAGE = "TRIP_IMAGE";
    String BUCKET_IMAGE = "BUCKET_IMAGE";
    String DTL_RECEIPT = "DTL_RECEIPT";
}