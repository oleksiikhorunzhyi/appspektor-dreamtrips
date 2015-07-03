package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_profile)
public class UserFragment extends ProfileFragment<UserPresenter>
        implements UserPresenter.View {

    @InjectView(R.id.friend_request_caption)
    protected TextView friendRequestCaption;
    @InjectView(R.id.friend_request)
    protected ViewGroup friendRequest;
    @InjectView(R.id.accept)
    protected AppCompatButton accept;
    @InjectView(R.id.reject)
    protected AppCompatButton reject;
    @InjectView(R.id.control_panel)
    protected ViewGroup controlPanel;

    @Override
    protected UserPresenter createPresenter(Bundle savedInstanceState) {
        return new UserPresenter(getArguments());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        ColorStateList csl = getResources().getColorStateList(R.color.button_background);
        reject.setSupportBackgroundTintList(csl);

        controlPanel.setVisibility(View.GONE);
        cover.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
        updateInfo.setVisibility(View.GONE);
        userBalance.setVisibility(View.GONE);
        addFriend.setVisibility(View.VISIBLE);
    }

    @Override
    public void setIsFriend(boolean isFriend) {
        addFriend.setText(isFriend ? R.string.profile_friends : R.string.profile_add_friend);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                isFriend ? R.drawable.friend_added
                        : R.drawable.add_friend,
                0, 0);
    }

    @Override
    public void setWaiting() {
        addFriend.setText(R.string.profile_waiting);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.respond,
                0, 0);
    }

    @Override
    public void setRespond() {
        addFriend.setText(R.string.profile_respond);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.respond,
                0, 0);
    }

    @OnClick(R.id.accept)
    void onAcceptRequest() {
        getPresenter().acceptClicked();
    }

    @OnClick(R.id.reject)
    void onRejectRequest() {
        getPresenter().rejectClicked();
    }

    @OnClick(R.id.add_friend)
    void onAddFriend() {
        getPresenter().addFriendClicked();
    }

    @Override
    public void showFriendRequest(String name) {
        friendRequestCaption.setText(String.format(getString(R.string.profile_friend_request), name));
        friendRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFriendRequest() {
        friendRequest.setVisibility(View.GONE);
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
