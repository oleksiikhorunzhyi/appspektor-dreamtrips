package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListFragmentPresentation;
import com.worldventures.dreamtrips.view.adapter.BaseRecycleAdapter;
import com.worldventures.dreamtrips.view.adapter.item.PhotoItem;
import com.worldventures.dreamtrips.view.custom.RecyclerItemClickListener;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_trip_list_images)
public class TripImagesListFragment extends BaseFragment<TripImagesListFragmentPresentation> implements TripImagesListFragmentPresentation.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";

    @InjectView(R.id.lv_items)
    RecyclerView lvItems;
    @InjectView(R.id.ll_empty_view)
    ViewGroup llEmptyView;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    private BaseRecycleAdapter adapter;


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        lvItems.setLayoutManager(layoutManager);
        adapter = new BaseRecycleAdapter();
        lvItems.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        lvItems.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> this.getPresentationModel().onItemClick(position))
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.getItemCount() == 0) {
            refreshLayout.post(() -> {
                refreshLayout.setRefreshing(true);
                this.getPresentationModel().loadImages();
            });
        }
    }

    @Override
    public void setPhotos(List<Photo> photos) {
        adapter.addItems(PhotoItem.convert(this, photos));
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
        if (photos == null || photos.isEmpty()) {
            llEmptyView.setVisibility(View.VISIBLE);
        } else {
            llEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void clearAdapter() {
        adapter.clear();
    }

    @Override
    public void onRefresh() {
        this.getPresentationModel().loadImages();
    }

    @Override
    protected TripImagesListFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        Type type = (Type) getArguments().getSerializable(BUNDLE_TYPE);
        return new TripImagesListFragmentPresentation(this, type);
    }

    public static enum Type {
        MY_IMAGES, MEMBER_IMAGES, YOU_SHOULD_BE_HERE
    }
}
