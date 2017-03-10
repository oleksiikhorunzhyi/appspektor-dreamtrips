package com.worldventures.dreamtrips.wallet.service.nxt.util;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiErrorResponse;

import java.util.List;

public interface NxtRecord {

   Record getTokenizedRecord();

   Record getDetokenizedRecord();

   @NonNull
   List<MultiErrorResponse> getResponseErrors();

}