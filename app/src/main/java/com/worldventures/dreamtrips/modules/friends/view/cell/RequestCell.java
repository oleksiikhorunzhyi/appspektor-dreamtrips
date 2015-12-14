package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.CancelRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.HideRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.RejectRequestEvent;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_request)
public class RequestCell extends BaseUserCell {

    @InjectView(R.id.accept_button)
    FrameLayout accept;
    @InjectView(R.id.reject_button)
    FrameLayout reject;
    @InjectView(R.id.hide_button)
    FrameLayout hide;
    @InjectView(R.id.cancel_button)
    FrameLayout cancel;
    @InjectView(R.id.buttonContainer)
    ViewGroup container;

    public RequestCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();

        container.setVisibility(View.VISIBLE);

        switch (getModelObject().getRelationship()) {
            case INCOMING_REQUEST:
                hide.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                reject.setVisibility(View.VISIBLE);
                accept.setVisibility(View.VISIBLE);
                break;
            case OUTGOING_REQUEST:
                reject.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                hide.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                break;
            case REJECTED:
                reject.setVisibility(View.GONE);
                accept.setVisibility(View.GONE);
                hide.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                tvMutual.setVisibility(View.GONE);
                break;
        }
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
}
