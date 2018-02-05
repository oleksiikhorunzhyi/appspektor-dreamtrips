package com.worldventures.wallet.di;

import android.content.Context;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.model.session.FeatureManager;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.wallet.analytics.general.SmartCardAnalyticErrorHandler;
import com.worldventures.wallet.service.FactoryResetInteractor;
import com.worldventures.wallet.service.FirmwareInteractor;
import com.worldventures.wallet.service.RecordInteractor;
import com.worldventures.wallet.service.SmartCardErrorServiceWrapper;
import com.worldventures.wallet.service.SmartCardInteractor;
import com.worldventures.wallet.service.SmartCardLocationInteractor;
import com.worldventures.wallet.service.SmartCardSyncManager;
import com.worldventures.wallet.service.SmartCardUserDataInteractor;
import com.worldventures.wallet.service.SystemPropertiesProvider;
import com.worldventures.wallet.service.WalletAccessValidator;
import com.worldventures.wallet.service.WalletAnalyticsInteractor;
import com.worldventures.wallet.service.WalletAnalyticsServiceWrapper;
import com.worldventures.wallet.service.WalletBluetoothService;
import com.worldventures.wallet.service.WalletNetworkService;
import com.worldventures.wallet.service.WalletSchedulerProvider;
import com.worldventures.wallet.service.WalletSchedulerProviderImpl;
import com.worldventures.wallet.service.WizardInteractor;
import com.worldventures.wallet.service.command.settings.WalletSettingsInteractor;
import com.worldventures.wallet.service.firmware.FirmwareModule;
import com.worldventures.wallet.service.impl.AndroidBleService;
import com.worldventures.wallet.service.impl.AndroidNetworkManager;
import com.worldventures.wallet.service.impl.AndroidPropertiesProvider;
import com.worldventures.wallet.service.impl.WalletAccessValidatorImpl;
import com.worldventures.wallet.service.impl.WalletAccessValidatorMock;
import com.worldventures.wallet.service.impl.WalletBluetoothServiceMock;
import com.worldventures.wallet.service.lostcard.LostCardModule;
import com.worldventures.wallet.service.nxt.NxtInteractor;
import com.worldventures.wallet.service.provisioning.ProvisioningModule;
import com.worldventures.wallet.util.WalletBuildConfigHelper;
import com.worldventures.wallet.util.WalletFeatureHelper;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

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
   WalletBluetoothService walletBluetoothService(Context appContext, WalletBuildConfigHelper configHelper) {
      if (configHelper.isEmulatorModeEnabled()) {
         return new WalletBluetoothServiceMock();
      }
      return new AndroidBleService(appContext);
   }

   @Singleton
   @Provides
   WalletAccessValidator walletNetworkService(FeatureManager featureManager, WalletBuildConfigHelper configHelper) {
      if (configHelper.isEmulatorModeEnabled()) {
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
   WalletSchedulerProvider provideWalletSchedulerProvider() {
      return new WalletSchedulerProviderImpl();
   }

   @Singleton
   @Provides
   SmartCardInteractor provideSmartCardInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator,
         WalletSchedulerProvider schedulerProvider) {
      return new SmartCardInteractor(sessionActionPipeCreator, schedulerProvider);
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
         FirmwareInteractor firmwareInteractor, RecordInteractor recordInteractor,
         FactoryResetInteractor factoryResetInteractor, AuthInteractor authInteractor, WalletFeatureHelper featureHelper) {
      return new SmartCardSyncManager(janet, smartCardInteractor, firmwareInteractor, recordInteractor,
            factoryResetInteractor, authInteractor, featureHelper);
   }

   @Singleton
   @Provides
   SmartCardLocationInteractor locationInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new SmartCardLocationInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   WalletAnalyticsInteractor analyticsInteractor(@Named(JANET_WALLET) SessionActionPipeCreator sessionActionPipeCreator) {
      return new WalletAnalyticsInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   SmartCardAnalyticErrorHandler smartCardErrorAnalyticEventHandler(SmartCardErrorServiceWrapper errorServiceWrapper,
         WalletAnalyticsServiceWrapper analyticsServiceWrapper, WalletAnalyticsInteractor analyticsInteractor) {
      return new SmartCardAnalyticErrorHandler(errorServiceWrapper, analyticsServiceWrapper, analyticsInteractor);
   }
}
