package com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate;

import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.social.service.users.base.model.RequestHeaderModel;

public interface RequestHeaderCellDelegate extends CellDelegate<RequestHeaderModel> {

   void acceptAllRequests();
}
