package com.worldventures.dreamtrips.modules.facebook.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.widget.LoginButton;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.RecyclerItemClickListener;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.facebook.FacebookUtils;
import com.worldventures.dreamtrips.modules.facebook.model.FacebookAlbum;
import com.worldventures.dreamtrips.modules.facebook.presenter.FacebookAlbumPresenter;
import com.worldventures.dreamtrips.modules.facebook.view.cell.FacebookAlbumCell;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.dialog_facebook_select_album)
public class FacebookAlbumFragment extends BaseFragment<FacebookAlbumPresenter> implements FacebookAlbumPresenter.View {

    @InjectView(R.id.lv_items)
    protected RecyclerView lvItems;
    @InjectView(R.id.login_button)
    protected LoginButton loginButton;
    @InjectView(R.id.toolbar_actionbar)
    protected Toolbar toolbar;

    @Inject
    @ForActivity
    Provider<Injector> injector;

    int previousTotal;
    boolean loading;

    BaseArrayListAdapter<FacebookAlbum> adapter;
    GridLayoutManager layoutManager;

    private Session.StatusCallback callback = (session, state, exception) -> {
        if (session != null && session.isOpened()) {
            //nothing to do here
        }
    };

    private boolean tryToOpenSession = false;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new BaseArrayListAdapter(getActivity(), this);
        adapter.registerCell(FacebookAlbum.class, FacebookAlbumCell.class);
        toolbar.setTitle(getString(R.string.fab_select_album));
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(v -> fragmentCompass.pop());
        toolbar.getBackground().setAlpha(255);
        lvItems.setAdapter(adapter);

        lvItems.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> {
                    String facebookId = (adapter.getItem(position)).getId();
                    Bundle b = new Bundle();
                    b.putString(FacebookPhotoFragment.BUNDLE_ALBUM_ID, facebookId);
                    fragmentCompass.add(Route.PICK_FB_PHOTO, b);
                })

        );
        layoutManager = new GridLayoutManager(getActivity(), 2);

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

    public void scrolled(int totalItemCount, int lastVisible) {
        if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
        }
        if (!loading && lastVisible == totalItemCount - 1) {
            getPresenter().requestAlbums(true);
            loading = true;
        }
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
            fragmentCompass.pop();
        }
    }

    @Override
    public void onDestroyView() {
        this.lvItems.setAdapter(null);
        super.onDestroyView();
    }


    @Override
    protected FacebookAlbumPresenter createPresenter(Bundle savedInstanceState) {
        return new FacebookAlbumPresenter();
    }

    private void loadData() {
        if (adapter.getItemCount() == 0) {
            getPresenter().requestAlbums(false);
        }
    }

    @Override
    public void handleResponse(Response response) {
        if (response != null && response.getError() == null) {
            List<GraphObject> graphObjects = FacebookUtils.typedListFromResponse(response, GraphObject.class);
            List<FacebookAlbum> albums = new ArrayList<>(graphObjects.size());
            for (GraphObject graphObject : graphObjects) {
                FacebookAlbum album = FacebookAlbum.create(graphObject);
                if (album.getCount() != null && album.getCount() > 0) {
                    albums.add(album);
                }
            }
            adapter.addItems(albums);
            adapter.notifyDataSetChanged();
        }
    }
}
