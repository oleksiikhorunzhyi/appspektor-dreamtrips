package com.worldventures.wallet.ui.settings.security.lostcard.custom;


import com.bluelinelabs.conductor.Router;

public interface LostCardControllerFlipper {

   void init(Router childRouter);

   void init(Router childRouter, ControllerFlipListener flipListener);

   void flip(boolean isMapEnabled);

   void destroy();

   boolean isUpdated();
}
