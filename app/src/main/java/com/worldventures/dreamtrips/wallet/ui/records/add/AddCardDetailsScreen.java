package com.worldventures.dreamtrips.wallet.ui.records.add;

import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.records.model.RecordViewModel;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface AddCardDetailsScreen extends WalletScreen {

   void setCardBank(RecordViewModel record);

   void setCardName(String cardName);

   Observable<String> getCardNicknameObservable();

   Observable<String> getCvvObservable();

   void defaultPaymentCard(boolean defaultPaymentCard);

   void showChangeCardDialog(Record record);

   Observable<Boolean> setAsDefaultPaymentCardCondition();

   void setEnableButton(boolean enable);

   void showCardNameError();

   void hideCardNameError();

   OperationView<AddRecordCommand> provideOperationAddRecord();

   RecordViewModel getRecordViewModel();
}
