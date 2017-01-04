package com.worldventures.dreamtrips.modules.profile.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.profile.model.FriendGroupRelation;

public interface FriendPrefsCellDelegate extends CellDelegate<FriendGroupRelation> {

   void onRelationChanged(FriendGroupRelation friendGroupRelation, State state);
}
