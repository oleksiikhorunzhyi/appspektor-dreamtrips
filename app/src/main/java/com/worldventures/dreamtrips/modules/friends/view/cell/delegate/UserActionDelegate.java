package com.worldventures.dreamtrips.modules.friends.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.User;

public interface UserActionDelegate extends CellDelegate<User> {
   void userClicked(User user);
}
