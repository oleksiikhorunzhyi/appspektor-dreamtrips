package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.QuantityHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.modules.friends.presenter.UsersLikedItemPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserCell;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.UserCellDelegate;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_likes)
public class UsersLikedItemFragment extends BaseUsersFragment<UsersLikedItemPresenter, UsersLikedEntityBundle>
        implements UsersLikedItemPresenter.View, UserCellDelegate {

    @InjectView(R.id.title)
    TextView header;

    @Override
    protected UsersLikedItemPresenter createPresenter(Bundle savedInstanceState) {
        return new UsersLikedItemPresenter(getArgs());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter.registerCell(User.class, UserCell.class);
        adapter.registerDelegate(User.class, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //hack for https://trello.com/c/oKIh9Rnb/922-nav-bar-of-likers-pop-up-becomes-grey-if-go-back-from-profile (reproducible on android 5.0+ )
        header.getBackground().mutate().setAlpha(255);
    }

    @Override
    public void refreshUsers(List<User> users) {
        super.refreshUsers(users);
        if (isTabletLandscape()) {
            String titleArg = users.size() == 1 ? users.get(0).getFullName() : String.valueOf(users.size());

            String title = String.format(getResources().getString(
                    QuantityHelper.chooseResource(users.size(), R.string.people_liked_one, R.string.people_liked_other)), titleArg);
            header.setText(title);
            header.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected LinearLayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void acceptRequest(User user) {
        getPresenter().acceptRequest(user);
    }

    @Override
    public void addUserRequest(User user) {
        getPresenter().addUserRequest(user);
    }

    @Override
    public void onCellClicked(User model) {

    }
}
