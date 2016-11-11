package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.wallet.analytics.PaycardAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.analytics.WalletAnalyticsCommand;
import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchBatteryLevelCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchSmartCardLockState;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateSmartCardConnectionStatus;
import com.worldventures.dreamtrips.wallet.service.command.firmware.InstallFirmwareCommand;
import com.worldventures.dreamtrips.wallet.service.command.firmware.PreInstallationCheckCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.AssociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.CreateBankCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateActiveCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.DisassociateCardUserCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchAndStoreDefaultAddressInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchFirmwareInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ConfirmResetCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.RemoveSmartCardDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.wizard.WizardCheckCommand;

import dagger.Module;

@Module(
      includes = {},
      injects = {
            CompressImageForSmartCardCommand.class,
            CardListCommand.class,
            AttachCardCommand.class,
            UpdateCardDetailsDataCommand.class,
            UpdateBankCardCommand.class,
            CardStacksCommand.class,
            LoadImageForSmartCardCommand.class,
            SetupUserDataCommand.class,
            FetchDefaultCardIdCommand.class,
            ActivateSmartCardCommand.class,
            CreateAndConnectToCardCommand.class,
            WizardCheckCommand.class,
            GetDefaultAddressCommand.class,
            SaveCardDetailsDataCommand.class,
            SetupDefaultAddressCommand.class,
            SetDefaultCardOnDeviceCommand.class,
            FetchSmartCardLockState.class,
            UpdateSmartCardConnectionStatus.class,
            SetStealthModeCommand.class,
            GetActiveSmartCardCommand.class,
            ConnectSmartCardCommand.class,
            FetchCardPropertiesCommand.class,
            SetLockStateCommand.class,
            FetchDefaultCardCommand.class,
            FetchTermsAndConditionsCommand.class,
            CreateBankCardCommand.class,
            AssociateCardUserCommand.class,
            FetchFirmwareInfoCommand.class,
            PreInstallationCheckCommand.class,
            SetAutoClearSmartCardDelayCommand.class,
            InstallFirmwareCommand.class,
            WalletAnalyticsCommand.class,
            PaycardAnalyticsCommand.class,
            SetDisableDefaultCardDelayCommand.class,
            FetchAndStoreDefaultAddressInfoCommand.class,
            RemoveSmartCardDataCommand.class,
            ResetSmartCardCommand.class,
            ConfirmResetCommand.class,
            FetchBatteryLevelCommand.class,
            DisassociateCardUserCommand.class,
            DisassociateActiveCardUserCommand.class,
            SaveLockStateCommand.class
      },
      complete = false, library = true)
public class WalletCommandModule {}

