package com.worldventures.dreamtrips.social.ui.friends.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.social.service.friends.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;
import com.worldventures.dreamtrips.social.ui.profile.model.FriendGroupRelation;
import com.worldventures.dreamtrips.social.ui.profile.service.ProfileInteractor;
import com.worldventures.dreamtrips.social.ui.profile.service.command.AddFriendToCircleCommand;
import com.worldventures.dreamtrips.social.ui.profile.service.command.RemoveFriendFromCircleCommand;
import com.worldventures.dreamtrips.social.ui.profile.view.cell.delegate.State;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class FriendPreferencesPresenter extends Presenter<FriendPreferencesPresenter.View> {

   @Inject CirclesInteractor circlesInteractor;
   @Inject ProfileInteractor profileInteractor;

   private User friend;

   public FriendPreferencesPresenter(UserBundle userBundle) {
      friend = userBundle.getUser();
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      subscribeCircles();
      updateCircles();
   }

   private void updateCircles() {
      circlesInteractor.getPipe().send(new GetCirclesCommand());
   }

   private void subscribeCircles() {
      circlesInteractor.getPipe()
            .observe()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> onCirclesSuccess(new ArrayList<>(circlesCommand.getResult())))
                  .onFail(this::onCirclesError));
   }

   private void onCirclesStart() {
      view.showBlockingProgress();
   }

   private void onCirclesSuccess(List<Circle> resultCircles) {
      view.addItems(Queryable.from(resultCircles).map(circle -> {
         return new FriendGroupRelation(circle, friend);
      }).toList());
      view.hideBlockingProgress();
   }

   private void onCirclesError(CommandWithError commandWithError, Throwable throwable) {
      view.hideBlockingProgress();
      handleError(commandWithError, throwable);
   }

   public void onRelationshipChanged(Circle circle, State state) {
      switch (state) {
         case ADDED:
            profileInteractor.addFriendToCirclesPipe().send(new AddFriendToCircleCommand(circle, friend));
            friend.getCircles().add(circle);
            break;
         case REMOVED:
            profileInteractor.removeFriendFromCirclesPipe().send(new RemoveFriendFromCircleCommand(circle, friend));
            friend.getCircles().remove(circle);
            break;
         default:
            break;
      }

   }

   public interface View extends Presenter.View, BlockingProgressView {
      void addItems(List<FriendGroupRelation> circles);
   }
}
