package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.loader.ContentLoader;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Video;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.presentation.MembershipPM;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.utils.busevents.ScreenOrientationChangeEvent;
import com.worldventures.dreamtrips.view.cell.VideoCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_member_ship)
@MenuResource(R.menu.menu_membership)
public class MemberShipFragment extends BaseFragment<MembershipPM> implements BasePresentation.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    BaseArrayListAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
      //  this.recyclerView.setEmptyView(emptyView);

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(Video.class, VideoCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        this.arrayListAdapter.setContentLoader(getPresentationModel().getAdapterController());
        AdobeTrackingHelper.video();

        getPresentationModel().getAdapterController().getContentLoaderObserver().registerObserver(new ContentLoader.ContentLoadingObserving<List<Object>>() {
            @Override
            public void onStartLoading() {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onFinishLoading(List<Object> result) {
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable throwable) {
                refreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        getPresentationModel().actionEnroll();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.arrayListAdapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> {
                getPresentationModel().getAdapterController().reload();
            });
        }
    }

    @Override
    public void onRefresh() {
        getPresentationModel().getAdapterController().reload();
    }

    @Override
    protected MembershipPM createPresentationModel(Bundle savedInstanceState) {
        return new MembershipPM(this);
    }

    public void onEvent(ScreenOrientationChangeEvent event) {
        boolean landscape = event.isLandscape();
        setupLayoutManager(landscape);
    }

    private void setupLayoutManager(boolean landscape) {
        int spanCount = landscape ? 2 : 1;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        this.recyclerView.setLayoutManager(layoutManager);
    }
}
