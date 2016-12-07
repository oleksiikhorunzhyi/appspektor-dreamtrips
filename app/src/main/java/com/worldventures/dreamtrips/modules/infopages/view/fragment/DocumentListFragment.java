package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

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
      DocumentListPresenter.View {

   @InjectView(R.id.documentsList) RecyclerView documentsList;
   @InjectView(R.id.progress) ProgressBar progressBar;

   private BaseDelegateAdapter adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

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
      progressBar.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideProgress() {
      progressBar.setVisibility(View.GONE);
   }
}