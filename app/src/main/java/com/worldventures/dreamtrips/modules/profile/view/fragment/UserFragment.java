package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;

import java.util.List;

@Layout(R.layout.fragment_profile)
@MenuResource(R.menu.menu_empty)
public class UserFragment extends ProfileFragment<UserPresenter>
        implements UserPresenter.View {

    @Override
    protected UserPresenter createPresenter(Bundle savedInstanceState) {
        return new UserPresenter(getArguments());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        profileView.getControlPanel().setVisibility(View.GONE);
        profileView.getCover().setVisibility(View.GONE);
        profileView.getAvatar().setVisibility(View.GONE);
        profileView.getUpdateInfo().setVisibility(View.GONE);
        profileView.getUserBalance().setVisibility(View.GONE);
        profileView.getAddFriend().setVisibility(View.VISIBLE);

        profileView.getBuckets().setClickable(false);
        profileView.getTripImages().setClickable(false);

        profileView.getBuckets().setText(R.string.coming_soon);
        profileView.getTripImages().setText(R.string.coming_soon);

        profileView.findViewById(R.id.wrapper_enroll).setVisibility(View.GONE);
        profileView.findViewById(R.id.wrapper_from).setVisibility(View.GONE);
        profileView.findViewById(R.id.wrapper_date_of_birth).setVisibility(View.GONE);
        profileView.getMore().setVisibility(View.INVISIBLE);

        profileView.setIsExpandEnabled(false);
        profileView.getInfo().show();

        profileView.setOnAcceptRequest(() -> getPresenter().acceptClicked());
        profileView.setOnRejectRequest(() -> getPresenter().rejectClicked());
        profileView.setOnAddFriend(() -> getPresenter().addFriendClicked());

    }

    @Override
    public void setIsFriend(boolean isFriend) {
        profileView.getAddFriend().setText(isFriend ? R.string.profile_friends : R.string.profile_add_friend);
        profileView.getAddFriend().setCompoundDrawablesWithIntrinsicBounds(0,
                isFriend ? R.drawable.friend_added
                        : R.drawable.add_friend,
                0, 0);
    }

    @Override
    public void setWaiting() {
        profileView.getAddFriend().setText(R.string.profile_waiting);
        profileView.getAddFriend().setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.respond,
                0, 0);
    }

    @Override
    public void setRespond() {
        profileView.getAddFriend().setText(R.string.profile_respond);
        profileView.getAddFriend().setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.respond,
                0, 0);
    }

    @Override
    public void showFriendRequest(String name) {
        profileView.getFriendRequestCaption().setText(String.format(getString(R.string.profile_friend_request), name));
        profileView.getFriendRequest().setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFriendRequest() {
        profileView.getFriendRequest().setVisibility(View.GONE);
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
}
