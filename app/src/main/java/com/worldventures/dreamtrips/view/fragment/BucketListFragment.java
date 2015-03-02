package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.BucketItem;
import com.worldventures.dreamtrips.presentation.BasePresentation;
import com.worldventures.dreamtrips.presentation.BucketListFragmentPM;
import com.worldventures.dreamtrips.utils.AdobeTrackingHelper;
import com.worldventures.dreamtrips.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.view.custom.DragSortRecycler;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.view.custom.SwipeDismissRecyclerViewTouchListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@Layout(R.layout.fragment_bucket_list)
@MenuResource(R.menu.menu_bucket)
public class BucketListFragment extends BaseFragment<BucketListFragmentPM> implements BasePresentation.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";

    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @InjectView(R.id.textViewEmptyAdd)
    TextView textViewEmptyAdd;

    @Inject
    @Global
    EventBus eventBus;

    BaseArrayListAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BUNDLE_TYPE);

        AdobeTrackingHelper.bucketList();
        eventBus.register(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setEmptyView(emptyView);

        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.tv_name);

        this.recyclerView.addItemDecoration(dragSortRecycler);
        this.recyclerView.addOnItemTouchListener(dragSortRecycler);
        this.recyclerView.setOnScrollListener(dragSortRecycler.getScrollListener());

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(BucketItem.class, BucketItemCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        this.textViewEmptyAdd.setText(getString(R.string.bucket_list_add).replace("[bucket_category]", getString(type.res)));

        this.arrayListAdapter.setContentLoader(getPresentationModel().getAdapterController());

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                View menuItemView = getActivity().findViewById(R.id.action_filter); // SAME ID AS MENU ID
                PopupMenu popupMenu = new PopupMenu(getActivity(), menuItemView);
                popupMenu.inflate(R.menu.menu_bucket_filter);
                popupMenu.show();
                break;
        }
        return super.onOptionsItemSelected(item);
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
    protected BucketListFragmentPM createPresentationModel(Bundle savedInstanceState) {
        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BUNDLE_TYPE);
        return new BucketListFragmentPM(this, type);
    }

    public void requestReload() {
        getPresentationModel().getAdapterController().reload();
    }

}
