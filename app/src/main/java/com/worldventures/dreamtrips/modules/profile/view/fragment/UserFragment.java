package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.menu_empty)
public class UserFragment extends ProfileFragment<UserPresenter>
        implements UserPresenter.View {

    private MenuItem chatActionItem;
    @Inject
    protected DrawableUtil drawableUtil;

    @Override
    protected UserPresenter createPresenter(Bundle savedInstanceState) {
        return new UserPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        profileToolbarTitle.setVisibility(View.INVISIBLE);
        profileToolbarUserStatus.setVisibility(View.INVISIBLE);

        profileToolbar.inflateMenu(R.menu.user_profile_fragment);
        chatActionItem = profileToolbar.getMenu().findItem(R.id.action_chat);
        chatActionItem.setOnMenuItemClickListener(item -> {
            getPresenter().onStartChatClicked();
            return true;
        });
        showChatButtonForFriend(getPresenter().getUser());
    }

    @Override
    public void setUser(User user) {
        super.setUser(user);
        showChatButtonForFriend(user);
    }

    @Override
    public void notifyUserChanged() {
        super.notifyUserChanged();
        showChatButtonForFriend(getPresenter().getUser());
    }

    public void showChatButtonForFriend(User user) {
        chatActionItem.setVisible(user.getRelationship() == User.Relationship.FRIEND);
    }

    public void showAddFriendDialog(List<Circle> circles, Action1<Integer> selectedAction) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(getString(R.string.friend_add_to))
                .adapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, circles),
                        (materialDialog, view, i, charSequence) -> {
                            selectedAction.apply(i);
                            materialDialog.dismiss();
                        })
                .negativeText(R.string.action_cancel)
                .show();
    }

    @Override
    public void showFriendDialog(User user) {
        ImageView userPhoto = ButterKnife.findById(statePaginatedRecyclerViewManager.stateRecyclerView, R.id.user_photo);
        if (userPhoto != null) {
            userPhoto.setDrawingCacheEnabled(true);
            new FriendActionDialogDelegate(getActivity(), getEventBus())
                    .showFriendDialogSkipChat
                            (user, drawableUtil.copyIntoDrawable(userPhoto.getDrawingCache()));
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
        profileToolbar.setNavigationIcon(R.drawable.back_icon);
        profileToolbar.setNavigationOnClickListener(view -> getActivity().onBackPressed());
    }

    @Override
    protected BaseDelegateAdapter createAdapter() {
        return new IgnoreFirstItemAdapter(getContext(), this);
    }

    @Override
    public void flagSentSuccess() {
        informUser(R.string.flag_sent_success_msg);
    }
}