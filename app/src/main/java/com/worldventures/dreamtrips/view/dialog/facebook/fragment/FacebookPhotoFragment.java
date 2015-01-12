package com.worldventures.dreamtrips.view.dialog.facebook.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.adapter.BaseRecycleAdapter;
import com.worldventures.dreamtrips.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.view.dialog.facebook.FacebookUtils;
import com.worldventures.dreamtrips.view.dialog.facebook.model.FacebookPhoto;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookPhotoItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FacebookPhotoFragment extends BaseFacebookDialogFragment {

    @InjectView(R.id.lv_items)
    RecyclerView lvItems;
    private BaseRecycleAdapter adapter;
    private String albumId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_facebook_select_photo, container, false);
        ButterKnife.inject(this, view);
        adapter = new BaseRecycleAdapter();
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
                    imagePickCallback.onResult(image, null);
                    dismissAllowingStateLoss();
                })
        );
        lvItems.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Request(
                Session.getActiveSession(),
                "/{album-id}/photos".replace("{album-id}", albumId),
                null,
                HttpMethod.GET,
                this::handleResponse
        ).executeAsync();
    }

    private void handleResponse(Response response) {
        List<GraphObject> graphObjects = FacebookUtils.typedListFromResponse(response, GraphObject.class);
        List<FacebookPhoto> photos = new ArrayList<>(graphObjects.size());
        for (GraphObject graphObject : graphObjects) {
            FacebookPhoto photo = FacebookPhoto.create(graphObject);
            photos.add(photo);
        }
        adapter.addItems(FacebookPhotoItem.convert(injector, photos));
        adapter.notifyDataSetChanged();
    }


    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
