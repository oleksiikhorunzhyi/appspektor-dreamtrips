package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.wallet.service.command.ActivateSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.AttachCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardCountCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardListCommand;
import com.worldventures.dreamtrips.wallet.service.command.CardStacksCommand;
import com.worldventures.dreamtrips.wallet.service.command.CompressImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.ConnectSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.CreateAndConnectToCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchCardPropertiesCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchDefaultCardIdCommand;
import com.worldventures.dreamtrips.wallet.service.command.FetchSmartCardLockState;
import com.worldventures.dreamtrips.wallet.service.command.GetActiveSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.LoadImageForSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveCardDetailsDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.SaveDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetAutoClearSmartCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDisableDefaultCardDelayCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetLockStateCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetStealthModeCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetupUserDataCommand;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchRecordIssuerInfoCommand;
import com.worldventures.dreamtrips.wallet.service.command.UpdateSmartCardConnectionStatus;
import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;

import dagger.Module;

@Module(
      includes = {},
      injects = {
            CompressImageForSmartCardCommand.class,
            CardListCommand.class,
            AttachCardCommand.class,
            CardStacksCommand.class,
            LoadImageForSmartCardCommand.class,
            SetupUserDataCommand.class,
            FetchDefaultCardIdCommand.class,
            ActivateSmartCardCommand.class,
            CreateAndConnectToCardCommand.class,
            GetActiveSmartCardCommand.class,
            CardCountCommand.class,
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
            SaveDefaultAddressCommand.class,
            FetchDefaultCardCommand.class,
            FetchTermsAndConditionsCommand.class,
            FetchRecordIssuerInfoCommand.class,
            SetAutoClearSmartCardDelayCommand.class,
            SetDisableDefaultCardDelayCommand.class
      },
      complete = false, library = true)
public class WalletCommandModule {}

