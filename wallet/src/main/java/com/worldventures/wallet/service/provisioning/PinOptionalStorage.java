package com.worldventures.wallet.service.provisioning;


public interface PinOptionalStorage {

   boolean shouldAskForPin();

   void saveShouldAskForPin(boolean shouldAskForPin);
}
