package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;

import java.util.List;

import butterknife.ButterKnife;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.menu_empty)
public class UserFragment extends ProfileFragment<UserPresenter>
        implements UserPresenter.View {

    private DrawableUtil drawableUtil;

    @Override
    protected UserPresenter createPresenter(Bundle savedInstanceState) {
        return new UserPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        profileToolbarTitle.setVisibility(View.INVISIBLE);
        profileToolbarUserStatus.setVisibility(View.INVISIBLE);
        drawableUtil = new DrawableUtil(getContext());
    }

    public void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectedAction) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(getString(R.string.friend_add_to))
                .adapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, circles),
                        (materialDialog, view, i, charSequence) -> {
                            selectedAction.apply(i);
                            materialDialog.dismiss();
                        })
                .negativeText(R.string.cancel)
                .show();
    }

    @Override
    public void showFriendDialog(User user) {
        ImageView userPhoto = ButterKnife.findById(feedView, R.id.user_photo);
        if (userPhoto != null) {
            userPhoto.setDrawingCacheEnabled(true);
            new FriendActionDialogDelegate(getActivity(), getEventBus()).showFriendDialog(user,
                    drawableUtil.copyIntoDrawable(userPhoto.getDrawingCache()));
        }
    }

    @Override
    public void openFriendPrefs(UserBundle userBundle) {
        router.moveTo(Route.FRIEND_PREFERENCES, NavigationConfigBuilder.forActivity()
                .data(userBundle)
                .build());
    }

    @Override
    protected void initialToolbar() {
        profileToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        profileToolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }
}
