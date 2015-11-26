package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.support.annotation.Nullable;
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
import com.worldventures.dreamtrips.modules.friends.view.util.MutualStringUtil;
import com.worldventures.dreamtrips.modules.profile.view.dialog.FriendActionDialogDelegate;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_user)
public class UserCell extends AbstractCell<User> {

    @InjectView(R.id.sdv_avatar)
    SimpleDraweeView sdvAvatar;
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_mutual)
    TextView tvMutual;
    @InjectView(R.id.tv_company)
    TextView tvCompany;
    @InjectView(R.id.tv_group)
    TextView tvGroup;
    @InjectView(R.id.iv_status)
    ImageView ivStatus;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;

    private MutualStringUtil mutualStringUtil;

    public UserCell(View view) {
        super(view);
        mutualStringUtil = new MutualStringUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        User user = getModelObject();
        sdvAvatar.setImageURI(Uri.parse(user.getAvatar().getThumb()));

        tvName.setText(user.getFullName());

        String companyName = getModelObject().getCompany();
        tvCompany.setVisibility(TextUtils.isEmpty(companyName)? View.GONE : View.VISIBLE);
        tvCompany.setText(companyName);

        String circleName = user.getCircles();
        tvGroup.setVisibility(TextUtils.isEmpty(circleName) ? View.GONE : View.VISIBLE);
        tvGroup.setText(circleName);

        String mutualText = mutualStringUtil.createMutualString(getModelObject().getMutualFriends());
        tvMutual.setVisibility(TextUtils.isEmpty(mutualText) ? View.GONE : View.VISIBLE);
        tvMutual.setText(mutualText);

        if (!appSessionHolder.get().get().getUser().equals(user)
                && user.getRelationship() != null) {
            ivStatus.setVisibility(View.VISIBLE);
            switch (user.getRelationship()) {
                case FRIEND:
                    setStatusParameters(R.drawable.ic_profile_friend, v -> openFriendActionDialog());
                    break;
                case OUTGOING_REQUEST:
                case REJECT:
                    setStatusParameters(R.drawable.ic_profile_friend_respond, null);
                    break;
                case INCOMING_REQUEST:
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

    @Override
    public void prepareForReuse() {

    }

    @OnClick(R.id.sdv_avatar)
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
                .showFriendDialog(getModelObject(), sdvAvatar.getDrawable());
    }


}
