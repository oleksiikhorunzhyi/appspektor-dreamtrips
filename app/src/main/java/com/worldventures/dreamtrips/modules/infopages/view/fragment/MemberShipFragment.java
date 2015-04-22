package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.LoaderRecycleAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.video.model.Video;
import com.worldventures.dreamtrips.modules.video.presenter.MembershipVideosPresenter;
import com.worldventures.dreamtrips.modules.video.cell.VideoCell;
import com.worldventures.dreamtrips.modules.video.model.CachedEntity;

import butterknife.InjectView;

@Layout(R.layout.fragment_member_ship)
@MenuResource(R.menu.menu_membership)
public class MemberShipFragment extends BaseFragment<MembershipVideosPresenter> implements MembershipVideosPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.lv_items)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;

    private LoaderRecycleAdapter<Object> arrayListAdapter;

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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_enroll) {
            getPresenter().actionEnroll();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.arrayListAdapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> getPresenter().getAdapterController().reload());
        }
    }

    @Override
    public void onRefresh() {
        getPresenter().getAdapterController().reload();
    }

    @Override
    protected MembershipVideosPresenter createPresenter(Bundle savedInstanceState) {
        return new MembershipVideosPresenter(this);
    }

    private void setupLayoutManager(boolean landscape) {
        int spanCount = landscape ? 2 : 1;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        this.recyclerView.setLayoutManager(layoutManager);
    }


    public void onDeleteAction(CachedEntity videoEntity) {
        getPresenter().onDeleteAction(videoEntity);
    }

    @Override
    public void onCancelCaching(CachedEntity cacheEntity) {
        getPresenter().onCancelAction(cacheEntity);
    }

    @Override
    public void notifyItemChanged(CachedEntity videoEntity) {
        arrayListAdapter.notifyDataSetChanged();
    }

    @Override
    public void startLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void finishLoading() {
        refreshLayout.setRefreshing(false);
    }


    @Override
    public BaseArrayListAdapter getAdapter() {
        return arrayListAdapter;
    }

}
