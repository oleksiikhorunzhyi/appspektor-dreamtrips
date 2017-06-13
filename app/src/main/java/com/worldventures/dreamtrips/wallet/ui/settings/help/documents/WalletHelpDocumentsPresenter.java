package com.worldventures.dreamtrips.wallet.ui.settings.help.documents;


import android.content.Context;
import android.os.Parcelable;
import android.util.Log;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.DocumentsInteractor;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletPresenter;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.WalletScreen;
import com.worldventures.dreamtrips.wallet.ui.common.navigation.Navigator;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.doc.HelpDocumentPath;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.OperationActionSubscriber;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand.DocumentType.SMARTCARD;

public class WalletHelpDocumentsPresenter extends WalletPresenter<WalletHelpDocumentsPresenter.Screen, Parcelable> {

   @Inject Navigator navigator;
   @Inject DocumentsInteractor documentsInteractor;

   public WalletHelpDocumentsPresenter(Context context, Injector injector) {
      super(context, injector);
   }

   @Override
   public void onAttachedToWindow() {
      super.onAttachedToWindow();
      observeDocumentsChanges();
      refreshDocuments();
   }

   public void refreshDocuments() {
      documentsInteractor.getDocumentsActionPipe().send(new GetDocumentsCommand(SMARTCARD, true));
   }

   public void loadNextDocuments() {
      documentsInteractor.getDocumentsActionPipe().send(new GetDocumentsCommand(SMARTCARD));
   }

   private void observeDocumentsChanges() {
      documentsInteractor.getDocumentsActionPipe()
            .observe()
            .compose(bindViewIoToMainComposer())
            .subscribe(OperationActionSubscriber.forView(getView().provideOperationGetDocuments())
                  .onSuccess(documentResponse -> getView().onDocumentsLoaded(documentResponse.getResult()))
                  .onFail((command, error) -> getView().onError(command.getErrorMessage()))
                  .create());
   }

   public void goBack() {
      navigator.goBack();
   }

   public void openDocument(Document model) {
      navigator.go(new HelpDocumentPath(model));
   }

   public interface Screen extends WalletScreen {

      void onDocumentsLoaded(List<Document> documents);

      void onError(String errorMessage);

      OperationView<GetDocumentsCommand> provideOperationGetDocuments();
   }


}
