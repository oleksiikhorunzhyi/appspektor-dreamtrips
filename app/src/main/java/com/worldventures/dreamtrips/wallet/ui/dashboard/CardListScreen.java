package com.worldventures.dreamtrips.wallet.ui.dashboard;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.design.widget.FloatingActionButton;
import android.widget.TextView;

import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.service.command.SyncSmartCardCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.SyncRecordOnNewDeviceCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.BaseViewModel;
import com.worldventures.dreamtrips.wallet.ui.settings.general.reset.FactoryResetView;

import java.util.ArrayList;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface CardListScreen extends WalletScreen, FactoryResetView {

   int ERROR_DIALOG_FULL_SMARTCARD = 1;
   int ERROR_DIALOG_NO_INTERNET_CONNECTION = 2;
   int ERROR_DIALOG_NO_SMARTCARD_CONNECTION = 3;

   void showRecordsInfo(ArrayList<BaseViewModel> result);

   void setDefaultSmartCard();

   void setSmartCardStatusAttrs(int batteryLevel, boolean connected, boolean lock, boolean stealthMode);

   void setSmartCardUser(SmartCardUser smartCardUser);

   void setCardsCount(int count);

   void setDisplayType(int displayType);

   void showAddCardErrorDialog(@ErrorDialogType int errorDialogType);

   void hideFirmwareUpdateBtn();

   void showFirmwareUpdateBtn();

   void showFirmwareUpdateError();

   void showForceFirmwareUpdateDialog();

   void showFactoryResetConfirmationDialog();

   @IntDef({ERROR_DIALOG_FULL_SMARTCARD, ERROR_DIALOG_NO_INTERNET_CONNECTION, ERROR_DIALOG_NO_SMARTCARD_CONNECTION})
   @interface ErrorDialogType {}

   void showSCNonConnectionDialog();

   void modeAddCard();

   void modeSyncPaymentsFab();

   void showSyncFailedOptionsDialog();

   OperationView<SyncSmartCardCommand> provideOperationSyncSmartCard();

   OperationView<SyncRecordOnNewDeviceCommand> provideReSyncOperationView();

   Context getViewContext();

   FloatingActionButton getCardListFab();

   TextView getEmptyCardListView();
}
