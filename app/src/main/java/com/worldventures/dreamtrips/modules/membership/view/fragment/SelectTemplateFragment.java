package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.membership.model.InviteTemplate;
import com.worldventures.dreamtrips.modules.membership.presenter.SelectTemplatePresenter;
import com.worldventures.dreamtrips.modules.membership.view.cell.InviteTemplateCell;
import com.worldventures.dreamtrips.modules.reptools.view.adapter.HeaderAdapter;

import java.util.ArrayList;

import butterknife.InjectView;

@Layout(R.layout.fragment_select_template)
public class SelectTemplateFragment extends BaseFragment<SelectTemplatePresenter> implements SelectTemplatePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.lv_templates)
    RecyclerView lvTemplates;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    BaseArrayListAdapter adapter;

    @Override
    protected SelectTemplatePresenter createPresenter(Bundle savedInstanceState) {
        return new SelectTemplatePresenter(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        lvTemplates.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseArrayListAdapter<>(getActivity(), (Injector) getActivity());
        adapter.registerCell(InviteTemplate.class, InviteTemplateCell.class);
        adapter.setHasStableIds(true);
        lvTemplates.setAdapter(adapter);
        swipeContainer.setOnRefreshListener(this);
        swipeContainer.setColorSchemeResources(R.color.theme_main_darker);

        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(lvTemplates)
                .setStickyHeadersAdapter(new HeaderAdapter(adapter.getItems(),
                        R.layout.adapter_item_succes_story_header), false)
                .build();

        lvTemplates.addItemDecoration(decoration);
    }

    @Override
    public void startLoading() {
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));
    }

    @Override
    public void finishLoading() {
        swipeContainer.post(() -> swipeContainer.setRefreshing(false));
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
