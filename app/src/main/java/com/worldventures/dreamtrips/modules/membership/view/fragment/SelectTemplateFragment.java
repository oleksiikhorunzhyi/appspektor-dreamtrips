package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.bundle.TemplateBundle;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.view.cell.InviteTemplateCell;
import com.worldventures.dreamtrips.modules.reptools.view.adapter.SuccessStoryHeaderAdapter;

import java.util.ArrayList;

import butterknife.InjectView;

@Layout(R.layout.fragment_select_template)
public class SelectTemplateFragment extends BaseFragment<SelectTemplatePresenter>
        implements SelectTemplatePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.lv_templates)
    RecyclerView lvTemplates;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

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
        lvTemplates.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseDelegateAdapter<>(getActivity(), this);
        adapter.registerCell(InviteTemplate.class, InviteTemplateCell.class);
        adapter.registerDelegate(InviteTemplate.class, getPresenter()::onTemplateSelected);
        adapter.setHasStableIds(true);

        lvTemplates.setAdapter(adapter);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);

        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(lvTemplates)
                .setStickyHeadersAdapter(new SuccessStoryHeaderAdapter(adapter.getItems(),
                        R.layout.adapter_template_header), false)
                .build();

        lvTemplates.addItemDecoration(decoration);
    }

    @Override
    public void startLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(true);
        });
    }

    @Override
    public void finishLoading() {
        weakHandler.post(() -> {
            if (swipeContainer != null) swipeContainer.setRefreshing(false);
        });
    }

    @Override
    public void addItems(ArrayList<InviteTemplate> inviteTemplates) {
        adapter.clear();
        adapter.addItems(inviteTemplates);
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    public void openTemplate(TemplateBundle templateBundle) {
        router.moveTo(Route.EDIT_INVITE_TEMPLATE, NavigationConfigBuilder.forActivity()
                .toolbarConfig(ToolbarConfig.Builder.create().visible(true).build())
                .data(templateBundle)
                .build());
    }
}
