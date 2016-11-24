package com.worldventures.dreamtrips.modules.feed.presenter;

import android.net.Uri;

import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.bundle.SingleCommentBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.EditCommentCommand;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class EditCommentPresenter extends Presenter<EditCommentPresenter.View> {

   private FeedEntity feedEntity;
   private Comment comment;

   @Inject CommentsInteractor commentsInteractor;

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
      observeEditing();
   }

   public void onSave(String text) {
      commentsInteractor.editCommentPipe().send(new EditCommentCommand(feedEntity, comment.getUid(), text));
   }

   private void observeEditing() {
      commentsInteractor.editCommentPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<EditCommentCommand>()
                  .onStart(commentCommand -> view.disableSaveButton())
                  .onSuccess(commentCommand -> view.close())
                  .onFail(this::handleError));
   }

   @Override
   public void handleError(Object action, Throwable error) {
      super.handleError(action, error);
      view.enableSaveButton();
   }

   public interface View extends Presenter.View {
      void setUsername(String name);

      void setImageURI(Uri uri);

      void close();

      void disableSaveButton();

      void enableSaveButton();

      void setText(String text);
   }
}
