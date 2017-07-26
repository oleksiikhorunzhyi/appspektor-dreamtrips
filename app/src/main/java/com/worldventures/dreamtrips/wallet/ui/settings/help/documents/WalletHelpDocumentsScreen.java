package com.worldventures.dreamtrips.wallet.ui.settings.help.documents;


import android.support.v4.widget.SwipeRefreshLayout;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;

import java.util.List;

import io.techery.janet.operationsubscriber.view.OperationView;

public interface WalletHelpDocumentsScreen extends WalletScreen, CellDelegate<Document>, SwipeRefreshLayout.OnRefreshListener {

   void onDocumentsLoaded(List<Document> documents);

   void onError(String errorMessage);

   OperationView<GetDocumentsCommand> provideOperationGetDocuments();

}
