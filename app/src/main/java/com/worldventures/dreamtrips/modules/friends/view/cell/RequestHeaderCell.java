package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.friends.model.RequestHeaderModel;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.RequestHeaderCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_friend_header)
public class RequestHeaderCell extends AbstractDelegateCell<RequestHeaderModel, RequestHeaderCellDelegate> {

   public static final int MIN_REQUEST_COUNT_FOR_ADVANCED_VIEW = 2;

   @InjectView(R.id.header) TextView header;
   @InjectView(R.id.requestCountDesc) TextView requestCountDesc;
   @InjectView(R.id.advanced_container) LinearLayout advancedContainer;

   public RequestHeaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      header.setText(getModelObject().getName());
      boolean advanced = getModelObject().isAdvanced() && getModelObject().getCount() >= MIN_REQUEST_COUNT_FOR_ADVANCED_VIEW;
      advancedContainer.setVisibility(advanced ? View.VISIBLE : View.GONE);
      requestCountDesc.setText(requestCountDesc.getResources()
            .getString(R.string.request_count_desc, getModelObject().getCount()));
   }

   @OnClick(R.id.accept_all_btn)
   void onAcceptAll() {
      cellDelegate.acceptAllRequests();
   }
}
