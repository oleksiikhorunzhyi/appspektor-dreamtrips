package com.worldventures.dreamtrips.modules.infopages.presenter;

import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.infopages.service.command.AttachmentsRemovedCommand;
import com.worldventures.dreamtrips.modules.infopages.model.FeedbackImageAttachment;
import com.worldventures.dreamtrips.modules.infopages.service.FeedbackInteractor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class FeedbackImageAttachmentsPresenter extends Presenter<FeedbackImageAttachmentsPresenter.View> {

   private int initialPosition;
   @State ArrayList<FeedbackImageAttachment> attachments;
   @State ArrayList<FeedbackImageAttachment> locallyDeletedAttachments = new ArrayList<>();

   @Inject BackStackDelegate backStackDelegate;
   @Inject FeedbackInteractor feedbackInteractor;

   public FeedbackImageAttachmentsPresenter(List<FeedbackImageAttachment> initialAttachments, int initialPosition) {
      this.attachments = new ArrayList<>(initialAttachments);
      this.initialPosition = initialPosition;
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      view.addItems(attachments);
      view.setPosition(initialPosition);
      backStackDelegate.setListener(this::handleBackPress);
   }

   public boolean handleBackPress() {
      if (!locallyDeletedAttachments.isEmpty()) {
         feedbackInteractor.attachmentsRemovedPipe()
               .send(new AttachmentsRemovedCommand(locallyDeletedAttachments));
      }
      return false;
   }

   public void onRemoveItem(int position) {
      locallyDeletedAttachments.add(attachments.remove(position));
      view.removeItem(position);
   }

   public interface View extends Presenter.View {

      void addItems(List<FeedbackImageAttachment> imageAttachments);

      void setPosition(int position);

      void removeItem(int position);
   }
}
