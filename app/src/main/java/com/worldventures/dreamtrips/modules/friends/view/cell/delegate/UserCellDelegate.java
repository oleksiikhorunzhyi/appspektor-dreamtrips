package com.worldventures.dreamtrips.modules.friends.view.cell.delegate;

import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.modules.common.model.User;

public interface UserCellDelegate extends CellDelegate<User> {

   void acceptRequest(User user);

   void addUserRequest(User user);
}
