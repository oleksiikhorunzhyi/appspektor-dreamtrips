package com.worldventures.wallet.ui.common.base;


import android.animation.LayoutTransition;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.worldventures.wallet.R;
import com.worldventures.wallet.domain.entity.ConnectionStatus;

import static android.widget.LinearLayout.VERTICAL;

public abstract class WalletScreenDelegate {
   private final View connectionSmartCardHeader;
   private final View connectionHttpHeader;
   private final boolean visibleConnectionSmartCardLabel;
   private final boolean visibleHttpConnectionLabel;

   private WalletScreenDelegate(View container, boolean visibleConnectionSmartCardLabel, boolean visibleHttpConnectionLabel) {
      this.connectionSmartCardHeader = LayoutInflater.from(container.getContext())
            .inflate(R.layout.wallet_plank_smartcard_connection, (ViewGroup) container, false);
      this.connectionHttpHeader = LayoutInflater.from(container.getContext())
            .inflate(R.layout.wallet_plank_http_connection, (ViewGroup) container, false);
      this.visibleConnectionSmartCardLabel = visibleConnectionSmartCardLabel;
      this.visibleHttpConnectionLabel = visibleHttpConnectionLabel;
   }

   public static WalletScreenDelegate create(View container, boolean visibleConnectionSmartCardLabel, boolean visibleHttpConnectionLabel) {
      if (container instanceof LinearLayout) {
         return new WalletLinearScreenDelegate(container, visibleConnectionSmartCardLabel, visibleHttpConnectionLabel);
      } else if (container instanceof ConstraintLayout) {
         return new WalletConstraintScreenDelegate(container, visibleConnectionSmartCardLabel, visibleHttpConnectionLabel);
      } else {
         throw new IllegalArgumentException("WalletScreenDelegate doesn't support such type of container");
      }
   }

   public void showConnectionStatus(ConnectionStatus connectionStatus) {
      if (!visibleConnectionSmartCardLabel) return;

      viewLabelController(connectionSmartCardHeader, connectionStatus.isConnected());
   }

   public abstract void viewLabelController(View view, boolean connected);

   public void showHttpConnectionStatus(boolean connected) {
      if (!visibleHttpConnectionLabel || visibleConnectionSmartCardLabel) return;

      viewLabelController(connectionHttpHeader, connected);
   }

   public View getConnectionSmartCardHeader() {
      return connectionSmartCardHeader;
   }

   public View getConnectionHttpHeader() {
      return connectionHttpHeader;
   }

   public boolean isVisibleConnectionSmartCardLabel() {
      return visibleConnectionSmartCardLabel;
   }

   public boolean isVisibleHttpConnectionLabel() {
      return visibleHttpConnectionLabel;
   }


   private static class WalletLinearScreenDelegate extends WalletScreenDelegate {
      private final LinearLayout containerLayout;

      private WalletLinearScreenDelegate(View container, boolean visibleConnectionSmartCardLabel,
            boolean visibleHttpConnectionLabel) {
         super(container, visibleConnectionSmartCardLabel, visibleHttpConnectionLabel);
         this.containerLayout = (LinearLayout) container;
         initContainer();
      }

      private void initContainer() {
         containerLayout.setOrientation(VERTICAL);
         containerLayout.setLayoutTransition(new LayoutTransition());
      }

      private int findCorrectIndex() {
         if (containerLayout.getChildCount() > 0 && hasFirstViewIsToolbar(containerLayout)) {
            return 1;
         } else {
            return 0;
         }
      }

      private boolean hasFirstViewIsToolbar(ViewGroup viewGroup) {
         if (viewGroup.getChildAt(0) instanceof Toolbar) {
            return true;
         } else {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
               View child = viewGroup.getChildAt(i);
               if (child instanceof ViewGroup) {
                  return hasFirstViewIsToolbar((ViewGroup) child);
               } else {
                  return false;
               }
            }
            return false;
         }
      }

      @Override
      public void viewLabelController(View view, boolean connected) {
         if (connected) {
            containerLayout.removeView(view);
         } else {
            if (containerLayout.indexOfChild(view) < 0) {
               containerLayout.addView(view, findCorrectIndex());
            }
         }
      }
   }

   private static class WalletConstraintScreenDelegate extends WalletScreenDelegate {

      private WalletConstraintScreenDelegate(View container, boolean visibleConnectionSmartCardLabel,
            boolean visibleHttpConnectionLabel) {
         super(container, visibleConnectionSmartCardLabel, visibleHttpConnectionLabel);
      }

      @Override
      public void viewLabelController(View view, boolean connected) {
         // TODO: 5/22/17 Come up with an idea on how to show these labels more nicely
      }
   }

}
