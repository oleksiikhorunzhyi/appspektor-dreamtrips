package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.events.AddUserRequestEvent;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_user_search)
public class UserSearchCell extends BaseUserCell {

    public UserSearchCell(View view) {
        super(view);
    }

    @OnClick(R.id.add)
    void onAccept() {
        getEventBus().post(new AddUserRequestEvent(getModelObject()));
    }
}
