package com.worldventures.dreamtrips.modules.navdrawer;

import com.worldventures.core.component.ComponentDescription;
import com.worldventures.core.model.User;

import java.util.List;

public interface NavigationDrawerView {

   void setData(List<ComponentDescription> activeComponents);

   void setUser(User user);

   void setCurrentComponent(ComponentDescription newComponent);

   void setNotificationCount(int count);

   void setUnreadMessagesCount(int count);

   void setNavigationDrawerPresenter(NavigationDrawerPresenter navigationDrawerPresenter);
}
