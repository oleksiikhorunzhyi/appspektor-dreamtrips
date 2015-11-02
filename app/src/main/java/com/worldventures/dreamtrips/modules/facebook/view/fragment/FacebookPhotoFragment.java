package com.worldventures.dreamtrips.modules.facebook.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.Response;
import com.facebook.model.GraphObject;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.facebook.FacebookUtils;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoPresenter;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FacebookPickPhotoActivity;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookPhotoCell;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.dialog_facebook_select_photo)
public class FacebookPhotoFragment extends BaseFragment<FacebookPhotoPresenter> implements FacebookPhotoPresenter.View {

    public static final String BUNDLE_ALBUM_ID = "BUNDLE_ALBUM_ID";

    @InjectView(R.id.lv_items)
    protected RecyclerView lvItems;
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    BaseArrayListAdapter adapter;
    GridLayoutManager layoutManager;

    @Inject
    @ForActivity
    Provider<Injector> injector;

    int previousTotal;
    boolean loading;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        adapter = new BaseArrayListAdapter(getActivity(), injector);
        adapter.registerCell(FacebookPhoto.class, FacebookPhotoCell.class);

        toolbar.setTitle(R.string.fab_select_photo);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> fragmentCompass.pop());
        toolbar.getBackground().setAlpha(255);
        lvItems.setAdapter(adapter);
        lvItems.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> {
                    ChosenImage image = new ChosenImage();
                    FacebookPhoto item = (FacebookPhoto) adapter.getItem(position);
                    List<FacebookPhoto.ImageSource> is = item.getImageSources();
                    if (is.size() > 2) {
                        image.setFilePathOriginal(is.get(0).getSource());
                        image.setFileThumbnail(is.get(is.size() / 2 + 1).getSource());
                        image.setFileThumbnailSmall(is.get(is.size() - 1).getSource());
                    } else {
                        image.setFilePathOriginal(item.getSource());
                        image.setFileThumbnail(item.getSource());
                        image.setFileThumbnailSmall(item.getPicture());
                    }
                    getPresenter().onPhotoChosen(image);
                })
        );
        layoutManager = new GridLayoutManager(getActivity(), 3);

        lvItems.setLayoutManager(layoutManager);
        lvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int itemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                scrolled(itemCount, lastVisibleItemPosition);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.getItemCount() == 0) {
            getPresenter().requestPhotos(false);
        }
    }


    @Override
    public void onDestroyView() {
        this.lvItems.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    protected FacebookPhotoPresenter createPresenter(Bundle savedInstanceState) {
        String albumId = getArguments().getString(BUNDLE_ALBUM_ID);
        return new FacebookPhotoPresenter(albumId);
    }

    @Override
    public void handleResponse(Response response) {
        List<GraphObject> graphObjects = FacebookUtils.typedListFromResponse(response, GraphObject.class);
        List<FacebookPhoto> photos = new ArrayList<>(graphObjects.size());
        for (GraphObject graphObject : graphObjects) {
            FacebookPhoto photo = FacebookPhoto.create(graphObject);
            photos.add(photo);
        }
        adapter.addItems(photos);
        adapter.notifyDataSetChanged();
    }

    public void scrolled(int totalItemCount, int lastVisible) {
        if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }
        if (!loading && lastVisible == totalItemCount - 1) {
            getPresenter().requestPhotos(true);
            loading = true;
        }
    }

    @Override
    public void preFinishProcessing(ChosenImage image) {
        ((FacebookPickPhotoActivity) getActivity()).preFinishProcessing(image);
    }
}