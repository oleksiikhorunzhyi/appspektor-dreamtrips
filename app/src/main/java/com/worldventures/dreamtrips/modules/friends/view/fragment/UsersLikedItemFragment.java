package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.seppius.i18n.plurals.PluralResources;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.bundle.UsersLikedEntityBundle;
import com.worldventures.dreamtrips.modules.friends.presenter.UsersLikedItemPresenter;
import com.worldventures.dreamtrips.modules.friends.view.cell.UserCell;

import java.util.List;

import butterknife.InjectView;
import timber.log.Timber;

@Layout(R.layout.fragment_likes)
public class UsersLikedItemFragment extends BaseUsersFragment<UsersLikedItemPresenter, UsersLikedEntityBundle>
        implements UsersLikedItemPresenter.View {

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
    }


    @Override
    public void onResume() {
        super.onResume();
        //hack for https://trello.com/c/oKIh9Rnb/922-nav-bar-of-likers-pop-up-becomes-grey-if-go-back-from-profile (reproducible on android 5.0+ )
        header.getBackground().setAlpha(255);

    }

    @Override
    public void refreshUsers(List<User> users) {
        super.refreshUsers(users);
        if (isTabletLandscape()) {
            Object titleArg = users.size() == 1 ? users.get(0).getFullName() : users.size();
            try {
                String title = new PluralResources(getResources()).getQuantityString(R.plurals.people_liked, users.size(), titleArg);
                header.setText(title);
                header.setVisibility(View.VISIBLE);
            } catch (NoSuchMethodException e) {
                Timber.w(e, "Can't set plural");
            }
        }
    }

    @Override
    protected LinearLayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
}
