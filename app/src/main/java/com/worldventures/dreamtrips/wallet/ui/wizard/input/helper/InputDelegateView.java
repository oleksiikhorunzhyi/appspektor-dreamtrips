package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper;

import android.view.View;

import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface InputDelegateView {

   OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus();

   void showErrorCardIsAssignedDialog();

   View getView();

   void reset();
}
