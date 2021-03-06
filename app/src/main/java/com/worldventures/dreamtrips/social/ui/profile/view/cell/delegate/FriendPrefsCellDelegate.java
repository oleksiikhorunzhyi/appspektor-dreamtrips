package com.worldventures.dreamtrips.social.ui.profile.view.cell.delegate;

import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.service.profile.model.FriendGroupRelation;

public interface FriendPrefsCellDelegate extends CellDelegate<FriendGroupRelation> {

   void onRelationChanged(FriendGroupRelation friendGroupRelation, State state);
}
