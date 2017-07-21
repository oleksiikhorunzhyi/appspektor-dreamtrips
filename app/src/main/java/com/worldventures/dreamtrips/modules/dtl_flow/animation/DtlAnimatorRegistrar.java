package com.worldventures.dreamtrips.modules.dtl_flow.animation;

import android.util.Pair;

import com.worldventures.dreamtrips.core.flow.animation.BaseAnimatorRegistrar;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.details.DtlMerchantDetailsPath;
import com.worldventures.dreamtrips.modules.dtl_flow.parts.fullscreen_image.DtlFullscreenImagePath;
import com.worldventures.dreamtrips.wallet.ui.dashboard.CardListPath;
import com.worldventures.dreamtrips.wallet.ui.records.detail.CardDetailsPath;
import com.worldventures.dreamtrips.wallet.ui.start.WalletStartPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.manual.WizardManualInputPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScanBarcodePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScannerEnterAnimFactory;
import com.worldventures.dreamtrips.wallet.ui.wizard.input.scanner.WizardScannerExitAnimFactory;
import com.worldventures.dreamtrips.wallet.ui.wizard.pairkey.PairKeyPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.profile.WizardEditProfilePath;
import com.worldventures.dreamtrips.wallet.ui.wizard.splash.WizardSplashPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals.WizardTermsPath;
import com.worldventures.dreamtrips.wallet.ui.wizard.unassign.ExistingDeviceDetectPath;

public class DtlAnimatorRegistrar extends BaseAnimatorRegistrar {

   public DtlAnimatorRegistrar() {
      super();
      animators.put(new Pair<>(DtlMerchantDetailsPath.class, DtlFullscreenImagePath.class), new FadeAnimatorFactory());
      animators.put(new Pair<>(DtlFullscreenImagePath.class, DtlMerchantDetailsPath.class), new FadeAnimatorFactory());

      //temporary workaround to do custom anim for Wallet stream needs
      animators.put(new Pair<>(WizardSplashPath.class, WizardTermsPath.class), new FadeAnimatorFactory());
      animators.put(new Pair<>(WizardTermsPath.class, WizardSplashPath.class), new FadeAnimatorFactory());
      animators.put(new Pair<>(WalletStartPath.class, CardListPath.class), null);
      animators.put(new Pair<>(CardListPath.class, CardDetailsPath.class), null);

      animators.put(new Pair<>(WizardSplashPath.class, WizardScanBarcodePath.class), new WizardScannerEnterAnimFactory());
      animators.put(new Pair<>(WizardScanBarcodePath.class, WizardSplashPath.class), new WizardScannerEnterAnimFactory());

      animators.put(new Pair<>(WizardScanBarcodePath.class, WizardManualInputPath.class), new WizardScannerExitAnimFactory());
      animators.put(new Pair<>(WizardManualInputPath.class, WizardScanBarcodePath.class), new WizardScannerExitAnimFactory());
      animators.put(new Pair<>(WizardScanBarcodePath.class, ExistingDeviceDetectPath.class), new WizardScannerExitAnimFactory());
      animators.put(new Pair<>(ExistingDeviceDetectPath.class, WizardScanBarcodePath.class), new WizardScannerExitAnimFactory());
      animators.put(new Pair<>(WizardScanBarcodePath.class, PairKeyPath.class), new WizardScannerExitAnimFactory());
      animators.put(new Pair<>(PairKeyPath.class, WizardScanBarcodePath.class), new WizardScannerExitAnimFactory());
      animators.put(new Pair<>(WizardEditProfilePath.class, WizardScanBarcodePath.class), new WizardScannerExitAnimFactory());
   }
}
