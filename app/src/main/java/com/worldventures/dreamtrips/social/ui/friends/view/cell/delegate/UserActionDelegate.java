package com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate;

import com.worldventures.core.model.User;
import com.worldventures.core.ui.view.cell.CellDelegate;

public interface UserActionDelegate extends CellDelegate<User> {
   void userClicked(User user);
}
