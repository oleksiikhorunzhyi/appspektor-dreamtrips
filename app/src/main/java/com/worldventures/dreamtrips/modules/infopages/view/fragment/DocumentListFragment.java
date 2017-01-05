package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.feed.model.LoadMoreModel;
import com.worldventures.dreamtrips.modules.feed.view.cell.LoaderCell;
import com.worldventures.dreamtrips.modules.feed.view.util.StatePaginatedRecyclerViewManager;
import com.worldventures.dreamtrips.modules.infopages.bundle.DocumentBundle;
import com.worldventures.dreamtrips.modules.infopages.model.Document;
import com.worldventures.dreamtrips.modules.infopages.presenter.DocumentListPresenter;
import com.worldventures.dreamtrips.modules.infopages.view.cell.DocumentCell;
import com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration;

import java.util.List;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.modules.membership.view.util.DividerItemDecoration.VERTICAL_LIST;

@Layout(R.layout.fragment_documents)
public class DocumentListFragment extends BaseFragment<DocumentListPresenter> implements CellDelegate<Document>,
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
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      adapter = new BaseDelegateAdapter(getContext(), this);
      adapter.registerCell(Document.class, DocumentCell.class, this);
      adapter.registerCell(LoadMoreModel.class, LoaderCell.class);

      statePaginatedRecyclerViewManager = new StatePaginatedRecyclerViewManager(rootView);
      statePaginatedRecyclerViewManager.stateRecyclerView.setEmptyView(emptyView);
      statePaginatedRecyclerViewManager.init(adapter, savedInstanceState);
      statePaginatedRecyclerViewManager.setOnRefreshListener(this);
      statePaginatedRecyclerViewManager.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL_LIST));
      statePaginatedRecyclerViewManager.setPaginationListener(() -> {
         if (!statePaginatedRecyclerViewManager.isNoMoreElements()) {
            adapter.addItem(new LoadMoreModel());
            adapter.notifyDataSetChanged();
         }

         getPresenter().loadNextDocuments();
      });
   }

   @Override
   protected DocumentListPresenter createPresenter(Bundle savedInstanceState) {
      return new DocumentListPresenter();
   }

   @Override
   public void onCellClicked(Document document) {
      router.moveTo(Route.DOCUMENT, NavigationConfigBuilder.forActivity()
            .data(new DocumentBundle(document.getUrl(), document.getName()))
            .build());
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