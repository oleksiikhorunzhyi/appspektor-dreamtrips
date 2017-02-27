package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.locatecard.LocateCardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AddBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AddListRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.DefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FactoryResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetCompatibleDevicesCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.RestartSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SyncCardsCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AvailabilitySmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAssociatedSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateProfileModule;
import com.worldventures.dreamtrips.wallet.service.command.profile.UpdateSmartCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.RemoveSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.WipeSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCompleteCommand;

import dagger.Module;

@Module(
      includes = {
            UpdateProfileModule.class
      },
      injects = {
            GetCompatibleDevicesCommand.class,
            CompressImageForSmartCardCommand.class,
            CardListCommand.class,
            AttachCardCommand.class,
            AddListRecordCommand.class,
            UpdateCardDetailsDataCommand.class,
            UpdateBankCardCommand.class,
            SyncCardsCommand.class,
            LoadImageForSmartCardCommand.class,
            SetupUserDataCommand.class,
            DefaultCardIdCommand.class,
            ActivateSmartCardCommand.class,
            CreateAndConnectToCardCommand.class,
            WizardCheckCommand.class,
            GetDefaultAddressCommand.class,
            AddBankCardCommand.class,
            SetupDefaultAddressCommand.class,
            SetDefaultCardOnDeviceCommand.class,
            SetPaymentCardAction.class,
            SetStealthModeCommand.class,
            ActiveSmartCardCommand.class,
            ConnectSmartCardCommand.class,
            FetchCardPropertiesCommand.class,
            FetchFirmwareVersionCommand.class,
            SetLockStateCommand.class,
            FetchDefaultCardCommand.class,
            FetchTermsAndConditionsCommand.class,
            CreateBankCardCommand.class,
            AssociateCardUserCommand.class,
            WizardCompleteCommand.class,
            FetchFirmwareInfoCommand.class,
            SetAutoClearSmartCardDelayCommand.class,
            WalletAnalyticsCommand.class,
            PaycardAnalyticsCommand.class,
            SetDisableDefaultCardDelayCommand.class,
            FetchAndStoreDefaultAddressInfoCommand.class,
            RemoveSmartCardDataCommand.class,
            ResetSmartCardCommand.class,
            FetchBatteryLevelCommand.class,
            FetchAssociatedSmartCardCommand.class,
            RestartSmartCardCommand.class,
            AvailabilitySmartCardCommand.class,
            UpdateSmartCardUserCommand.class,
            FactoryResetCommand.class,
            LocateCardAnalyticsCommand.class,
            WipeSmartCardDataCommand.class
      },
      complete = false, library = true)
public class WalletCommandModule {}

