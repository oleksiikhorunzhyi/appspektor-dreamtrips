package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.oncard.GetOnCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.oncard.SendOnCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.SetPinEnabledCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetSmartCardTimeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SmartCardAvatarCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateNxtSessionCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateProfileModule;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SecureMultipleRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SecureRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.RemoveSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.CustomerSupportFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.dreamtrips.wallet.service.command.settings.help.SmartCardFeedbackCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;

import dagger.Module;

@Module(
      includes = {
            UpdateProfileModule.class
      },
      injects = {
            GetCompatibleDevicesCommand.class,
            RecordListCommand.class,
            SecureRecordCommand.class,
            SecureMultipleRecordsCommand.class,
            RestoreOfflineModeDefaultStateCommand.class,
            UpdateRecordCommand.class,
            DeleteRecordCommand.class,
            SyncSmartCardCommand.class,
            SyncRecordsCommand.class,
            SmartCardAvatarCommand.class,
            SetupUserDataCommand.class,
            DefaultRecordIdCommand.class,
            ActivateSmartCardCommand.class,
            CreateAndConnectToCardCommand.class,
            WizardCheckCommand.class,
            GetDefaultAddressCommand.class,
            AddRecordCommand.class,
            SetupDefaultAddressCommand.class,
            SetDefaultCardOnDeviceCommand.class,
            SetPaymentCardAction.class,
            SetStealthModeCommand.class,
            ActiveSmartCardCommand.class,
            ConnectSmartCardCommand.class,
            SetSmartCardTimeCommand.class,
            FetchCardPropertiesCommand.class,
            FetchFirmwareVersionCommand.class,
            SetLockStateCommand.class,
            FetchTermsAndConditionsCommand.class,
            GetCustomerSupportContactCommand.class,
            SmartCardFeedbackCommand.class,
            CustomerSupportFeedbackCommand.class,
            CreateRecordCommand.class,
            AssociateCardUserCommand.class,
            WizardCompleteCommand.class,
            FetchFirmwareInfoCommand.class,
            SetAutoClearSmartCardDelayCommand.class,
            WalletAnalyticsCommand.class,
            WalletFirmwareAnalyticsCommand.class,
            PaycardAnalyticsCommand.class,
            TokenizationAnalyticsLocationCommand.class,
            SendOnCardAnalyticsCommand.class,
            GetOnCardAnalyticsCommand.class,
            SetDisableDefaultCardDelayCommand.class,
            FetchAndStoreDefaultAddressInfoCommand.class,
            RemoveSmartCardDataCommand.class,
            ResetSmartCardCommand.class,
            FetchBatteryLevelCommand.class,
            FetchAssociatedSmartCardCommand.class,
            RestartSmartCardCommand.class,
            GetSmartCardStatusCommand.class,
            UpdateSmartCardUserCommand.class,
            FactoryResetCommand.class,
            WipeSmartCardDataCommand.class,
            CreateNxtSessionCommand.class,
            LocateCardAnalyticsCommand.class,
            FetchFirmwareUpdateDataCommand.class,
            OfflineModeStatusCommand.class,
            ReAssignCardCommand.class,
            SyncRecordOnNewDeviceCommand.class,
            SwitchOfflineModeCommand.class,
            SetPinEnabledCommand.class,
            GetDocumentsCommand.class
      },
      complete = false, library = true)
public class WalletCommandModule {}

