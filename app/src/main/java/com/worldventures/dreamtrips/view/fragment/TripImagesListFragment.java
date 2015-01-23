package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Photo;
import com.worldventures.dreamtrips.presentation.TripImagesListFragmentPresentation;
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
public class TripImagesListFragment extends BaseFragment<TripImagesListFragmentPresentation> implements TripImagesListFragmentPresentation.View, SwipeRefreshLayout.OnRefreshListener {

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

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        eventBus.register(this);

        setupLayoutManager(ViewUtils.isLandscapeOrientation(getActivity()));
        this.recyclerView.setEmptyView(emptyView);

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(Photo.class, PhotoCell.class);
        this.arrayListAdapter.registerCell(ImageUploadTaskRealmProxy.class, PhotoUploadCell.class);

        this.recyclerView.setAdapter(this.arrayListAdapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        this.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> this.getPresentationModel().onItemClick(position))
        );

        this.arrayListAdapter.setContentLoader(getPresentationModel().getPhotosController());

        getPresentationModel().getPhotosController().getContentLoaderObserver().registerObserver(new ContentLoader.ContentLoadingObserving<List<Object>>() {
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
    public void onResume() {
        super.onResume();

        if (this.arrayListAdapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> {
                getPresentationModel().getPhotosController().reload();
            });
        }
    }


    public void onEvent(ScreenOrientationChangeEvent event) {
        boolean landscape = event.isLandscape();
        setupLayoutManager(landscape);
    }

    private void setupLayoutManager(boolean landscape) {
        int spanCount = landscape ? 4 : ViewUtils.isTablet(getActivity()) ? 3 : 2;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), spanCount);
        this.recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onRefresh() {
        getPresentationModel().getPhotosController().reload();
    }

    @Override
    protected TripImagesListFragmentPresentation createPresentationModel(Bundle savedInstanceState) {
        Type type = (Type) getArguments().getSerializable(BUNDLE_TYPE);
        return new TripImagesListFragmentPresentation(this, type);
    }

    @Override
    public void requestUpdateAdapter() {
        arrayListAdapter.notifyItemChanged(0);
    }

    public static enum Type {
        MY_IMAGES, MEMBER_IMAGES, YOU_SHOULD_BE_HERE, INSPIRE_ME
    }
}
