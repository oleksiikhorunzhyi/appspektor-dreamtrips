package com.worldventures.dreamtrips.modules.navdrawer;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class NavigationDrawerPresenter {

   private final SessionHolder appSessionHolder;
   private final SnappyRepository db;
   private final UnreadConversationObservable unreadObservable;
   private final AuthInteractor authInteractor;
   private final NotificationCountEventDelegate notificationCountEventDelegate;

   private NavigationDrawerView navigationDrawerView;
   private DrawerLayout drawerLayout;

   private PublishSubject<Void> destroyViewStopper = PublishSubject.create();

   public NavigationDrawerPresenter(SessionHolder appSessionHolder, SnappyRepository db,
         UnreadConversationObservable unreadObservable, AuthInteractor authInteractor,
         NotificationCountEventDelegate notificationCountEventDelegate) {
      this.appSessionHolder = appSessionHolder;
      this.db = db;
      this.unreadObservable = unreadObservable;
      this.authInteractor = authInteractor;
      this.notificationCountEventDelegate = notificationCountEventDelegate;
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
      navigationDrawerView.setUser(appSessionHolder.get().get().getUser());

      notificationCountEventDelegate.getObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(event -> updateNotificationsCount());

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

   public void updateNotificationsCount() {
      navigationDrawerView.setNotificationCount(db.getExclusiveNotificationsCount());
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
      if (drawerLayout != null) drawerLayout.openDrawer(GravityCompat.START);
   }

   public void setOnLogout(Action0 onLogout) {
      this.onLogout = onLogout;
   }

   void onItemSelected(ComponentDescription componentDescription) {
      if (onItemSelected != null) onItemSelected.call(componentDescription);
   }

   void onItemReselected(ComponentDescription componentDescription) {
      if (onItemReselected != null) onItemReselected.call(componentDescription);
   }

   void onLogout() {
      if (onLogout != null) onLogout.call();
   }

   protected <T> Observable.Transformer<T, T> bindView() {
      return input -> input.takeUntil(destroyViewStopper);
   }
}
