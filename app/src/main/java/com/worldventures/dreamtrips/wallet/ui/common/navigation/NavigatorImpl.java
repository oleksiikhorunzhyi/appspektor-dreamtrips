package com.worldventures.dreamtrips.wallet.ui.common.navigation;


import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.worldventures.dreamtrips.wallet.ui.start.impl.WalletStartScreenImpl;

import dagger.Lazy;

public class NavigatorImpl implements NavigatorConductor {

   private final Lazy<Router> routerLazy;

   public NavigatorImpl(Lazy<Router> routerLazy) {
      this.routerLazy = routerLazy;
   }

   @Override
   public void goGeneralSettings() {
      //TODO : add settings controller
      routerLazy.get().pushController(RouterTransaction.with(new WalletStartScreenImpl()));
   }

   @Override
   public void goProvisioningBlocked() {
      //TODO : add Provision blocked controller
      routerLazy.get().replaceTopController(RouterTransaction.with(new WalletStartScreenImpl()));
   }

   @Override
   public void goCardList() {
      //TODO : add CardList blocked controller
      routerLazy.get().replaceTopController(RouterTransaction.with(new WalletStartScreenImpl()));
   }

   @Override
   public void goInstallFirmware() {
      //TODO : add InstallFirmware blocked controller
      routerLazy.get().replaceTopController(RouterTransaction.with(new WalletStartScreenImpl()));

   }

   @Override
   public void goNewFirmwareAvailable() {
      //TODO : add NewFirmware blocked controller
      routerLazy.get().replaceTopController(RouterTransaction.with(new WalletStartScreenImpl()));

   }

   @Override
   public void goWizardWelcome() {
      //TODO : add WizardWelcome blocked controller
      routerLazy.get().replaceTopController(RouterTransaction.with(new WalletStartScreenImpl()));
   }

   @Override
   public void goBack() {
      routerLazy.get().handleBack();
   }

   @Override
   public void finish() {
      routerLazy.get().popToRoot();
   }
}
