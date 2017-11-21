package com.worldventures.wallet.analytics.firmware.action

import com.worldventures.janet.analytics.AnalyticsEvent
import com.worldventures.core.service.analytics.AdobeTracker

@AnalyticsEvent(action = "wallet:settings:general:SmartCard Update:Ready Checklist:Install", trackers = arrayOf(AdobeTracker.TRACKER_KEY))
class UpdateInstallAction : FirmwareAnalyticsAction()
