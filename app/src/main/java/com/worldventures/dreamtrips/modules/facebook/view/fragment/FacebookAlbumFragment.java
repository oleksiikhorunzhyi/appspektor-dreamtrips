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
import com.facebook.widget.LoginButton;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseRecycleAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.facebook.FacebookUtils;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookAlbum;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumPresenter;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookAlbumItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.dialog_facebook_select_album)
public class FacebookAlbumFragment extends BaseFragment<FacebookAlbumPresenter> {

    @InjectView(R.id.lv_items)
    protected RecyclerView lvItems;

    @InjectView(R.id.login_button)
    protected LoginButton loginButton;

    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;
    private BaseRecycleAdapter adapter;

    private Session.StatusCallback callback = (session, state, exception) -> {
        if (session != null && session.isOpened()) {
            //nothing to do here
        }
    };

    private boolean tryToOpenSession = false;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new BaseRecycleAdapter();

        toolbar.setTitle(getString(R.string.fab_select_album));
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> getPresenter().backAction());

        lvItems.setAdapter(adapter);
        lvItems.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> {
                    String facebookId = ((FacebookAlbum) adapter.getItem(position).getItem()).getId();
                    getPresenter().onItemClick(facebookId);
                })

        );
        lvItems.setLayoutManager(new GridLayoutManager(getActivity(), 2));

    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            loadData();
        } else if (!tryToOpenSession) {
            loginButton.setReadPermissions("user_photos");
            loginButton.setSessionStatusCallback(callback);
            loginButton.performClick();
            tryToOpenSession = true;
        } else {
            getPresenter().backAction();
        }
    }

    @Override
    protected FacebookAlbumPresenter createPresenter(Bundle savedInstanceState) {
        return new FacebookAlbumPresenter(this);
    }

    private void loadData() {
        if (adapter.getItemCount() == 0) {
            new Request(
                    Session.getActiveSession(),
                    "/me/albums",
                    null,
                    HttpMethod.GET,
                    this::handleResponse
            ).executeAsync();
        }
    }

    private void handleResponse(Response response) {
        if (response != null && response.getError() == null) {
            List<GraphObject> graphObjects = FacebookUtils.typedListFromResponse(response, GraphObject.class);
            List<FacebookAlbum> albums = new ArrayList<>(graphObjects.size());
            for (GraphObject graphObject : graphObjects) {
                FacebookAlbum album = FacebookAlbum.create(graphObject);
                if (album.getCount() != null && album.getCount() > 0) {
                    albums.add(album);
                }
            }
            adapter.addItems(FacebookAlbumItem.convert(((Injector) getActivity()), albums));
            adapter.notifyDataSetChanged();
        } else {
            //TODO handle error
        }
    }
}
