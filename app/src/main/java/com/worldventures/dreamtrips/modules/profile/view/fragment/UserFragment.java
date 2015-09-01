package com.worldventures.dreamtrips.modules.profile.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.innahema.collections.query.functions.Action1;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.presenter.UserPresenter;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import java.util.List;

import butterknife.ButterKnife;

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
            new FriendActionDialogDelegate(getActivity(), getEventBus()).showFriendDialog(user, userPhoto.getDrawable());
        }
    }
}
