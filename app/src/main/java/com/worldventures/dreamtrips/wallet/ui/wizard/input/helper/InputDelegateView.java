package com.worldventures.dreamtrips.wallet.ui.wizard.input.helper;

import com.worldventures.dreamtrips.wallet.service.command.http.GetSmartCardStatusCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.RxLifecycleView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface InputDelegateView extends RxLifecycleView {

   OperationView<GetSmartCardStatusCommand> provideOperationFetchCardStatus();

   void showErrorCardIsAssignedDialog();
}
