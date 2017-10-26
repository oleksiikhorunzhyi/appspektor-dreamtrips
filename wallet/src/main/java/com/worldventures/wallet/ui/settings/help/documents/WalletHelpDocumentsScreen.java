package com.worldventures.wallet.ui.settings.help.documents;

import android.support.v4.widget.SwipeRefreshLayout;

import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;

import java.util.ArrayList;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletHelpDocumentsScreen extends WalletScreen, SwipeRefreshLayout.OnRefreshListener {

   void onDocumentsLoaded(ArrayList<WalletDocumentModel> documents);

   void onError(String errorMessage);

   OperationView<GetDocumentsCommand> provideOperationGetDocuments();
}
