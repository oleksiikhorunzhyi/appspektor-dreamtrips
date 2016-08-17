package com.worldventures.dreamtrips.modules.friends.view.cell;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.friends.view.cell.delegate.RequestCellDelegate;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_request)
public class RequestCell extends BaseUserCell<RequestCellDelegate> {

   @InjectView(R.id.accept_button) FrameLayout accept;
   @InjectView(R.id.reject_button) FrameLayout reject;
   @InjectView(R.id.hide_button) FrameLayout hide;
   @InjectView(R.id.cancel_button) FrameLayout cancel;
   @InjectView(R.id.hide) View btnHide;
   @InjectView(R.id.cancel) View btnCancel;
   @InjectView(R.id.reject) View btnReject;
   @InjectView(R.id.accept) View btnAccept;
   @InjectView(R.id.buttonContainer) ViewGroup container;

   public RequestCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      super.syncUIStateWithModel();
      container.setVisibility(View.VISIBLE);
      switch (getModelObject().getRelationship()) {
         case INCOMING_REQUEST:
            hide.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            reject.setVisibility(View.VISIBLE);
            accept.setVisibility(View.VISIBLE);
            break;
         case OUTGOING_REQUEST:
            reject.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            hide.setVisibility(View.GONE);
            cancel.setVisibility(View.VISIBLE);
            break;
         case REJECTED:
            reject.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            hide.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.GONE);
            tvMutual.setVisibility(View.GONE);
            break;
      }
      enableButtons();
   }

   @OnClick(R.id.accept)
   void onAccept() {
      btnAccept.setEnabled(false);
      cellDelegate.acceptRequest(getModelObject());
   }

   @OnClick(R.id.reject)
   void onReject() {
      btnReject.setEnabled(false);
      cellDelegate.rejectRequest(getModelObject());
      TrackingHelper.tapMyFriendsButtonFeed(TrackingHelper.ATTRIBUTE_REJECT_FRIEND_REQUEST);
   }

   @OnClick(R.id.hide)
   void onHide() {
      btnHide.setEnabled(false);
      cellDelegate.hideRequest(getModelObject());
   }

   @OnClick(R.id.cancel)
   void onCancel() {
      btnCancel.setEnabled(false);
      cellDelegate.cancelRequest(getModelObject());
      TrackingHelper.tapMyFriendsButtonFeed(TrackingHelper.ATTRIBUTE_CANCEL_FRIEND_REQUEST);
   }

   private void enableButtons() {
      btnAccept.setEnabled(true);
      btnReject.setEnabled(true);
      btnHide.setEnabled(true);
      btnCancel.setEnabled(true);
   }
}
