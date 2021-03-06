package com.worldventures.dreamtrips.modules.navdrawer;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.messenger.util.UnreadConversationObservable;
import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class NavigationDrawerPresenter {

   private final SessionHolder appSessionHolder;
   private final UnreadConversationObservable unreadObservable;
   private final AuthInteractor authInteractor;
   private final UserNotificationInteractor userNotificationInteractor;

   private NavigationDrawerView navigationDrawerView;
   private DrawerLayout drawerLayout;

   private PublishSubject<Void> destroyViewStopper = PublishSubject.create();

   public NavigationDrawerPresenter(SessionHolder appSessionHolder, UnreadConversationObservable unreadObservable,
         AuthInteractor authInteractor, UserNotificationInteractor userNotificationInteractor) {
      this.appSessionHolder = appSessionHolder;
      this.unreadObservable = unreadObservable;
      this.authInteractor = authInteractor;
      this.userNotificationInteractor = userNotificationInteractor;
   }

   public void attachView(NavigationDrawerView navigationDrawerView, List<ComponentDescription> components) {
      attachView(null, navigationDrawerView, components);
   }

   public void attachView(DrawerLayout drawerLayout, NavigationDrawerView navigationDrawerView, List<ComponentDescription> components) {
      this.drawerLayout = drawerLayout;
      this.navigationDrawerView = navigationDrawerView;
      //
      navigationDrawerView.setNavigationDrawerPresenter(this);
      navigationDrawerView.setData(components);
      navigationDrawerView.setUser(appSessionHolder.get().get().user());

      userNotificationInteractor.notificationCountChangedPipe()
            .observeSuccess()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(command -> navigationDrawerView.setNotificationCount(command.getExclusiveNotificationCount()));

      unreadObservable.getObservable()
            .compose(bindView())
            .subscribe(navigationDrawerView::setUnreadMessagesCount);
      authInteractor.updateUserPipe().observe()
            .compose(new IoToMainComposer<>())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<UpdateUserCommand>()
                  .onSuccess(updateUserCommand -> navigationDrawerView.setUser(updateUserCommand.getResult())));
   }

   public void detach() {
      navigationDrawerView = null;
      onItemReselected = null;
      onItemSelected = null;
      onLogout = null;
      destroyViewStopper.onNext(null);
   }

   public void setCurrentComponent(ComponentDescription componentDescription) {
      navigationDrawerView.setCurrentComponent(componentDescription);
   }

   private Action1<ComponentDescription> onItemSelected;
   private Action1<ComponentDescription> onItemReselected;
   private Action0 onLogout;

   public void setOnItemSelected(Action1<ComponentDescription> onItemSelected) {
      this.onItemSelected = onItemSelected;
   }

   public void setOnItemReselected(Action1<ComponentDescription> onItemReselected) {
      this.onItemReselected = onItemReselected;
   }

   public void openDrawer() {
      if (drawerLayout != null) {
         drawerLayout.openDrawer(GravityCompat.START);
      }
   }

   public void setOnLogout(Action0 onLogout) {
      this.onLogout = onLogout;
   }

   void onItemSelected(ComponentDescription componentDescription) {
      if (onItemSelected != null) {
         onItemSelected.call(componentDescription);
      }
   }

   void onItemReselected(ComponentDescription componentDescription) {
      if (onItemReselected != null) {
         onItemReselected.call(componentDescription);
      }
   }

   void onLogout() {
      if (onLogout != null) {
         onLogout.call();
      }
   }

   protected <T> Observable.Transformer<T, T> bindView() {
      return input -> input.takeUntil(destroyViewStopper);
   }
}
