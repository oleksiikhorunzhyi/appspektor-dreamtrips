package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.bundle.SingleCommentBundle;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import javax.inject.Inject;

public class EditCommentPresenter extends Presenter<EditCommentPresenter.View> {
   private FeedEntity feedEntity;

   private Comment comment;

   @Inject FeedEntityManager entityManager;

   public EditCommentPresenter(SingleCommentBundle bundle) {
      this.feedEntity = bundle.getFeedEntity();
      this.comment = bundle.getComment();
   }

   @Override
   public void takeView(EditCommentPresenter.View view) {
      super.takeView(view);
      view.setText(comment.getMessage());
      User owner = comment.getOwner();
      view.setImageURI(Uri.parse(owner.getAvatar().getThumb()));
      view.setUsername(owner.getFullName());
   }

   @Override
   public void onInjected() {
      super.onInjected();
      entityManager.setRequestingPresenter(this);
   }

   public void onSave() {
      if (comment != null && view != null) {
         comment.setMessage(view.getText());
         entityManager.updateComment(feedEntity, comment);
      }
   }

   public void onEvent(FeedEntityManager.CommentEvent event) {
      if (event.getType() == FeedEntityManager.CommentEvent.Type.EDITED) {
         if (event.getSpiceException() == null) {
            view.close();
         } else {
            handleError(event.getSpiceException());
            view.enableSaveButton();
         }
      }
   }

   public interface View extends Presenter.View {
      void setUsername(String name);

      void setImageURI(Uri uri);

      void close();

      void enableSaveButton();

      String getText();

      void setText(String text);
   }
}
