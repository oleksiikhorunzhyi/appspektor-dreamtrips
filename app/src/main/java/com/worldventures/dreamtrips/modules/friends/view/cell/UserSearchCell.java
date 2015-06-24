package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.net.Uri;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;
import com.worldventures.dreamtrips.modules.friends.events.UserClickedEvent;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_request)
public class UserSearchCell extends AbstractCell<User> {

    @InjectView(R.id.accept)
    AppCompatButton accept;
    @InjectView(R.id.avatar)
    SimpleDraweeView avatar;
    @InjectView(R.id.name)
    TextView name;
    @InjectView(R.id.buttonContainer)
    ViewGroup container;

    public UserSearchCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        accept.setText(R.string.add);
        accept.setVisibility(View.VISIBLE);
        name.setText(getModelObject().getFullName());
        avatar.setImageURI(Uri.parse(getModelObject().getAvatar().getMedium()));
        container.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.avatar)
    void onUserClicked() {
        getEventBus().post(new UserClickedEvent(getModelObject()));
    }

    @OnClick(R.id.accept)
    void onAccept() {
        getEventBus().post(new AddUserRequestEvent(getModelObject(), getAdapterPosition()));
    }

    @Override
    public void prepareForReuse() {

    }
}
