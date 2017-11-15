package com.worldventures.wallet.di;

import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.wallet.analytics.firmware.WalletFirmwareAnalyticsCommand;
import com.worldventures.wallet.analytics.general.action.SmartCardCommunicationErrorAction;
import com.worldventures.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.wallet.analytics.oncard.GetOnCardAnalyticsCommand;
import com.worldventures.wallet.analytics.oncard.SendOnCardAnalyticsCommand;
import com.worldventures.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand;
import com.worldventures.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.wallet.service.command.FactoryResetCommand;
import com.worldventures.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.wallet.service.command.FetchFirmwareUpdateDataCommand;
import com.worldventures.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.wallet.service.command.RecordListCommand;
import com.worldventures.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.wallet.service.command.SetLockStateCommand;
import com.worldventures.wallet.service.command.SetPaymentCardAction;
import com.worldventures.wallet.service.command.SetPinEnabledCommand;
import com.worldventures.wallet.service.command.SetSmartCardTimeCommand;
import com.worldventures.wallet.service.command.SetStealthModeCommand;
import com.worldventures.wallet.service.command.SetupUserDataCommand;
import com.worldventures.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.wallet.service.command.http.CreateNxtSessionCommand;
import com.worldventures.wallet.service.command.http.CreateRecordCommand;
import com.worldventures.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.wallet.service.command.offline_mode.OfflineModeStatusCommand;
import com.worldventures.wallet.service.command.offline_mode.RestoreOfflineModeDefaultStateCommand;
import com.worldventures.wallet.service.command.offline_mode.SwitchOfflineModeCommand;
import com.worldventures.wallet.service.command.profile.UpdateProfileModule;
import com.worldventures.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.wallet.service.command.record.AddRecordCommand;
import com.worldventures.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.wallet.service.command.record.SecureMultipleRecordsCommand;
import com.worldventures.wallet.service.command.record.SecureRecordCommand;
import com.worldventures.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.wallet.service.command.record.SyncRecordsCommand;
import com.worldventures.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.wallet.service.command.reset.RemoveSmartCardDataCommand;
import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.wallet.service.command.settings.general.display.GetDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.RestoreDefaultDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.SaveDisplayTypeCommand;
import com.worldventures.wallet.service.command.settings.general.display.ValidateDisplayTypeDataCommand;
import com.worldventures.wallet.service.command.settings.help.CustomerSupportFeedbackCommand;
import com.worldventures.wallet.service.command.settings.help.GetCustomerSupportContactCommand;
import com.worldventures.wallet.service.command.settings.help.PaymentFeedbackCommand;
import com.worldventures.wallet.service.command.settings.help.SmartCardFeedbackCommand;
import com.worldventures.wallet.service.command.uploadery.SmartCardUploaderyCommand;
import com.worldventures.wallet.service.command.wizard.AddDummyRecordCommand;
import com.worldventures.wallet.service.command.wizard.FetchAssociatedSmartCardCommand;
import com.worldventures.wallet.service.command.wizard.ReAssignCardCommand;
import com.worldventures.wallet.service.command.wizard.WizardCompleteCommand;

import dagger.Module;

@Module(
      includes = {
            UpdateProfileModule.class
      },
      injects = {
            SmartCardUploaderyCommand.class,
            GetCompatibleDevicesCommand.class,
            RecordListCommand.class,
            SecureRecordCommand.class,
            SecureMultipleRecordsCommand.class,
            RestoreOfflineModeDefaultStateCommand.class,
            UpdateRecordCommand.class,
            DeleteRecordCommand.class,
            SyncSmartCardCommand.class,
            SyncRecordsCommand.class,
            SetupUserDataCommand.class,
            DefaultRecordIdCommand.class,
            ActivateSmartCardCommand.class,
            CreateAndConnectToCardCommand.class,
            AddRecordCommand.class,
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
            SaveDisplayTypeCommand.class,
            GetDisplayTypeCommand.class,
            RestoreDefaultDisplayTypeCommand.class,
            ValidateDisplayTypeDataCommand.class,
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
            SmartCardCommunicationErrorAction.class,
            GetOnCardAnalyticsCommand.class,
            SetDisableDefaultCardDelayCommand.class,
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
            GetDocumentsCommand.class,
            PaymentFeedbackCommand.class,
            AddDummyRecordCommand.class,
      },
      complete = false, library = true)
public class WalletCommandModule {}

