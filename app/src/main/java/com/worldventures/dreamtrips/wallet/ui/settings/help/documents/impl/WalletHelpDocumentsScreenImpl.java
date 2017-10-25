package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.core.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.core.ui.view.DividerItemDecoration;
import com.worldventures.core.utils.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.MultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.SimpleMultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.WalletHelpDocumentsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.WalletHelpDocumentsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder.DocumentHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder.HelpDocsHolderFactoryImpl;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;

import java.util.ArrayList;

import javax.inject.Inject;

import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.core.ui.view.DividerItemDecoration.VERTICAL_LIST;

public class WalletHelpDocumentsScreenImpl extends WalletBaseController<WalletHelpDocumentsScreen, WalletHelpDocumentsPresenter> implements WalletHelpDocumentsScreen {

   private static final String ITEMS_KEY = "WalletHelpDocumentsScreenImpl#ITEMS_KEY";

   @Inject WalletHelpDocumentsPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private TextView errorTv;
   private MultiHolderAdapter adapter;
   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private ArrayList<WalletDocumentModel> documents;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      final Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
      errorTv = view.findViewById(R.id.empty_view);
   }

   @Override
   public View inflateView(LayoutInflater layoutInflater, ViewGroup viewGroup) {
      return layoutInflater.inflate(R.layout.screen_wallet_documents, viewGroup, false);
   }

   @Override
   public boolean supportConnectionStatusLabel() {
      return false;
   }

   @Override
   public boolean supportHttpConnectionStatusLabel() {
      return false;
   }

   @Override
   protected void onAttach(@NonNull View view) {
      super.onAttach(view);
      setUpView();
   }

   @Override
   public WalletHelpDocumentsPresenter getPresenter() {
      return presenter;
   }

   private void setUpView() {
      adapter = new SimpleMultiHolderAdapter<>(new ArrayList<>(), new HelpDocsHolderFactoryImpl(documentCallback));

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(getView());
      statePaginatedRecyclerViewManager.stateRecyclerView.setEmptyView(errorTv);

      statePaginatedRecyclerViewManager.init(adapter, new Bundle());
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL_LIST));
      statePaginatedRecyclerViewManager.setPaginationListener(() -> {
         if (!statePaginatedRecyclerViewManager.isNoMoreElements()) {
            adapter.addItem(new WalletLoadMoreModel());
            adapter.notifyDataSetChanged();
         }

         getPresenter().loadNextDocuments();
      });
      statePaginatedRecyclerViewManager.startLoading();
      if (documents != null) {
         adapter.clear();
         adapter.addItems(documents);
      }
   }

   private DocumentHolder.Callback documentCallback = walletDocument -> getPresenter().openDocument(walletDocument);

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public void onDocumentsLoaded(ArrayList<WalletDocumentModel> documents) {
      this.documents = documents;
      statePaginatedRecyclerViewManager.finishLoading();
      adapter.clear();
      adapter.addItems(documents);
   }

   @Override
   public void onError(String errorMessage) {
      statePaginatedRecyclerViewManager.finishLoading();
      errorTv.setText(errorMessage);
   }

   @Override
   protected void onSaveViewState(@NonNull View view, @NonNull Bundle outState) {
      outState.putParcelableArrayList(ITEMS_KEY, documents);
      super.onSaveViewState(view, outState);
   }

   @Override
   protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
      super.onRestoreViewState(view, savedViewState);
      documents = savedViewState.getParcelableArrayList(ITEMS_KEY);
   }

   @Override
   public OperationView<GetDocumentsCommand> provideOperationGetDocuments() {
      return new ComposableOperationView<>(
            ErrorViewFactory.<GetDocumentsCommand>builder()
                  .addProvider(new HttpErrorViewProvider<>(getContext(), httpErrorHandlingUtil,
                        command -> getPresenter().refreshDocuments(),
                        command -> {
                        }))
                  .build());
   }

   @Override
   public void onRefresh() {
      getPresenter().refreshDocuments();
   }
}
