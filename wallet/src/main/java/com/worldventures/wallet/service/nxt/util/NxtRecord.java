package com.worldventures.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.nxt.model.MultiErrorResponse;

import java.util.List;

public interface NxtRecord {

   Record getTokenizedRecord();

   Record getDetokenizedRecord();

   @NonNull
   List<MultiErrorResponse> getResponseErrors();

}