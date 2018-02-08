package com.worldventures.wallet.ui.settings.security.lostcard.custom;


import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bluelinelabs.conductor.Router;

public interface LostCardControllerFlipper {

   void init(Router childRouter, ControllerFlipListener flipListener);

   void flip(boolean isMapEnabled);

   void onSaveState(@NonNull Bundle outState);

   void onRestoreState(@NonNull Bundle savedViewState);

   void destroy();
}
