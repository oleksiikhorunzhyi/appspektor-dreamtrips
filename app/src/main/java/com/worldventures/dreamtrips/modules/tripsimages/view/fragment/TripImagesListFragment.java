package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoUploadCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_trip_list_images)
public class TripImagesListFragment extends BaseFragment<TripImagesListPresenter> implements TripImagesListPresenter.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";

    @Inject
    @ForActivity
    Provider<Injector> injector;

    @InjectView(R.id.lv_items)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private BaseArrayListAdapter<IFullScreenAvailableObject> arrayListAdapter;
    private LinearLayoutManager layoutManager;

    private int lastScrollPosition;

    RecyclerViewStateDelegate stateDelegate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        this.layoutManager = getLayoutManager();
        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setEmptyView(emptyView);
        stateDelegate.setRecyclerView(recyclerView);

        this.arrayListAdapter = new BaseArrayListAdapter<>(rootView.getContext(), injector);
        this.arrayListAdapter.registerCell(Photo.class, PhotoCell.class);
        this.arrayListAdapter.registerCell(Inspiration.class, PhotoCell.class);
        this.arrayListAdapter.registerCell(ImageUploadTask.class, PhotoUploadCell.class);
        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        this.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> this.getPresenter().onItemClick(position))
        );
        this.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int childCount = recyclerView.getChildCount();
                int itemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                getPresenter().scrolled(childCount, itemCount, firstVisibleItemPosition);
            }
        });
    }

    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void setSelection() {
        //nothing to do here
    }

    private GridLayoutManager getLayoutManager() {
        boolean landscape = ViewUtils.isLandscapeOrientation(getActivity());
        int spanCount = landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
        return new GridLayoutManager(getActivity(), spanCount);
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    protected TripImagesListPresenter createPresenter(Bundle savedInstanceState) {
        Type type = (Type) getArguments().getSerializable(BUNDLE_TYPE);
        return TripImagesListPresenter.create(type, this);
    }

    @Override
    public List<IFullScreenAvailableObject> getPhotosFromAdapter() {
        return arrayListAdapter.getItems();
    }

    @Override
    public void startLoading() {
        refreshLayout.post(() -> refreshLayout.setRefreshing(true));
    }

    @Override
    public void finishLoading() {
        refreshLayout.setRefreshing(false);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public IRoboSpiceAdapter getAdapter() {
        return arrayListAdapter;
    }

    @Override
    public void addAll(List<IFullScreenAvailableObject> items) {
        int itemCount = arrayListAdapter.getItemCount();
        arrayListAdapter.addItems(items);
        arrayListAdapter.notifyItemRangeInserted(itemCount - 1, items.size());
    }

    @Override
    public void add(IFullScreenAvailableObject item) {
        arrayListAdapter.addItem(item);
        arrayListAdapter.notifyItemInserted(arrayListAdapter.getItemCount() - 1);
    }

    @Override
    public void add(int position, IFullScreenAvailableObject item) {
        arrayListAdapter.addItem(position, item);
        arrayListAdapter.notifyItemInserted(position);
    }

    @Override
    public void clear() {
        arrayListAdapter.clear();
    }

    @Override
    public void replace(int position, IFullScreenAvailableObject item) {
        arrayListAdapter.replaceItem(position, item);
        arrayListAdapter.notifyItemChanged(position);
    }

    @Override
    public void remove(int index) {
        arrayListAdapter.remove(index);
        arrayListAdapter.notifyItemRemoved(index);
    }

    public enum Type {
        MEMBER_IMAGES, MY_IMAGES, YOU_SHOULD_BE_HERE, INSPIRE_ME, VIDEO_360, BUCKET_PHOTOS
    }
}
