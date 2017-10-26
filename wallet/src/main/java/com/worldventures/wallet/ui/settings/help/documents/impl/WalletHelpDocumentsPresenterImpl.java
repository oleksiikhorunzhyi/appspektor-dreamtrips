package com.worldventures.wallet.ui.settings.help.documents.impl;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.modules.infopages.model.Document;
import com.worldventures.core.modules.infopages.service.DocumentsInteractor;
import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.wallet.ui.common.base.WalletDeviceConnectionDelegate;
import com.worldventures.wallet.ui.common.base.WalletPresenterImpl;
import com.worldventures.wallet.ui.common.navigation.Navigator;
import com.worldventures.wallet.ui.settings.help.documents.WalletHelpDocumentsPresenter;
import com.worldventures.wallet.ui.settings.help.documents.WalletHelpDocumentsScreen;
import com.worldventures.wallet.ui.settings.help.documents.model.WalletDocumentModel;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand.DocumentType.SMARTCARD;

public class WalletHelpDocumentsPresenterImpl extends WalletPresenterImpl<WalletHelpDocumentsScreen> implements WalletHelpDocumentsPresenter {

   private final DocumentsInteractor documentsInteractor;

   public WalletHelpDocumentsPresenterImpl(Navigator navigator, WalletDeviceConnectionDelegate deviceConnectionDelegate,
         DocumentsInteractor documentsInteractor) {
      super(navigator, deviceConnectionDelegate);
      this.documentsInteractor = documentsInteractor;
   }

   @Override
   public void attachView(WalletHelpDocumentsScreen view) {
      super.attachView(view);
      observeDocumentsChanges();
      refreshDocuments();
   }

   @Override
   public void refreshDocuments() {
      documentsInteractor.getDocumentsActionPipe().send(new GetDocumentsCommand(SMARTCARD, true));
   }

   @Override
   public void loadNextDocuments() {
      documentsInteractor.getDocumentsActionPipe().send(new GetDocumentsCommand(SMARTCARD));
   }

   private void observeDocumentsChanges() {
      documentsInteractor.getDocumentsActionPipe()
            .observe()
            .compose(getView().bindUntilDetach())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationGetDocuments())
                  .onSuccess(documentResponse -> getView().onDocumentsLoaded(convert(documentResponse.getResult())))
                  .onFail((command, error) -> getView().onError(command.getErrorMessage()))
                  .create());
   }

   private ArrayList<WalletDocumentModel> convert(List<Document> documents) {
      return (ArrayList<WalletDocumentModel>) Queryable.from(documents).map(WalletDocumentModel::new).toList();
   }

   @Override
   public void goBack() {
      getNavigator().goBack();
   }

   @Override
   public void openDocument(WalletDocumentModel model) {
      getNavigator().goHelpDocumentDetails(model);
   }
}
