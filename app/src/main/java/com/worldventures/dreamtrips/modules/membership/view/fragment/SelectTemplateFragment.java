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
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.view.cell.InviteTemplateCell;
import com.worldventures.dreamtrips.modules.reptools.view.adapter.HeaderAdapter;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_select_template)
public class SelectTemplateFragment extends BaseFragment<SelectTemplatePresenter> implements SelectTemplatePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @Inject
    @ForActivity
    Provider<Injector> injector;

    @InjectView(R.id.lv_templates)
    RecyclerView lvTemplates;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    BaseArrayListAdapter adapter;
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
        adapter = new FilterableArrayListAdapter<>(getActivity(), injector);
        adapter.registerCell(InviteTemplate.class, InviteTemplateCell.class);
        adapter.setHasStableIds(true);

        lvTemplates.setAdapter(adapter);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);

        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(lvTemplates)
                .setStickyHeadersAdapter(new HeaderAdapter(adapter.getItems(),
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
}
