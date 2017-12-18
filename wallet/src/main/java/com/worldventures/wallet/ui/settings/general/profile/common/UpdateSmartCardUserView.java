package com.worldventures.wallet.ui.settings.general.profile.common;

import com.worldventures.wallet.service.profile.RetryHttpUploadUpdatingCommand;
import com.worldventures.wallet.service.profile.UpdateSmartCardUserCommand;
import com.worldventures.wallet.ui.common.base.screen.RxLifecycleView;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface UpdateSmartCardUserView extends RxLifecycleView {

   OperationView<UpdateSmartCardUserCommand> provideUpdateSmartCardOperation(WalletProfileDelegate delegate);

   OperationView<RetryHttpUploadUpdatingCommand> provideHttpUploadOperation(WalletProfileDelegate delegate);

}