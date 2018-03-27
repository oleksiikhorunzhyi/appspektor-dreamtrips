package com.worldventures.wallet.ui.settings.general.reset.delegate;

import com.worldventures.wallet.service.command.reset.ResetSmartCardCommand;
import com.worldventures.wallet.ui.common.base.screen.RxLifecycleView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface FactoryResetView extends RxLifecycleView {

   OperationView<ResetSmartCardCommand> provideResetOperationView(FactoryResetDelegate factoryResetDelegate);

}
