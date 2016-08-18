package com.worldventures.dreamtrips.wallet.ui.wizard.success;

import android.content.Context;
import android.os.Parcelable;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import flow.Flow;

public class WalletSuccessPresenter extends WalletPresenter<WalletSuccessPresenter.Screen, Parcelable> {

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
      Flow.get(getContext()).goBack();
   }

   public void goToNext() {
      Flow.get(getContext()).set(screenContent.nextPath());
   }

   public interface Screen extends WalletScreen {

      void buttonText(String buttonText);

      void text(String text);

      void title(String title);

   }
}
