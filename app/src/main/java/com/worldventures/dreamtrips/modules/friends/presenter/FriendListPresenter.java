package com.worldventures.dreamtrips.modules.friends.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.request.Query;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.api.janet.command.CirclesCommand;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.friends.api.GetFriendsQuery;
import com.worldventures.dreamtrips.modules.friends.model.Circle;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

public class FriendListPresenter extends BaseUserListPresenter<FriendListPresenter.View> {

   @State Circle selectedCircle;
   @State String query;
   @State int position = 0;

   @Inject SnappyRepository snappyRepository;

   @Override
   public void onInjected() {
      super.onInjected();
      query = "";
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      friendsInteractor.acceptRequestPipe()
            .observeSuccess()
            .compose(bindView())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(action -> reload());
   }

   public void onFilterClicked() {
      getCirclesObservable().subscribe(new ActionStateSubscriber<CirclesCommand>().onStart(circlesCommand -> onCirclesStart())
            .onSuccess(circlesCommand -> onCirclesFilterSuccess(circlesCommand.getResult()))
            .onFail((circlesCommand, throwable) -> onCirclesError(circlesCommand.getErrorMessage())));
   }

   private void onCirclesFilterSuccess(List<Circle> circles) {
      onCirclesSuccess(circles);
      circles.add(0, Circle.all(context.getString(R.string.show_all)));
      view.showFilters(circles, position);
   }

   public void reloadWithFilter(Circle circle, int position) {
      selectedCircle = circle;
      this.position = position;
      reload();
   }

   @Override
   protected void userStateChanged(User user) {
      view.finishLoading();
      users.remove(user);
      view.refreshUsers(users);
   }

   public String getQuery() {
      return query;
   }

   public void setQuery(String query) {
      int previousLength = this.query.length();
      this.query = query;
      if (query.length() < 3 && (previousLength < query.length() || previousLength < 3)) return;
      //
      reload();
   }

   @Override
   public void handleError(SpiceException error) {
      super.handleError(error);
      if (view != null) view.finishLoading();
   }

   @Override
   protected Query<ArrayList<User>> getUserListQuery(int page) {
      return new GetFriendsQuery(selectedCircle, query, page, getPerPageCount());
   }

   public interface View extends BaseUserListPresenter.View {

      void showFilters(List<Circle> circles, int selectedPosition);

      void openFriendPrefs(UserBundle userBundle);
   }
}
