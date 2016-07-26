package com.messenger.ui.adapter.cell;

import android.view.View;
import android.widget.TextView;

import com.messenger.ui.util.recyclerview.Header;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;

import butterknife.InjectView;

@Layout(R.layout.list_item_contact_section_header)
public class HeaderCell extends AbstractCell<Header> {

    @InjectView(R.id.section_name_textview)
    TextView sectionNameTextView;

    public HeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        sectionNameTextView.setText(getModelObject().getName());
    }

    @Override
    public boolean shouldInject() {
        return false;
    }
}
