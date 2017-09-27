package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper;

import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;

import io.techery.janet.operationsubscriber.view.OperationView;
import rx.Observable;

public interface InputDelegateView {

   OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus();

   void showErrorCardIsAssignedDialog();

   <T> Observable.Transformer<T, T> bindToLifecycle();
}
