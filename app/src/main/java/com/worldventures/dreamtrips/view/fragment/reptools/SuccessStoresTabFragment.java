package com.worldventures.dreamtrips.view.fragment.reptools;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.loader.ContentLoader;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.presentation.SuccessStoresTabPM;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.view.adapter.SuccessStoryHeaderAdapter;
import com.worldventures.dreamtrips.view.cell.SuccessStoryCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_success_stores)
public class SuccessStoresTabFragment extends BaseFragment<SuccessStoresTabPM> implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.recyclerViewTrips)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    BaseArrayListAdapter<SuccessStory> adapter;

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
        this.adapter.setContentLoader(getPresentationModel().getStoresLoader());
        this.adapter.setHasStableIds(true);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        getPresentationModel().getStoresLoader().getContentLoaderObserver().registerObserver(new ContentLoader.ContentLoadingObserving<List<SuccessStory>>() {
            @Override
            public void onStartLoading() {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onFinishLoading(List<SuccessStory> result) {
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable throwable) {
                refreshLayout.setRefreshing(false);
            }
        });


        StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
                .setAdapter(adapter)
                .setRecyclerView(recyclerView)
                .setStickyHeadersAdapter(
                        new SuccessStoryHeaderAdapter(),
                        true)
                .build();

        recyclerView.addItemDecoration(decoration);

    }

    @Override
    public void onRefresh() {
        getPresentationModel().reload();
    }
}
