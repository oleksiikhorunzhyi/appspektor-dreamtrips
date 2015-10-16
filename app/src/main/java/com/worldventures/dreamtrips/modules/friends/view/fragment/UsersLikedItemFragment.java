package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.modules.friends.presenter.UsersLikedItemPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserCell;

@Layout(R.layout.fragment_users)
public class UsersLikedItemFragment extends BaseUsersFragment<UsersLikedItemPresenter, UsersLikedEntityBundle>
        implements UsersLikedItemPresenter.View {

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter.registerCell(User.class, UserCell.class);
    }

    @Override
    protected UsersLikedItemPresenter createPresenter(Bundle savedInstanceState) {
        return new UsersLikedItemPresenter(getArgs());
    }
}
