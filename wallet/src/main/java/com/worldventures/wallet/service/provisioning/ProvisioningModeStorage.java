package com.worldventures.wallet.service.provisioning;

interface ProvisioningModeStorage {

   void saveState(ProvisioningMode state);

   ProvisioningMode getState();

   void clear();
}
