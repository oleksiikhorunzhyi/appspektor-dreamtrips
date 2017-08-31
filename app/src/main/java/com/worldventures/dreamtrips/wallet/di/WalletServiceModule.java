package com.worldventures.dreamtrips.wallet.di;

import android.content.Context;

import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.FeatureManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.general.SmartCardAnalyticErrorHandler;
import com.worldventures.dreamtrips.wallet.service.FactoryResetInteractor;
import com.worldventures.dreamtrips.wallet.service.FirmwareInteractor;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardErrorServiceWrapper;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardLocationInteractor;
import com.worldventures.dreamtrips.wallet.service.SmartCardSyncManager;
import com.worldventures.dreamtrips.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.dreamtrips.wallet.service.SystemPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.WalletAccessValidator;
import com.worldventures.dreamtrips.wallet.service.WalletAnalyticsServiceWrapper;
import com.worldventures.dreamtrips.wallet.service.WalletBluetoothService;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.WalletSocialInfoProvider;
import com.worldventures.dreamtrips.wallet.service.WizardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareModule;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidBleService;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidNetworkManager;
import com.worldventures.dreamtrips.wallet.service.impl.AndroidPropertiesProvider;
import com.worldventures.dreamtrips.wallet.service.impl.WalletAccessValidatorImpl;
import com.worldventures.dreamtrips.wallet.service.impl.WalletAccessValidatorMock;
import com.worldventures.dreamtrips.wallet.service.impl.WalletBluetoothServiceMock;
import com.worldventures.dreamtrips.wallet.service.impl.WalletSocialInfoProviderImpl;
import com.worldventures.dreamtrips.wallet.service.lostcard.LostCardModule;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.provisioning.ProvisioningModule;
import com.worldventures.dreamtrips.wallet.util.WalletFeatureHelper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@Module(
      includes = {
            FirmwareModule.class,
            LostCardModule.class,
            ProvisioningModule.class,
      },
      complete = false, library = true)
public class WalletServiceModule {

   @Singleton
   @Provides
   WalletBluetoothService walletBluetoothService(@ForApplication Context appContext) {
      if (BuildConfig.WALLET_EMULATOR_MODE) {
         return new WalletBluetoothServiceMock();
      }
      return new AndroidBleService(appContext);
   }

   @Singleton
   @Provides
   WalletAccessValidator walletNetworkService(FeatureManager featureManager) {
      if (BuildConfig.WALLET_EMULATOR_MODE) {
         return new WalletAccessValidatorMock();
      }
      return new WalletAccessValidatorImpl(featureManager);
   }

   @Singleton
   @Provides
   WalletNetworkService walletNetworkService(@ForApplication Context appContext) {
      return new AndroidNetworkManager(appContext);
   }

   @Singleton
   @Provides
   SystemPropertiesProvider systemPropertiesProvider(@ForApplication Context appContext) {
      return new AndroidPropertiesProvider(appContext);
   }

   @Singleton
   @Provides
   WizardInteractor provideWizardInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new WizardInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   SmartCardInteractor provideSmartCardInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new SmartCardInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   WalletSettingsInteractor provideSettingsHelpInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new WalletSettingsInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   RecordInteractor provideRecordInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new RecordInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   FirmwareInteractor firmwareInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new FirmwareInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   NxtInteractor nxtInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new NxtInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   FactoryResetInteractor factoryResetManager(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new FactoryResetInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   SmartCardUserDataInteractor smartCardUserDataInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new SmartCardUserDataInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   SmartCardSyncManager smartCardSyncManager(@Named(JANET_WALLET) Janet janet, SmartCardInteractor smartCardInteractor,
         FirmwareInteractor firmwareInteractor, RecordInteractor recordInteractor, WalletFeatureHelper featureHelper) {
      return new SmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor, featureHelper);
   }

   @Singleton
   @Provides
   SmartCardLocationInteractor locationInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new SmartCardLocationInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   SmartCardAnalyticErrorHandler smartCardErrorAnalyticEventHandler(SmartCardErrorServiceWrapper errorServiceWrapper,
         WalletAnalyticsServiceWrapper analyticsServiceWrapper, AnalyticsInteractor analyticsInteractor) {
      return new SmartCardAnalyticErrorHandler(errorServiceWrapper, analyticsServiceWrapper, analyticsInteractor);
   }

   @Singleton
   @Provides
   WalletSocialInfoProvider walletSocialInfoProvider(SessionHolder<UserSession> sessionHolder) {
      return new WalletSocialInfoProviderImpl(sessionHolder);
   }
}
