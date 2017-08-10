package com.worldventures.dreamtrips.wallet.ui.settings.help.documents.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;
import com.worldventures.dreamtrips.util.HttpErrorHandlingUtil;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletDocumentModel;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.model.WalletLoadMoreModel;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.MultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.adapter.SimpleMultiHolderAdapter;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletBaseController;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.WalletHelpDocumentsPresenter;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.WalletHelpDocumentsScreen;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder.DocumentHolder;
import com.worldventures.dreamtrips.wallet.ui.settings.help.documents.holder.HelpDocsHolderFactoryImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration.VERTICAL_LIST;

public class WalletHelpDocumentsScreenImpl extends WalletBaseController<WalletHelpDocumentsScreen, WalletHelpDocumentsPresenter> implements WalletHelpDocumentsScreen {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recyclerView) RecyclerView rvDocuments;
   @InjectView(R.id.empty_view) TextView errorTv;

   @Inject WalletHelpDocumentsPresenter presenter;
   @Inject HttpErrorHandlingUtil httpErrorHandlingUtil;

   private MultiHolderAdapter adapter;
   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;

   @Override
   protected void onFinishInflate(View view) {
      super.onFinishInflate(view);
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
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
   }

   private DocumentHolder.Callback documentCallback = walletDocument -> getPresenter().openDocument(walletDocument);

   protected void onNavigationClick() {
      getPresenter().goBack();
   }

   @Override
   public void onDocumentsLoaded(List<WalletDocumentModel> documents) {
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
