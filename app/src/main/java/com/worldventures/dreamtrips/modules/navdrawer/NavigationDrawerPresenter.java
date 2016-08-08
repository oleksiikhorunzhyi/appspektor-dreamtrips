package com.worldventures.dreamtrips.modules.navdrawer;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.UpdateUserInfoEvent;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class NavigationDrawerPresenter {

    @Inject protected SessionHolder<UserSession> appSessionHolder;
    @Inject protected SnappyRepository db;
    @Inject @Global protected EventBus eventBus;
    @Inject protected UnreadConversationObservable unreadObservable;
    @Inject NotificationCountEventDelegate notificationCountEventDelegate;

    private NavigationDrawerView navigationDrawerView;
    private DrawerLayout drawerLayout;

    private PublishSubject<Void> destroyViewStopper = PublishSubject.create();

    public NavigationDrawerPresenter() {
    }

    public NavigationDrawerPresenter(Injector injector) {
        injector.inject(this);
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

        eventBus.register(this);

        notificationCountEventDelegate.getObservable()
                .compose(bindView())
                .subscribe(event -> updateNotificationsCount());
        unreadObservable.getObservable()
                .compose(bindView())
                .subscribe(navigationDrawerView::setUnreadMessagesCount);
    }

    public void detach() {
        navigationDrawerView = null;
        onItemReselected = null;
        onItemSelected = null;
        onLogout = null;
        eventBus.unregister(this);
        destroyViewStopper.onNext(null);
    }

    public void onEventMainThread(UpdateUserInfoEvent event) {
        navigationDrawerView.setUser(appSessionHolder.get().get().getUser());
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
