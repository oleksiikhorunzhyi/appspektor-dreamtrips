package com.worldventures.dreamtrips.wallet.ui.records.detail;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetPaymentCardAction;
import com.worldventures.dreamtrips.wallet.service.command.record.DeleteRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.UpdateRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

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
