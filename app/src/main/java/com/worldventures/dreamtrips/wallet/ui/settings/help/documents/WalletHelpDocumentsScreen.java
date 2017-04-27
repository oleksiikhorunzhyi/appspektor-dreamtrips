package com.worldventures.dreamtrips.wallet.ui.settings.help.documents;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.service.command.GetDocumentsCommand;
import com.worldventures.dreamtrips.modules.infopages.view.cell.DocumentCell;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;
import com.worldventures.dreamtrips.wallet.ui.common.base.WalletLinearLayout;
import com.worldventures.dreamtrips.wallet.ui.common.base.screen.OperationScreen;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.ErrorViewFactory;
import com.worldventures.dreamtrips.wallet.ui.common.helper2.error.http.HttpErrorViewProvider;

import java.util.List;

import butterknife.InjectView;
import io.techery.janet.operationsubscriber.view.ComposableOperationView;
import io.techery.janet.operationsubscriber.view.OperationView;

import static com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration.VERTICAL_LIST;

public class WalletHelpDocumentsScreen extends WalletLinearLayout<WalletHelpDocumentsPresenter.Screen, WalletHelpDocumentsPresenter, WalletHelpDocumentsPath>
      implements WalletHelpDocumentsPresenter.Screen, CellDelegate<Document>, SwipeRefreshLayout.OnRefreshListener {

   @InjectView(R.id.toolbar) Toolbar toolbar;
   @InjectView(R.id.recyclerView) RecyclerView rvDocuments;
   @InjectView(R.id.empty_view) TextView errorTv;

   private BaseDelegateAdapter adapter;
   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;

   public WalletHelpDocumentsScreen(Context context) {
      super(context);
   }

   public WalletHelpDocumentsScreen(Context context, AttributeSet attributeSet) {
      super(context, attributeSet);
   }

   @NonNull
   @Override
   public WalletHelpDocumentsPresenter createPresenter() {
      return new WalletHelpDocumentsPresenter(getContext(), getInjector());
   }

   @Override
   public OperationScreen provideOperationDelegate() {
      return null;
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      supportConnectionStatusLabel(false);
      if (isInEditMode()) return;
      toolbar.setNavigationOnClickListener(v -> onNavigationClick());
   }

   private void setUpView() {

      adapter = new BaseDelegateAdapter(this.getContext(), getInjector());
      adapter.registerCell(Document.class, DocumentCell.class, this);
      adapter.registerCell(LoadMoreModel.class, LoaderCell.class);

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(this);
      statePaginatedRecyclerViewManager.stateRecyclerView.setEmptyView(errorTv);
      statePaginatedRecyclerViewManager.init(adapter, new Bundle());
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL_LIST));
      statePaginatedRecyclerViewManager.setPaginationListener(() -> {
         if (!statePaginatedRecyclerViewManager.isNoMoreElements()) {
            adapter.addItem(new LoadMoreModel());
            adapter.notifyDataSetChanged();
         }

         getPresenter().loadNextDocuments();
      });
      statePaginatedRecyclerViewManager.startLoading();
   }

   @Override
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      setUpView();
   }

   protected void onNavigationClick() {
      presenter.goBack();
   }

   @Override
   public void onDocumentsLoaded(List<Document> documents) {
      statePaginatedRecyclerViewManager.finishLoading();
      adapter.setItems(documents);
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
                  .addProvider(new HttpErrorViewProvider<>(getContext(),
                        command -> presenter.refreshDocuments(),
                        command -> {}))
                  .build());
   }

   @Override
   public void onCellClicked(Document model) {
      presenter.openDocument(model);
   }

   @Override
   public void onRefresh() {
      getPresenter().refreshDocuments();
   }
}
