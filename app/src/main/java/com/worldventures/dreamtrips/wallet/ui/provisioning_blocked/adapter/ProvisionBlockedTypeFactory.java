package com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.adapter;

import com.worldventures.dreamtrips.wallet.ui.common.adapter.HolderTypeFactory;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.CustomerSupportContactModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.SupportedDevicesListModel;
import com.worldventures.dreamtrips.wallet.ui.provisioning_blocked.holder.UnsupportedDeviceModel;

public interface ProvisionBlockedTypeFactory extends HolderTypeFactory {

   int type(UnsupportedDeviceModel model);

   int type(CustomerSupportContactModel model);

   int type(SupportedDevicesListModel model);
}
