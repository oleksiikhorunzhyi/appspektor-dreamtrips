package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.UserImagesPresenter;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_account_images_list)
public class UsersImagesListFragment<P extends UserImagesPresenter> extends TripImagesListFragment<P> implements FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    @InjectView(R.id.v_bg_holder)
    protected View vBgHolder;
    @InjectView(R.id.multiple_actions_down)
    protected FloatingActionsMenu multipleActionsDown;
    @InjectView(R.id.fab_facebook)
    protected FloatingActionButton fabFacebook;
    @InjectView(R.id.fab_gallery)
    protected FloatingActionButton fabGallery;
    @InjectView(R.id.fab_photo)
    protected FloatingActionButton fabPhoto;


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        this.multipleActionsDown.setOnFloatingActionsMenuUpdateListener(this);
    }

    @Override
    public void onMenuExpanded() {
        this.vBgHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMenuCollapsed() {
        this.vBgHolder.setVisibility(View.GONE);
    }

    @OnClick(R.id.fab_facebook)
    public void actionFacebook() {
        openPicker(PickImageDelegate.REQUEST_FACEBOOK);
    }

    @OnClick(R.id.fab_gallery)
    public void actionGallery() {
        openPicker(PickImageDelegate.REQUEST_PICK_PICTURE);
    }

    @OnClick(R.id.fab_photo)
    public void actionPhoto() {
        openPicker(PickImageDelegate.REQUEST_CAPTURE_PICTURE);
    }


    private void openPicker(int requestType) {
        getPresenter().pickImage(requestType);
        this.multipleActionsDown.collapse();

        if (this instanceof AccountImagesListFragment){
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MY_IMAGES);
        } else {
            TrackingHelper.uploadTripImagePhoto(TrackingHelper.ACTION_MEMBER_IMAGES);
        }
    }
}
