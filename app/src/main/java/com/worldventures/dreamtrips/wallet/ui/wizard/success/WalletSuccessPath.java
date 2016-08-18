package com.worldventures.dreamtrips.wallet.ui.wizard.success;

import android.support.annotation.NonNull;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_wallet_wizard_success)
public class WalletSuccessPath extends StyledPath implements ScreenContent {

   private final String title;
   private final String buttonText;
   private final String text;
   private final StyledPath nextPath;

   public WalletSuccessPath(String title, String buttonText, String text, @NonNull StyledPath nextPath) {
      this.title = title;
      this.buttonText = buttonText;
      this.text = text;
      this.nextPath = nextPath;
   }

   @Override
   public MasterDetailPath getMaster() {
      return this;
   }

   @Override
   public String title() {
      return title;
   }

   @Override
   public String buttonText() {
      return buttonText;
   }

   @Override
   public String text() {
      return text;
   }

   @NonNull
   @Override
   public StyledPath nextPath() {
      return nextPath;
   }
}
