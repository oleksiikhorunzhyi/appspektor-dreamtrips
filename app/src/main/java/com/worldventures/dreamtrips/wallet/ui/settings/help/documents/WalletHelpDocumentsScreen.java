package com.worldventures.dreamtrips.wallet.ui.settings.help.documents;


import android.support.v4.widget.SwipeRefreshLayout;

import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.WalletDocument;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletHelpDocumentsScreen extends WalletScreen, SwipeRefreshLayout.OnRefreshListener {

   void onDocumentsLoaded(List<WalletDocument> documents);

   void onError(String errorMessage);

   OperationView<GetDocumentsCommand> provideOperationGetDocuments();

}
