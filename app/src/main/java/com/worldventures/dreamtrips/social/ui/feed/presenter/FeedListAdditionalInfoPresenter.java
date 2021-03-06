package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.support.annotation.NonNull;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.model.Circle;
import com.worldventures.core.model.User;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.BlockingProgressView;
import com.worldventures.dreamtrips.social.domain.storage.SocialSnappyRepository;
import com.worldventures.dreamtrips.social.service.users.base.interactor.CirclesInteractor;
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.users.circle.command.GetCirclesCommand;
import com.worldventures.dreamtrips.social.service.users.friend.command.GetFriendsCommand;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FeedListAdditionalInfoPresenter extends FeedItemAdditionalInfoPresenter<FeedListAdditionalInfoPresenter.View> {

   private static final int PAGE_SIZE = 100;

   @Inject SocialSnappyRepository db;
   @Inject CirclesInteractor circlesInteractor;
   @Inject FriendsInteractor friendsInteractor;

   private int nextPage = 1;
   private int prevTotalItemCount = 0;
   private boolean loading = true;
   private boolean canLoadMore = true;

   public FeedListAdditionalInfoPresenter(User user) {
      super(user);
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      if (view.isTabletLandscape()) {
         loadFriends();
         view.setCurrentCircle(getFilterCircle());
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // Circles interaction
   ///////////////////////////////////////////////////////////////////////////

   public void onCirclePicked(Circle c) {
      db.saveFeedFriendPickedCircle(c);
      reload();
   }

   public void onCircleFilterClicked() {
      circlesInteractor.getPipe()
            .createObservable(new GetCirclesCommand())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<GetCirclesCommand>().onStart(circlesCommand -> onCirclesStart())
                  .onSuccess(circlesCommand -> onCirclesSuccess(new ArrayList<>(circlesCommand.getResult())))
                  .onFail(this::onCirclesError));

   }

   private void onCirclesStart() {
      view.showBlockingProgress();
   }

   private void onCirclesError(CommandWithError commandWithError, Throwable throwable) {
      view.hideBlockingProgress();
      handleError(commandWithError, throwable);
   }

   private void onCirclesSuccess(List<Circle> resultCircles) {
      resultCircles.add(getDefaultCircleFilter());
      Collections.sort(resultCircles);
      view.hideBlockingProgress();
      view.showCirclePicker(resultCircles, getFilterCircle());
   }

   @NonNull
   private Circle getFilterCircle() {
      Circle filterCircle = db.getFeedFriendPickedCircle();
      return filterCircle == null ? getDefaultCircleFilter() : filterCircle;
   }

   private Circle getDefaultCircleFilter() {
      return Circle.withTitle(context.getString(R.string.all_friends));
   }

   ///////////////////////////////////////////////////////////////////////////
   // Friends loading
   ///////////////////////////////////////////////////////////////////////////

   private void loadFriends() {
      friendsInteractor.getFriendsPipe()
            .createObservable(new GetFriendsCommand(getFilterCircle(), nextPage, PAGE_SIZE))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetFriendsCommand>()
                  .onStart(getFriendsCommand -> {
                     loading = true;
                     view.startLoading();
                  })
                  .onSuccess(getFriendsCommand -> {
                     if (nextPage == 1) {
                        view.setFriends(new ArrayList<>(getFriendsCommand.getResult()));
                     } else {
                        view.addFriends(new ArrayList<>(getFriendsCommand.getResult()));
                     }
                     canLoadMore = getFriendsCommand.getResult().size() > 0;
                     nextPage++;
                     loading = false;
                     view.finishLoading();
                  })
                  .onFail((getFriendsCommand, throwable) -> {
                     loading = false;
                     view.finishLoading();
                     handleError(getFriendsCommand, throwable);
                  })
            );
   }

   public void reload() {
      nextPage = 1;
      prevTotalItemCount = 0;
      loadFriends();
   }

   public void onScrolled(int totalItemCount, int lastVisible) {
      if (totalItemCount > prevTotalItemCount) {
         prevTotalItemCount = totalItemCount;
      }
      if (!loading && canLoadMore && lastVisible >= totalItemCount - 1) {
         loadFriends();
      }
   }

   ///////////////////////////////////////////////////////////////////////////
   // User related events
   ///////////////////////////////////////////////////////////////////////////

   public void userClicked(User user) {
      view.openUser(new UserBundle(user));
   }

   public interface View extends FeedItemAdditionalInfoPresenter.View, BlockingProgressView {

      void startLoading();

      void finishLoading();

      void setFriends(@NonNull List<User> friends);

      void addFriends(@NonNull List<User> friends);

      void showCirclePicker(@NonNull List<Circle> circles, @NonNull Circle activeCircle);

      void setCurrentCircle(Circle currentCircle);

      void openUser(UserBundle bundle);
   }
}
