package com.worldventures.dreamtrips.modules.profile.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.profile.model.FriendGroupRelation;
import com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.modules.profile.event.FriendGroupRelationChangedEvent.State;

@Layout(R.layout.adapter_item_friend_pref_group)
public class FriendPrefGroupCell extends AbstractCell<FriendGroupRelation> {

    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.cb) CheckBox cb;

    public FriendPrefGroupCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        title.setText(getModelObject().circle().getName());
        itemView.setOnClickListener(v -> cb.setChecked(!cb.isChecked()));
        cb.setOnCheckedChangeListener(null);
        cb.setChecked(getModelObject().isFriendInCircle());
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            State state = isChecked ? State.ADDED : State.REMOVED;
            getEventBus().post(new FriendGroupRelationChangedEvent(
                            getModelObject().friend(),
                            getModelObject().circle(),
                            state)
            );
        });
    }
}
