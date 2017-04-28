package com.worldventures.dreamtrips.wallet.analytics.firmware.action;

import com.worldventures.dreamtrips.core.utils.tracksystem.AdobeTracker;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.Attribute;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsAction;
import com.worldventures.dreamtrips.wallet.domain.entity.FirmwareUpdateData;

@AnalyticsEvent(action = "wallet:SmartCard Update:Step 4:Installing Update",
                trackers = AdobeTracker.TRACKER_KEY)
public class InstallingUpdateAction extends FirmwareAnalyticsAction {

   @Attribute("scupdatestep4") final String updateStep4 = "1";
}
