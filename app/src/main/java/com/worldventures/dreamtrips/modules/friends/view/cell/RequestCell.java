package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.AcceptRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.CancelRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.HideRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.RejectRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_request)
public class RequestCell extends AbstractCell<User> {

    @InjectView(R.id.accept)
    AppCompatButton accept;
    @InjectView(R.id.reject)
    AppCompatButton reject;
    @InjectView(R.id.hide)
    AppCompatButton hide;
    @InjectView(R.id.cancel)
    AppCompatButton cancel;
    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.progress)
    ProgressBar progressBar;
    @InjectView(R.id.buttonContainer)
    ViewGroup container;

    public RequestCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        ColorStateList csl = itemView.getResources().getColorStateList(R.color.button_background);
        name.setText(getModelObject().getFullName());
        avatar.setImageURI(Uri.parse(getModelObject().getAvatar().getMedium()));
        container.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        reject.setSupportBackgroundTintList(csl);
        hide.setSupportBackgroundTintList(csl);
        cancel.setSupportBackgroundTintList(csl);

        if (getModelObject().getRelationship().equals(User.RELATION_INCOMING_REQUEST)) {
            hide.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            reject.setVisibility(View.VISIBLE);
            accept.setVisibility(View.VISIBLE);
        } else if (getModelObject().getRelationship().equals(User.RELATION_OUTGOING_REQUEST)) {
            reject.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            hide.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
        } else if (getModelObject().getRelationship().equals(User.RELATION_REJECT)) {
            reject.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            hide.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
        }
    }



    @OnClick(R.id.avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    @OnClick(R.id.accept)
    void onAccept() {
        getEventBus().post(new AcceptRequestEvent(getModelObject(), getAdapterPosition()));
        showProgress();
    }

    @OnClick(R.id.reject)
    void onReject() {
        getEventBus().post(new RejectRequestEvent(getModelObject(), getAdapterPosition()));
        showProgress();
    }

    @OnClick(R.id.hide)
    void onHide() {
        getEventBus().post(new HideRequestEvent(getModelObject(), getAdapterPosition()));
        showProgress();
    }

    @OnClick(R.id.cancel)
    void onCancel() {
        getEventBus().post(new CancelRequestEvent(getModelObject(), getAdapterPosition()));
        showProgress();
    }

    private void showProgress() {
        container.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void prepareForReuse() {

    }
}
