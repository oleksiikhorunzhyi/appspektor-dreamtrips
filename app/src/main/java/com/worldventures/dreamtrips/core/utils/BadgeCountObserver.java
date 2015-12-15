package com.worldventures.dreamtrips.core.utils;


import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;

import de.greenrobot.event.EventBus;

public class BadgeCountObserver {

    private BadgeUpdater badgeUpdater;
    private SnappyRepository database;
    private EventBus eventBus;

    public BadgeCountObserver(BadgeUpdater updater, SnappyRepository database, EventBus eventBus) {
        this.badgeUpdater = updater;
        this.database = database;
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    public void onEvent(HeaderCountChangedEvent event) {
        badgeUpdater.updateBadge(database.getBadgeNotificationsCount());
    }
}
