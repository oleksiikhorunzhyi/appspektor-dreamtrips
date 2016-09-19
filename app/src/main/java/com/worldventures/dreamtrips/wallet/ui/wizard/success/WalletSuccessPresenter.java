package com.worldventures.dreamtrips.wallet.ui.wizard.success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;

import javax.inject.Inject;

public class WalletSuccessPresenter extends WalletPresenter<WalletSuccessPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;

   private final ScreenContent screenContent;

   public WalletSuccessPresenter(Context context, Injector injector, ScreenContent screenContent) {
      super(context, injector);
      this.screenContent = screenContent;
   }

   @Override
   public void attachView(Screen view) {
      super.attachView(view);
      view.title(screenContent.title());
      view.text(screenContent.text());
      view.buttonText(screenContent.buttonText());
   }

   public void goToBack() {
      navigator.goBack();
   }

   public void goToNext() {
      navigator.go(screenContent.nextPath());
   }

   public interface Screen extends WalletScreen {

      void buttonText(String buttonText);

      void text(String text);

      void title(String title);

   }
}
