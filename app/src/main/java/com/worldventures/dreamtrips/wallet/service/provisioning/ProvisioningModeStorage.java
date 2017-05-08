package com.worldventures.dreamtrips.wallet.service.provisioning;

interface ProvisioningModeStorage {

   void saveState(ProvisioningMode state);

   ProvisioningMode getState();

   void clear();
}
