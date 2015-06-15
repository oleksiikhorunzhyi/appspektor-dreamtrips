package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.UserWrapper;
@Layout(R.layout.adapter_item_user_wrapper_cell)
public class UserWrapperCell extends AbstractCell<UserWrapper> {

    public UserWrapperCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {

    }

    @Override
    public void prepareForReuse() {

    }
}
