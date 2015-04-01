package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.tripsimages.model.IFullScreenAvailableObject;
import com.worldventures.dreamtrips.modules.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesListPM;
import com.worldventures.dreamtrips.modules.tripsimages.uploader.ImageUploadTask;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoCell;
import com.worldventures.dreamtrips.modules.tripsimages.view.cell.PhotoUploadCell;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_trip_list_images)
public class TripImagesListFragment extends BaseFragment<TripImagesListPM> implements TripImagesListPM.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";

    @InjectView(R.id.lv_items)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    protected ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;

    private BaseArrayListAdapter<IFullScreenAvailableObject> arrayListAdapter;
    private Type type;
    private LinearLayoutManager layoutManager;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
        this.recyclerView.setEmptyView(emptyView);

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
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
    public void onResume() {
        super.onResume();

        if (this.arrayListAdapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> {
                getPresenter().reload();
            });
        }
    }

    private void setupLayoutManager(boolean landscape) {
        int spanCount = landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
        layoutManager = new GridLayoutManager(getActivity(), spanCount);
        this.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onRefresh() {
        getPresenter().reload();
    }

    @Override
    protected TripImagesListPM createPresenter(Bundle savedInstanceState) {
        type = (Type) getArguments().getSerializable(BUNDLE_TYPE);
        return TripImagesListPM.create(type, this);
    }

    @Override
    public List<IFullScreenAvailableObject> getPhotosFromAdapter() {
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
    public void setSelection() {

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

    public static enum Type {
        MEMBER_IMAGES, MY_IMAGES, YOU_SHOULD_BE_HERE, INSPIRE_ME, VIDEO_360
    }
}
