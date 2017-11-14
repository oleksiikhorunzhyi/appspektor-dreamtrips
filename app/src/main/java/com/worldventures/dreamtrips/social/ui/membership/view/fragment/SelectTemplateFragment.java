package com.worldventures.dreamtrips.social.ui.membership.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.social.domain.entity.InviteTemplate;
import com.worldventures.dreamtrips.social.ui.membership.bundle.TemplateBundle;
import com.worldventures.dreamtrips.social.ui.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.social.ui.membership.view.cell.InviteTemplateCell;
import com.worldventures.dreamtrips.social.ui.reptools.view.adapter.SuccessStoryHeaderAdapter;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_select_template)
public class SelectTemplateFragment extends BaseFragment<SelectTemplatePresenter> implements SelectTemplatePresenter.View, SwipeRefreshLayout.OnRefreshListener {

   @InjectView(R.id.lv_templates) RecyclerView templates;
   @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeContainer;

   private BaseDelegateAdapter<InviteTemplate> adapter;
   private WeakHandler weakHandler;

   @Override
   protected SelectTemplatePresenter createPresenter(Bundle savedInstanceState) {
      return new SelectTemplatePresenter();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      weakHandler = new WeakHandler();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      templates.setLayoutManager(new LinearLayoutManager(getActivity()));
      adapter = new BaseDelegateAdapter<>(getActivity(), this);
      adapter.registerCell(InviteTemplate.class, InviteTemplateCell.class);
      adapter.registerDelegate(InviteTemplate.class, getPresenter()::onTemplateSelected);
      adapter.setHasStableIds(true);

      templates.setAdapter(adapter);
      swipeContainer.setOnRefreshListener(this);
      swipeContainer.setColorSchemeResources(R.color.theme_main_darker);

      StickyHeadersItemDecoration decoration = new StickyHeadersBuilder().setAdapter(adapter)
            .setRecyclerView(templates)
            .setStickyHeadersAdapter(new SuccessStoryHeaderAdapter(adapter.getItems(), R.layout.adapter_template_header), false)
            .build();

      templates.addItemDecoration(decoration);
   }

   @Override
   public void startLoading() {
      weakHandler.post(() -> {
         if (swipeContainer != null) {
            swipeContainer.setRefreshing(true);
         }
      });
   }

   @Override
   public void finishLoading() {
      weakHandler.post(() -> {
         if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
         }
      });
   }

   @Override
   public void addItems(@NonNull List<InviteTemplate> inviteTemplates) {
      adapter.clear();
      adapter.addItems(inviteTemplates);
   }

   @Override
   public void onRefresh() {
      getPresenter().reload();
   }

   @Override
   public void openTemplate(@NonNull TemplateBundle templateBundle) {
      router.moveTo(EditTemplateFragment.class, NavigationConfigBuilder.forActivity()
            .toolbarConfig(ToolbarConfig.Builder.create().visible(true).build())
            .data(templateBundle)
            .build());
   }
}
