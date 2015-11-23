package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.CancelRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.HideRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.RejectRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;
import com.worldventures.dreamtrips.modules.friends.view.util.MutualStringUtil;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_request)
public class RequestCell extends AbstractCell<User> {

    @InjectView(R.id.accept_button)
    FrameLayout accept;
    @InjectView(R.id.reject_button)
    FrameLayout reject;
    @InjectView(R.id.hide_button)
    FrameLayout hide;
    @InjectView(R.id.cancel_button)
    FrameLayout cancel;
    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.company)
    TextView companyName;
    @InjectView(R.id.buttonContainer)
    ViewGroup container;
    @InjectView(R.id.info)
    TextView tvMutual;

    private MutualStringUtil mutualStringUtil;

    public RequestCell(View view) {
        super(view);
        mutualStringUtil = new MutualStringUtil(view.getContext());
    }

    @Override
    protected void syncUIStateWithModel() {
        name.setText(getModelObject().getFullName());

        if (!TextUtils.isEmpty(getModelObject().getCompany())) {
            companyName.setText(getModelObject().getCompany());
            companyName.setVisibility(View.VISIBLE);
        } else {
            companyName.setVisibility(View.GONE);
        }

        avatar.setImageURI(Uri.parse(getModelObject().getAvatar().getMedium()));
        container.setVisibility(View.VISIBLE);

        switch (getModelObject().getRelationship()) {
            case INCOMING_REQUEST:
                hide.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                reject.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
                String mutual = mutualStringUtil.createMutualString(getModelObject().getMutualFriends());
                tvMutual.setVisibility(TextUtils.isEmpty(mutual) ? View.GONE : View.VISIBLE);
                tvMutual.setText(mutual);
                break;
            case OUTGOING_REQUEST:
                reject.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                hide.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                tvMutual.setVisibility(View.GONE);
                break;
            case REJECT:
                reject.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                hide.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                tvMutual.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick(R.id.avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    @OnClick(R.id.accept)
    void onAccept() {
        getEventBus().post(new AcceptRequestEvent(getModelObject(), getAdapterPosition()));
    }

    @OnClick(R.id.reject)
    void onReject() {
        getEventBus().post(new RejectRequestEvent(getModelObject(), getAdapterPosition()));
        TrackingHelper.tapMyFriendsButtonFeed(TrackingHelper.ATTRIBUTE_REJECT_FRIEND_REQUEST);
    }

    @OnClick(R.id.hide)
    void onHide() {
        getEventBus().post(new HideRequestEvent(getModelObject(), getAdapterPosition()));
    }

    @OnClick(R.id.cancel)
    void onCancel() {
        getEventBus().post(new CancelRequestEvent(getModelObject(), getAdapterPosition()));
        TrackingHelper.tapMyFriendsButtonFeed(TrackingHelper.ATTRIBUTE_CANCEL_FRIEND_REQUEST);
    }

    @Override
    public void prepareForReuse() {

    }
}
