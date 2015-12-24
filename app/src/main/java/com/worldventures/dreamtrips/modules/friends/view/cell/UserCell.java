package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_user)
public class UserCell extends BaseUserCell {

    @InjectView(R.id.iv_status)
    ImageView ivStatus;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    public UserCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();

        String circleName = getModelObject().getCirclesString();
        tvGroup.setVisibility(TextUtils.isEmpty(circleName) ? View.GONE : View.VISIBLE);
        tvGroup.setText(circleName);

        if (!appSessionHolder.get().get().getUser().equals(getModelObject())
                && getModelObject().getRelationship() != null) {
            ivStatus.setVisibility(View.VISIBLE);
            switch (getModelObject().getRelationship()) {
                case FRIEND:
                    setStatusParameters(R.drawable.ic_profile_friend, v -> openFriendActionDialog());
                    break;
                case OUTGOING_REQUEST:
                    setStatusParameters(R.drawable.ic_profile_friend_respond, null);
                    break;
                case INCOMING_REQUEST:
                case REJECTED:
                    setStatusParameters(R.drawable.ic_profile_add_friend_selector, v -> acceptRequest());
                    break;
                default:
                    setStatusParameters(R.drawable.ic_profile_add_friend_selector, v -> addUser());
                    break;
            }
        } else {
            ivStatus.setVisibility(View.GONE);
        }
    }

    private void setStatusParameters(int drawableId, @Nullable View.OnClickListener listener){
        ivStatus.setImageResource(drawableId);
        ivStatus.setOnClickListener(listener);
    }

    void acceptRequest() {
        getEventBus().post(new AcceptRequestEvent(getModelObject(), getAdapterPosition()));
    }

    void addUser() {
        getEventBus().post(new AddUserRequestEvent(getModelObject()));
    }

    private void openFriendActionDialog() {
        new FriendActionDialogDelegate(itemView.getContext(), getEventBus())
                .showFriendDialog(getModelObject(), sdvAvatar.getDrawable());
    }
}
