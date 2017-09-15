package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.text.TextUtils;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.profile.service.command.GetPublicProfileCommand;
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class FeedItemAdditionalInfoPresenter<V extends FeedItemAdditionalInfoPresenter.View> extends Presenter<V> {

   @Inject AuthInteractor authInteractor;
   @Inject ProfileInteractor profileInteractor;

   private User user;

   public FeedItemAdditionalInfoPresenter(User user) {
      this.user = user;
   }

   @Override
   public void takeView(V view) {
      super.takeView(view);
      subscribeToUserUpdate();
   }

   public void loadUser() {
      if (user == null) return;
      //
      if (!TextUtils.isEmpty(user.getBackgroundPhotoUrl())) {
         view.setupView(user);
      } else {
         profileInteractor.publicProfilePipe().createObservable(new GetPublicProfileCommand(user.getId()))
               .compose(bindViewToMainComposer())
               .subscribe(new ActionStateSubscriber<GetPublicProfileCommand>()
                     .onSuccess(command -> view.setupView(command.getResult()))
                     .onFail((getPublicProfileCommand, throwable) -> view.setupView(user)));
      }
   }

   private void subscribeToUserUpdate() {
      view.bindUntilDropView(authInteractor.updateUserPipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<UpdateUserCommand>().onSuccess(userUpdateCommand -> {
               User updatedUser = userUpdateCommand.getResult();
               if (user != null && updatedUser.getId() == user.getId()) {
                  this.user = updatedUser;
                  view.setupView(updatedUser);
               }
            }));
   }

   public interface View extends RxView {

      void setupView(User user);
   }
}
