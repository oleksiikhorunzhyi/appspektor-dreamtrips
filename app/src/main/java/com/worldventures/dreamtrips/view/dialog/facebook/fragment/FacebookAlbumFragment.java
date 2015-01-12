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
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.view.adapter.BaseRecycleAdapter;
import com.worldventures.dreamtrips.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.view.dialog.facebook.FacebookUtils;
import com.worldventures.dreamtrips.view.dialog.facebook.model.FacebookAlbum;
import com.worldventures.dreamtrips.view.dialog.facebook.view.FacebookAlbumItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class FacebookAlbumFragment extends BaseFacebookDialogFragment {
    @InjectView(R.id.lv_items)
    RecyclerView lvItems;
    private BaseRecycleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_facebook_select_album, container, false);
        ButterKnife.inject(this, view);
        adapter = new BaseRecycleAdapter();
        lvItems.setAdapter(adapter);
        lvItems.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> {
                    String id = ((FacebookAlbum) adapter.getItem(position).getItem()).getId();
                    FacebookPhotoFragment fbp = new FacebookPhotoFragment();
                    fbp.setAlbumId(id);
                    fbp.show(getFragmentManager(), injector, imagePickCallback);
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
                "/me/albums",
                null,
                HttpMethod.GET,
                this::handleResponse
        ).executeAsync();
    }

    private void handleResponse(Response response) {
        List<GraphObject> graphObjects = FacebookUtils.typedListFromResponse(response, GraphObject.class);
        List<FacebookAlbum> albums = new ArrayList<>(graphObjects.size());
        for (GraphObject graphObject : graphObjects) {
            FacebookAlbum album = FacebookAlbum.create(graphObject);
            if (album.getCount() != null && album.getCount() > 0) {
                albums.add(album);
            }
        }
        adapter.addItems(FacebookAlbumItem.convert(injector, albums));
        adapter.notifyDataSetChanged();
    }
}
