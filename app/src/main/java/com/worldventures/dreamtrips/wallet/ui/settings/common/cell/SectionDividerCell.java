package com.worldventures.dreamtrips.wallet.ui.settings.common.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.wallet.ui.settings.common.model.SectionDividerModel;

import butterknife.InjectView;

@Layout(R.layout.wallet_settings_list_header)
public class SectionDividerCell extends AbstractCell<SectionDividerModel> {

   @InjectView(R.id.title) TextView title;

   public SectionDividerCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      title.setText(getModelObject().getTitleId());
   }
}
