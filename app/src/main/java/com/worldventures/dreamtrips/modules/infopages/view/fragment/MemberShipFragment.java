package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.loader.ContentLoader;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ScreenOrientationChangeEvent;
import com.worldventures.dreamtrips.modules.common.presenter.BasePresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.infopages.model.Video;
import com.worldventures.dreamtrips.modules.infopages.presenter.MembershipPM;
import com.worldventures.dreamtrips.modules.infopages.view.cell.VideoCell;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_member_ship)
@MenuResource(R.menu.menu_membership)
public class MemberShipFragment extends BaseFragment<MembershipPM> implements BasePresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    LoaderRecycleAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
        this.recyclerView.setEmptyView(emptyView);

        this.arrayListAdapter = new LoaderRecycleAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(Video.class, VideoCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        this.arrayListAdapter.setContentLoader(getPresenter().getAdapterController());

        getPresenter().getAdapterController().getContentLoaderObserver().registerObserver(new ContentLoader.ContentLoadingObserving<List<Object>>() {
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
        getPresenter().actionEnroll();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.arrayListAdapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> {
                getPresenter().getAdapterController().reload();
            });
        }
    }

    @Override
    public void onRefresh() {
        getPresenter().getAdapterController().reload();
    }

    @Override
    protected MembershipPM createPresenter(Bundle savedInstanceState) {
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
