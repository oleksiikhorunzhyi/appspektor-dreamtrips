package com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.custom;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.wallet.ui.settings.security.lostcard.impl.MapScreenImpl;

public class LostCardControllerFlipperImpl implements LostCardControllerFlipper, ControllerChangeHandler.ControllerChangeListener {
   private Router childRouter;
   private ControllerFlipListener controllerFlipListener;
   private boolean enabled;
   private boolean isUpdatedContainer;

   @Override
   public void init(Router childRouter) {
      this.childRouter = childRouter;
      setRoot(new DisabledTrackingController());
   }

   @Override
   public void init(Router childRouter, ControllerFlipListener flipListener) {
      this.controllerFlipListener = flipListener;
      init(childRouter);
   }

   @Override
   public void flip(boolean isMapEnabled) {
      if (enabled != isMapEnabled) {
         enabled = isMapEnabled;
         final Controller targetController = isMapEnabled
               ? new MapScreenImpl()
               : new DisabledTrackingController();
         setRoot(targetController);
      }
      isUpdatedContainer = true;
   }

   @Override
   public void destroy() {
      childRouter.removeChangeListener(this);
      controllerFlipListener = null;
      childRouter = null;
   }

   @Override
   public boolean isUpdated() {
      return isUpdatedContainer;
   }

   private void setRoot(Controller controller) {
      childRouter.setRoot(RouterTransaction.with(controller)
            .pushChangeHandler(new FadeChangeHandler())
            .popChangeHandler(new FadeChangeHandler()));
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
