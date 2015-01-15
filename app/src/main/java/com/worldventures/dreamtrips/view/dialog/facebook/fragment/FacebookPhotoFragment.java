package com.worldventures.dreamtrips.view.dialog.facebook.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

    public static final String FB_PHOTO_TAG = "FB_PHOTO_TAG";
    @InjectView(R.id.lv_items)
    RecyclerView lvItems;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    private BaseRecycleAdapter adapter;
    private String albumId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_facebook_select_photo, container, false);
        ButterKnife.inject(this, view);
        adapter = new BaseRecycleAdapter();
        toolbar.setTitle("Select Photo");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> dismissAllowingStateLoss());
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
                    hideDialog();
                })
        );
        lvItems.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return view;
    }

    private void hideDialog() {
        new Handler().postDelayed(() -> {
            dismissAllowingStateLoss();
            Fragment albumDialog = fm.findFragmentByTag(FacebookAlbumFragment.FB_ALBUM_TAG);
            if (albumDialog != null && albumDialog instanceof DialogFragment) {
                ((DialogFragment) albumDialog).dismissAllowingStateLoss();
            }
        }, 200);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    @Override
    public String getDialogTag() {
        return FB_PHOTO_TAG;
    }
}
