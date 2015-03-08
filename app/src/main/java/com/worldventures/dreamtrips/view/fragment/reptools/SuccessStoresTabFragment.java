package com.worldventures.dreamtrips.view.fragment.reptools;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.presentation.SuccessStoresTabPM;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.utils.busevents.OnSuccessStoryCellClickEvent;
import com.worldventures.dreamtrips.view.adapter.SuccessStoryHeaderAdapter;
import com.worldventures.dreamtrips.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_success_stores)
public class SuccessStoresTabFragment extends BaseFragment<SuccessStoresTabPM> implements SwipeRefreshLayout.OnRefreshListener, SuccessStoresTabPM.View {

    @InjectView(R.id.recyclerViewTrips)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.detail_container)
    FrameLayout flDetailContainer;

    BaseArrayListAdapter adapter;

    @Override
    protected SuccessStoresTabPM createPresentationModel(Bundle savedInstanceState) {
        return new SuccessStoresTabPM(this);
    }


    @Override
    public void afterCreateView(View rootView) {
        AdobeTrackingHelper.dreamTrips();
        super.afterCreateView(rootView);

        this.adapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.adapter.registerCell(SuccessStory.class, SuccessStoryCell.class);
        this.adapter.setHasStableIds(true);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(recyclerView)
                .setStickyHeadersAdapter(new SuccessStoryHeaderAdapter(adapter.getItems()), true)
                .build();

        recyclerView.addItemDecoration(decoration);
    }

    @Override
    public void onRefresh() {
        getPresentationModel().reload();
    }

    @Override
    public boolean isTablet() {
        return ViewUtils.isTablet(getActivity());
    }

    @Override
    public boolean isLandscape() {
        return ViewUtils.isLandscapeOrientation(getActivity());
    }

    @Override
    public void setDetailsContainerVisibility(boolean b) {
        flDetailContainer.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    @Override
    public IRoboSpiceAdapter<SuccessStory> getAdapter() {
        return adapter;
    }

    @Override
    public void finishLoading(List<SuccessStory> result) {
        new Handler().postDelayed(() -> {
            refreshLayout.setRefreshing(false);
            if (isLandscape() && isTablet()) {
                if (!result.isEmpty()) {
                    getEventBus().post(new OnSuccessStoryCellClickEvent(result.get(0), 1));
                }
            }
        }, 500);
    }

    @Override
    public void startLoading() {
        refreshLayout.setRefreshing(true);
    }

}
