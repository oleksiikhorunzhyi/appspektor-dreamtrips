package com.worldventures.dreamtrips.social.ui.profile.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.profile.model.FriendGroupRelation;
import com.worldventures.dreamtrips.social.ui.profile.view.cell.delegate.FriendPrefsCellDelegate;
import com.worldventures.dreamtrips.social.ui.profile.view.cell.delegate.State;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_friend_pref_group)
public class FriendPrefGroupCell extends BaseAbstractDelegateCell<FriendGroupRelation, FriendPrefsCellDelegate> {

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

   @Override
   public boolean shouldInject() {
      return false;
   }
}
