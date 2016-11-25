package com.worldventures.dreamtrips.modules.dtl_flow.animation;

import android.util.Pair;

import com.worldventures.dreamtrips.core.flow.animation.BaseAnimatorRegistrar;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;
import com.worldventures.dreamtrips.wallet.ui.dashboard.list.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPath;

public class DtlAnimatorRegistrar extends BaseAnimatorRegistrar {

   public DtlAnimatorRegistrar() {
      super();
      animators.put(new Pair<>(DtlMerchantDetailsPath.class, DtlFullscreenImagePath.class), new FadeAnimatorFactory());
      animators.put(new Pair<>(DtlFullscreenImagePath.class, DtlMerchantDetailsPath.class), new FadeAnimatorFactory());

      //temporary workaround to do custom anim for Wallet stream needs
      animators.put(new Pair<>(WizardSplashPath.class, WizardTermsPath.class), new FadeAnimatorFactory());
      animators.put(new Pair<>(WizardTermsPath.class, WizardSplashPath.class), new FadeAnimatorFactory());
      animators.put(new Pair<>(WalletStartPath.class, CardListPath.class), null);
   }
}
