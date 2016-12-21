package com.worldventures.dreamtrips.modules.profile.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.profile.model.FriendGroupRelation;
import com.worldventures.dreamtrips.modules.profile.view.cell.delegate.FriendPrefsCellDelegate;
import com.worldventures.dreamtrips.modules.profile.view.cell.delegate.State;

import butterknife.InjectView;



@Layout(R.layout.adapter_item_friend_pref_group)
public class FriendPrefGroupCell extends AbstractDelegateCell<FriendGroupRelation, FriendPrefsCellDelegate> {

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
         cellDelegate.onRelationChanged(getModelObject(), state);
      });
   }
}
