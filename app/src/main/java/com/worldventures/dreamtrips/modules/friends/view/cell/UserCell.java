package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_user)
public class UserCell extends AbstractCell<User> {

    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.mutual)
    TextView mutual;
    @InjectView(R.id.company)
    TextView company;
    @InjectView(R.id.status)
    ImageView status;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    public UserCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        avatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));

        name.setText(user.getFullName());

        if (!TextUtils.isEmpty(getModelObject().getCompany())) {
            company.setText(getModelObject().getCompany());
            company.setVisibility(View.VISIBLE);
        } else {
            company.setVisibility(View.GONE);
        }

        if (getModelObject().getMutualFriends() == 0) {
            mutual.setVisibility(View.GONE);
        } else {
            mutual.setVisibility(View.VISIBLE);
            mutual.setText(itemView.getContext().getString(R.string.social_postfix_mutual_friends,
                    getModelObject().getMutualFriends()));
        }

        if (!appSessionHolder.get().get().getUser().equals(user)
                && user.getRelationship() != null) {
            status.setVisibility(View.VISIBLE);
            switch (user.getRelationship()) {
                case FRIEND:
                    status.setImageResource(R.drawable.ic_profile_friend);
                    status.setOnClickListener(v -> openFriendActionDialog());
                    break;
                case OUTGOING_REQUEST:
                    status.setImageResource(R.drawable.ic_profile_friend_respond);
                    status.setOnClickListener(null);
                    break;
                case REJECT:
                    status.setImageResource(R.drawable.ic_profile_friend_respond);
                    status.setOnClickListener(null);
                    break;
                case INCOMING_REQUEST:
                    status.setImageResource(R.drawable.ic_profile_add_friend_selector);
                    status.setOnClickListener(v -> acceptRequest());
                    break;
                default:
                    status.setImageResource(R.drawable.ic_profile_add_friend_selector);
                    status.setOnClickListener(v -> addUser());
                    break;
            }
        } else {
            status.setVisibility(View.GONE);
        }
    }

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.avatar)
    void userClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    void acceptRequest() {
        getEventBus().post(new AcceptRequestEvent(getModelObject(), getAdapterPosition()));
    }

    void addUser() {
        getEventBus().post(new AddUserRequestEvent(getModelObject()));
    }

    private void openFriendActionDialog() {
        new FriendActionDialogDelegate(itemView.getContext(), getEventBus())
                .showFriendDialog(getModelObject(), avatar.getDrawable());
    }


}
