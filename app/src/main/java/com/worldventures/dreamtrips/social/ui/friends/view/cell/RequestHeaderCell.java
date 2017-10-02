package com.worldventures.dreamtrips.social.ui.friends.view.cell;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.social.ui.friends.model.RequestHeaderModel;
import com.worldventures.dreamtrips.social.ui.friends.view.cell.delegate.RequestHeaderCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_friend_header)
public class RequestHeaderCell extends BaseAbstractDelegateCell<RequestHeaderModel, RequestHeaderCellDelegate> {

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

   @Override
   public boolean shouldInject() {
      return false;
   }
}
