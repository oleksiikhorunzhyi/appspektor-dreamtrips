package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDeviceModel;

public interface SupportDeviceItemTypeFactory extends HolderTypeFactory {

   int type(SupportedDeviceModel device);
}
