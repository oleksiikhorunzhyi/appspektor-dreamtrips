package com.worldventures.wallet.ui.records.detail;

import com.worldventures.wallet.domain.entity.record.Record;
import com.worldventures.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.wallet.service.command.SetPaymentCardAction;
import com.worldventures.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface CardDetailsScreen extends WalletScreen {

   void showDefaultCardDialog(Record defaultRecord);

   void showDeleteCardDialog();

   void showNetworkConnectionErrorDialog();

   void showCardIsReadyDialog(String cardName);

   void showSCNonConnectionDialog();

   void showCardNameError();

   void hideCardNameError();

   OperationView<UpdateRecordCommand> provideOperationSaveCardData();

   void notifyCardDataIsSaved();

   void defaultCardChanged(boolean isDefault);

   void undoDefaultCardChanges();

   OperationView<DeleteRecordCommand> provideOperationDeleteRecord();

   OperationView<SetDefaultCardOnDeviceCommand> provideOperationSetDefaultOnDevice();

   OperationView<SetPaymentCardAction> provideOperationSetPaymentCardAction();

   RecordDetailViewModel getDetailViewModel();
}
