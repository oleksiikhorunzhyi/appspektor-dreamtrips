package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;

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
    protected ButtonRectangle accept;
    @InjectView(R.id.reject)
    protected ButtonRectangle reject;

    @Override
    protected UserPresenter createPresenter(Bundle savedInstanceState) {
        return new UserPresenter(getArguments());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        cover.setVisibility(View.GONE);
        avatar.setVisibility(View.GONE);
        updateInfo.setVisibility(View.GONE);
        userBalance.setVisibility(View.GONE);
        addFriend.setVisibility(View.VISIBLE);
        layoutConfiguration();
    }

    private void layoutConfiguration() {
        int padding = getResources().getDimensionPixelSize(R.dimen.spacing_large);
        accept.setText(getString(R.string.profile_accept));
        reject.setText(getString(R.string.profile_reject));
        accept.getTextView().setPadding(padding, 0, padding, 0);
        reject.getTextView().setPadding(padding, 0, padding, 0);
        reject.setTextColor(getResources().getColor(R.color.black_semi_transparent));
    }

    @Override
    public void setIsFriend(boolean isFriend) {
        addFriend.setText(isFriend ? R.string.profile_friends : R.string.profile_add_friend);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                isFriend ? R.drawable.friend_added
                        : R.drawable.add_friend,
                0, 0);
    }

    @OnClick(R.id.add_friend)
    void onAddFriend() {
        getPresenter().addFriendClicked();
    }

    @Override
    public void showAddFriendDialog(String name) {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.social_add_friend_title)
                .content(R.string.social_add_friend_content, name)
                .positiveText(R.string.social_add_friend_yes)
                .negativeText(R.string.social_add_friend_no)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        getPresenter().addAsFriend();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }


    @Override
    public void showFriendRequest(String name) {
        friendRequestCaption.setText(String.format(getString(R.string.profile_friend_request), name));
        addFriend.setText(R.string.profile_respond);
        addFriend.setCompoundDrawablesWithIntrinsicBounds(0,
                R.drawable.respond,
                0, 0);
        friendRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFriendRequest() {
        friendRequest.setVisibility(View.GONE);
    }


}
