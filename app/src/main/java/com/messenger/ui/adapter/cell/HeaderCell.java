package com.messenger.ui.adapter.cell;

import android.view.View;
import android.widget.TextView;

import com.messenger.ui.util.recyclerview.Header;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractCell;

import butterknife.InjectView;

@Layout(R.layout.list_item_contact_section_header)
public class HeaderCell extends BaseAbstractCell<Header> {

   @InjectView(R.id.section_name_textview) TextView sectionNameTextView;

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
