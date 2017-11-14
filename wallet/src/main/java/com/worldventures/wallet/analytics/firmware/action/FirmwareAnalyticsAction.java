package com.worldventures.wallet.analytics.firmware.action;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.worldventures.core.service.analytics.Attribute;
import com.worldventures.core.service.analytics.BaseAnalyticsAction;
import com.worldventures.wallet.domain.entity.FirmwareUpdateData;

public abstract class FirmwareAnalyticsAction extends BaseAnalyticsAction {

   @Attribute("cid") String cid;

   @CallSuper
   public void setFirmwareData(@NonNull FirmwareUpdateData data) {
      cid = data.smartCardId();
   }
}
