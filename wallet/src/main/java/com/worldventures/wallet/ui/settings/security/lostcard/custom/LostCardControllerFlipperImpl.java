package com.worldventures.wallet.ui.settings.security.lostcard.custom;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.worldventures.wallet.ui.settings.security.lostcard.impl.MapScreenImpl;

public class LostCardControllerFlipperImpl implements LostCardControllerFlipper, ControllerChangeHandler.ControllerChangeListener {
   private static final String MAP_ENABLED_KEY = "LostCardControllerFlipperImpl#MAP_ENABLED_KEY";

   private Router childRouter;
   private ControllerFlipListener controllerFlipListener;
   private boolean isMapEnabled;

   @Override
   public void init(Router childRouter, ControllerFlipListener flipListener) {
      this.childRouter = childRouter;
      this.controllerFlipListener = flipListener;
      updateController(false);
   }

   @Override
   public void flip(boolean isMapEnabled) {
      if (this.isMapEnabled != isMapEnabled) {
         this.isMapEnabled = isMapEnabled;
         updateController(true);
      }
   }

   @Override
   public void onSaveViewState(@NonNull Bundle outState) {
      outState.putBoolean(MAP_ENABLED_KEY, isMapEnabled);
   }

   @Override
   public void onRestoreViewState(@NonNull Bundle savedViewState) {
      isMapEnabled = savedViewState.getBoolean(MAP_ENABLED_KEY);
      updateController(false);
   }

   @Override
   public void destroy() {
      childRouter.removeChangeListener(this);
      controllerFlipListener = null;
      childRouter = null;
   }

   private void updateController(boolean animate) {
      Controller controller = isMapEnabled
            ? new MapScreenImpl()
            : new DisabledTrackingController();
      RouterTransaction routerTransaction = RouterTransaction.with(controller);
      if (animate) {
         routerTransaction = routerTransaction.pushChangeHandler(new FadeChangeHandler())
               .popChangeHandler(new FadeChangeHandler());
      }
      childRouter.setRoot(routerTransaction);
   }

   @Override
   public void onChangeStarted(@Nullable Controller controller, @Nullable Controller controller1, boolean b,
         @NonNull ViewGroup viewGroup, @NonNull ControllerChangeHandler controllerChangeHandler) {
      if (controllerFlipListener != null) {
         controllerFlipListener.onFlipStarted();
      }
   }

   @Override
   public void onChangeCompleted(@Nullable Controller controller, @Nullable Controller controller1, boolean b,
         @NonNull ViewGroup viewGroup, @NonNull ControllerChangeHandler controllerChangeHandler) {
      if (controllerFlipListener != null) {
         controllerFlipListener.onFlipEnded();
      }

   }
}
