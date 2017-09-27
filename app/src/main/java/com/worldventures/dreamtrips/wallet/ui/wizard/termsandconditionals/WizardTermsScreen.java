package com.worldventures.dreamtrips.wallet.ui.wizard.termsandconditionals;

import com.worldventures.dreamtrips.wallet.service.command.http.FetchTermsAndConditionsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import io.techery.janet.operationsubscriber.view.ErrorView;
import io.techery.janet.operationsubscriber.view.OperationView;

public interface WizardTermsScreen extends WalletScreen, ErrorView {

   void showTerms(String url);

   OperationView<FetchTermsAndConditionsCommand> termsOperationView();

}
