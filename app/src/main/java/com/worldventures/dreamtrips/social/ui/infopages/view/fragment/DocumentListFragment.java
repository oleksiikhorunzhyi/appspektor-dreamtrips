package com.worldventures.dreamtrips.social.ui.infopages.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.view.View;
import android.widget.TextView;

import com.worldventures.core.modules.infopages.bundle.DocumentBundle;
import com.worldventures.core.modules.infopages.model.Document;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.util.StatePaginatedRecyclerViewManager;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.social.ui.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.social.ui.infopages.presenter.DocumentListPresenter;
import com.worldventures.dreamtrips.social.ui.infopages.view.cell.DocumentCell;
import com.worldventures.dreamtrips.social.ui.infopages.view.fragment.staticcontent.DocumentFragment;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_documents)
public abstract class DocumentListFragment<P extends DocumentListPresenter> extends BaseFragment<P> implements CellDelegate<Document>,
      DocumentListPresenter.View, SwipeRefreshLayout.OnRefreshListener {

   @InjectView(R.id.emptyView) TextView emptyView;

   private BaseDelegateAdapter adapter;
   private StatePaginatedRecyclerViewManager statePaginatedRecyclerViewManager;
   private Bundle savedInstanceState;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.savedInstanceState = savedInstanceState;
   }

   @Override
   public void onResume() {
      super.onResume();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      adapter = new BaseDelegateAdapter(getContext(), this);
      adapter.registerCell(Document.class, DocumentCell.class, this);
      adapter.registerCell(LoadMoreModel.class, LoaderCell.class);

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView.findViewById(R.id.recyclerView),
            rootView.findViewById(R.id.swipe_container));
      statePaginatedRecyclerViewManager.getStateRecyclerView().setEmptyView(emptyView);
      statePaginatedRecyclerViewManager.init(adapter, savedInstanceState);
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
      statePaginatedRecyclerViewManager.setPaginationListener(() -> {
         if (!statePaginatedRecyclerViewManager.isNoMoreElements()) {
            adapter.addItem(new LoadMoreModel());
            adapter.notifyDataSetChanged();
         }

         getPresenter().loadNextDocuments();
      });
   }

   @Override
   public void onCellClicked(Document document) {
      router.moveTo(DocumentFragment.class, NavigationConfigBuilder.forActivity()
            .data(new DocumentBundle(document, getPresenter().getAnalyticsActionForOpenedItem(document))).build());
   }

   @Override
   public boolean isAdapterEmpty() {
      return adapter.getItemCount() == 0;
   }

   @Override
   public void setDocumentList(List<Document> documentList) {
      adapter.setItems(documentList);
   }

   @Override
   public void showProgress() {
      statePaginatedRecyclerViewManager.startLoading();
   }

   @Override
   public void hideProgress() {
      statePaginatedRecyclerViewManager.finishLoading();
   }

   @Override
   public void updateLoadingStatus(boolean noMoreElements) {
      statePaginatedRecyclerViewManager.updateLoadingStatus(false, noMoreElements);
   }

   @Override
   public void onRefresh() {
      getPresenter().refreshDocuments();
   }
}