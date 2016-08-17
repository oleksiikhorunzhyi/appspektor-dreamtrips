package com.worldventures.dreamtrips.modules.navdrawer;

import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

import rx.Observable;

public interface NavigationDrawerView {

   <T> Observable<T> bind(Observable<T> observable);

   void setData(List<ComponentDescription> activeComponents);

   void setUser(User user);

   void setCurrentComponent(ComponentDescription newComponent);

   void setNotificationCount(int count);

   void setUnreadMessagesCount(int count);

   void setNavigationDrawerPresenter(NavigationDrawerPresenter navigationDrawerPresenter);
}
