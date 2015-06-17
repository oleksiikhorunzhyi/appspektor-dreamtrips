package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.adapter_friend_header)
public class RequestHeaderCell extends AbstractCell<String> {

    @InjectView(R.id.header)
    TextView header;

    public RequestHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        header.setText(getModelObject());
    }

    @Override
    public void prepareForReuse() {

    }
}
