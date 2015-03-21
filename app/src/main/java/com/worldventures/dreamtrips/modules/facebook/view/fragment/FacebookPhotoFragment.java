package com.worldventures.dreamtrips.modules.facebook.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseRecycleAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.facebook.FacebookUtils;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookPhotoFragmentPM;
import com.worldventures.dreamtrips.modules.facebook.view.activity.FBPickPhotoActivity;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookPhotoItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.dialog_facebook_select_photo)
public class FacebookPhotoFragment extends BaseFragment<FacebookPhotoFragmentPM> implements FacebookPhotoFragmentPM.View {

    public static final String BUNDLE_ALBUM_ID = "BUNDLE_ALBUM_ID";
    @InjectView(R.id.lv_items)
    RecyclerView lvItems;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    private BaseRecycleAdapter adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        adapter = new BaseRecycleAdapter();
        toolbar.setTitle(R.string.fab_select_photo);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> getPresenter().onBackAction());
        lvItems.setAdapter(adapter);
        lvItems.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> {
                    ChosenImage image = new ChosenImage();
                    FacebookPhoto item = (FacebookPhoto) adapter.getItem(position).getItem();
                    List<FacebookPhoto.ImageSource> is = item.getImageSources();
                    if (is.size() > 2) {
                        image.setFilePathOriginal(is.get(0).getSource());
                        image.setFileThumbnail(is.get(is.size() / 2 + 1).getSource());
                        image.setFileThumbnailSmall(is.get(is.size() - 1).getSource());
                    } else {
                        image.setFilePathOriginal(item.getPicture());
                        image.setFileThumbnail(item.getPicture());
                        image.setFileThumbnailSmall(item.getPicture());
                    }
                    getPresenter().onPhotoChosen(image);
                })
        );
        lvItems.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }


    @Override
    public void onResume() {
        super.onResume();
        String albumId = getArguments().getString(BUNDLE_ALBUM_ID);
        if (albumId == null) {
            throw new IllegalArgumentException("Album id can't be null");
        }
        if (adapter.getItemCount() == 0) {
            new Request(
                    Session.getActiveSession(),
                    "/{album-id}/photos".replace("{album-id}", albumId),
                    null,
                    HttpMethod.GET,
                    this::handleResponse
            ).executeAsync();
        }
    }

    @Override
    protected FacebookPhotoFragmentPM createPresenter(Bundle savedInstanceState) {
        return new FacebookPhotoFragmentPM(this);
    }

    private void handleResponse(Response response) {
        List<GraphObject> graphObjects = FacebookUtils.typedListFromResponse(response, GraphObject.class);
        List<FacebookPhoto> photos = new ArrayList<>(graphObjects.size());
        for (GraphObject graphObject : graphObjects) {
            FacebookPhoto photo = FacebookPhoto.create(graphObject);
            photos.add(photo);
        }
        adapter.addItems(FacebookPhotoItem.convert((Injector) getActivity(), photos));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void preFinishProcessing(ChosenImage image) {
        ((FBPickPhotoActivity) getActivity()).preFinishProcessing(image);
    }
}