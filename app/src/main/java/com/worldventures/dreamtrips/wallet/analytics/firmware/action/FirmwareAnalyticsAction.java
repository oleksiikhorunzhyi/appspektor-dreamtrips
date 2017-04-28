package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.core.utils.tracksystem.BaseAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

public abstract class FirmwareAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("cid") String cid;

   @CallSuper
   public void setFirmwareData(@NonNull FirmwareUpdateData data) {
      cid = data.smartCardId();
   }
}
