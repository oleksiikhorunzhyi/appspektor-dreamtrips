package com.worldventures.dreamtrips.core.utils;


import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;

public class BadgeCountObserver {

   public BadgeCountObserver(BadgeUpdater updater, SnappyRepository database, NotificationCountEventDelegate notificationCountEventDelegate) {
      notificationCountEventDelegate.getObservable().subscribe(event -> {
         updater.updateBadge(database.getBadgeNotificationsCount());
      });
   }
}
