package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.core.uploader.model.ImageUploadTask;
import com.worldventures.dreamtrips.presentation.TripImagesListPM;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.utils.busevents.ScreenOrientationChangeEvent;
import com.worldventures.dreamtrips.view.cell.PhotoCell;
import com.worldventures.dreamtrips.view.cell.PhotoUploadCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.view.custom.RecyclerItemClickListener;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import io.realm.ImageUploadTaskRealmProxy;

@Layout(R.layout.fragment_trip_list_images)
public class TripImagesListFragment extends BaseFragment<TripImagesListPM> implements TripImagesListPM.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";

    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @Inject
    @Global
    EventBus eventBus;

    BaseArrayListAdapter<Object> arrayListAdapter;
    private Type type;
    private LinearLayoutManager layoutManager;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        eventBus.register(this);

        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
        this.recyclerView.setEmptyView(emptyView);

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(Photo.class, PhotoCell.class);
        this.arrayListAdapter.registerCell(Inspiration.class, PhotoCell.class);
        this.arrayListAdapter.registerCell(ImageUploadTaskRealmProxy.class, PhotoUploadCell.class);
        this.arrayListAdapter.registerCell(ImageUploadTask.class, PhotoUploadCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        this.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> this.getPresentationModel().onItemClick(position))
        );
        this.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int childCount = recyclerView.getChildCount();
                int itemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                getPresentationModel().scrolled(childCount, itemCount, firstVisibleItemPosition);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.arrayListAdapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> {
                getPresentationModel().reload();
            });
        }
    }


    public void onEvent(ScreenOrientationChangeEvent event) {
        boolean landscape = event.isLandscape();
        setupLayoutManager(landscape);
    }

    private void setupLayoutManager(boolean landscape) {
        int spanCount = landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
        layoutManager = new GridLayoutManager(getActivity(), spanCount);
        this.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onRefresh() {
        getPresentationModel().reload();
    }

    @Override
    protected TripImagesListPM createPresentationModel(Bundle savedInstanceState) {
        type = (Type) getArguments().getSerializable(BUNDLE_TYPE);
        return TripImagesListPM.create(type, this);
    }

    @Override
    public List<Object> getPhotosFromAdapter() {
        return arrayListAdapter.getItems();
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
    public void addAll(List<Object> items) {
        arrayListAdapter.addItems(items);
        arrayListAdapter.notifyDataSetChanged();
    }

    @Override
    public void add(Object item) {
        arrayListAdapter.addItem(item);
        arrayListAdapter.notifyItemInserted(arrayListAdapter.getItemCount() - 1);
    }

    @Override
    public void add(int position, Object item) {
        arrayListAdapter.addItem(position, item);
        arrayListAdapter.notifyItemInserted(position);
    }

    @Override
    public void clear() {
        arrayListAdapter.clear();
    }

    @Override
    public void replace(int position, Object item) {
        arrayListAdapter.replaceItem(position, item);
        arrayListAdapter.notifyItemChanged(position);
    }

    @Override
    public void remove(int index) {
        arrayListAdapter.remove(index);
        arrayListAdapter.notifyItemRemoved(index);
    }

    public static enum Type {
        MEMBER_IMAGES, MY_IMAGES, YOU_SHOULD_BE_HERE, INSPIRE_ME
    }
}
