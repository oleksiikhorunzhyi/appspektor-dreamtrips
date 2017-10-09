package com.worldventures.dreamtrips.core.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.core.utils.BadgeUpdater;
import com.worldventures.dreamtrips.modules.common.service.UserNotificationInteractor;

import javax.inject.Inject;


public class BadgeCountObserverInitializer implements AppInitializer {

   @Inject UserNotificationInteractor userNotificationInteractor;
   @Inject BadgeUpdater badgeUpdater;

   @Override
   public void initialize(Injector injector) {
      injector.inject(this);
      userNotificationInteractor.notificationCountChangedPipe()
            .observeSuccess()
            .subscribe(command -> badgeUpdater.updateBadge(command.getBadgeNotificationsCount()));
   }
}
