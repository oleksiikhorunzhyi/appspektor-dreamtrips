package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.adapter.BaseRecycleAdapter;
import com.worldventures.dreamtrips.view.adapter.item.PhotoItem;
import com.worldventures.dreamtrips.view.presentation.TripImagesListFragmentPresentation;

import org.robobinding.ViewBinder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TripImagesListFragment extends BaseFragment<MainActivity> implements TripImagesListFragmentPresentation.View, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_TYPE = "BUNDLE_TYPE";
    @InjectView(R.id.lv_items)
    RecyclerView lvItems;
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;
    private BaseRecycleAdapter adapter;
    private TripImagesListFragmentPresentation pm;
    Type type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        type = (Type) getArguments().getSerializable(BUNDLE_TYPE);
        pm = new TripImagesListFragmentPresentation(this, type, getAbsActivity());
        ViewBinder viewBinder = getAbsActivity().createViewBinder();
        View view = viewBinder.inflateAndBindWithoutAttachingToRoot(R.layout.fragment_trip_list_images, pm, container);
        ButterKnife.inject(this, view);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getAbsActivity(), 2);
        lvItems.setLayoutManager(layoutManager);
        adapter = new BaseRecycleAdapter();
        lvItems.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.post(() -> {
            refreshLayout.setRefreshing(true);
            pm.loadImages();
        });
    }

    @Override
    public void setPhotos(List<Photo> photos) {
        adapter.addItems(PhotoItem.convert(getAbsActivity(), photos));
        adapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void clearAdapter() {
        adapter.clear();
    }

    @Override
    public void onRefresh() {
        pm.loadImages();
    }

    public static enum Type {
        MY_IMAGES, MEMBER_IMAGES, YOU_SHOULD_BE_HERE
    }
}
