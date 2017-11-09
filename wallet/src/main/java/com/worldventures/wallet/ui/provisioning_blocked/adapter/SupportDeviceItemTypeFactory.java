package com.worldventures.wallet.ui.provisioning_blocked.adapter;

import com.worldventures.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.wallet.ui.provisioning_blocked.holder.SupportedDeviceModel;

public interface SupportDeviceItemTypeFactory extends HolderTypeFactory {

   int type(SupportedDeviceModel device);
}
