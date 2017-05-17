package com.worldventures.dreamtrips.wallet.service.provisioning;


public interface PinOptionalStorage {

   boolean shouldAskForPin();

   void saveShouldAskForPin(boolean shouldAskForPin);
}
