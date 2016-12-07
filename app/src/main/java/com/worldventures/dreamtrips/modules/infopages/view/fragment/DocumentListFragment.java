package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
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

   @InjectView(R.id.documentsList) RecyclerView documentsList;
   @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;

   private BaseDelegateAdapter adapter;
   private WeakHandler weakHandler;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      weakHandler = new WeakHandler();

      swipeContainer.setColorSchemeResources(R.color.theme_main_darker);
      swipeContainer.setOnRefreshListener(this);

      adapter = new BaseDelegateAdapter(getContext(), this);
      adapter.registerCell(Document.class, DocumentCell.class, this);

      documentsList.setLayoutManager(new LinearLayoutManager(getContext()));
      documentsList.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL_LIST));
      documentsList.setAdapter(adapter);
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
   public void setDocumentList(List<Document> documentList) {
      adapter.setItems(documentList);
   }

   @Override
   public void showProgress() {
      weakHandler.post(() -> swipeContainer.setRefreshing(true));
   }

   @Override
   public void hideProgress() {
      weakHandler.post(() -> swipeContainer.setRefreshing(false));
   }

   @Override
   public void onRefresh() {
      getPresenter().getDocuments();
   }
}