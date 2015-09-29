package com.worldventures.dreamtrips.core.utils;


import android.content.Context;

import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.event.HeaderCountChangedEvent;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import me.leolin.shortcutbadger.ShortcutBadger;

public class BadgeCountObserver {

    private Context context;
    private SnappyRepository database;
    private EventBus eventBus;

    public BadgeCountObserver(Context context, SnappyRepository database, EventBus eventBus) {
        this.context = context.getApplicationContext();
        this.database = database;
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    public void onEvent(HeaderCountChangedEvent event){
        ShortcutBadger.with(context).count(database.getNotificationCount());
    }
}
