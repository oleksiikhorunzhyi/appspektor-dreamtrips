package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_user_search)
public class UserSearchCell extends BaseUserCell {

    @InjectView(R.id.add)
    TextView add;
    @InjectView(R.id.add_wrapper)
    View addWrapper;
    @InjectView(R.id.pending_wrapper)
    View pendingWrapper;

    public UserSearchCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        super.syncUIStateWithModel();
        if (getModelObject().getRelationship() == User.Relationship.OUTGOING_REQUEST) {
            addWrapper.setVisibility(View.GONE);
            pendingWrapper.setVisibility(View.VISIBLE);
        } else {
            pendingWrapper.setVisibility(View.GONE);
            addWrapper.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.add)
    void onAccept() {
        getEventBus().post(new AddUserRequestEvent(getModelObject()));
    }
}
