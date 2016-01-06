package com.messenger.synchmechanism;


import com.messenger.delegate.LoaderDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.raizlabs.android.dbflow.sql.language.Delete;

import timber.log.Timber;

public class MessengerCacheSynchronizer {

    private LoaderDelegate loaderDelegate;

    public MessengerCacheSynchronizer(MessengerServerFacade messengerServerFacade, UserProcessor userProcessor) {
        this.loaderDelegate = new LoaderDelegate(messengerServerFacade, userProcessor);
    }

    public void updateCache(OnUpdatedListener listener) {
        Timber.i("Sync started %s", System.currentTimeMillis());
        loaderDelegate.synchronizeCache(synchResult -> {
            Timber.i("Sync finished %s", System.currentTimeMillis());
            listener.onUpdated(synchResult);
        });
    }

    public void clearCache() {
        deleteTable(Message.class);
        deleteTable(Conversation.class);
        deleteTable(User.class);
        deleteTable(ParticipantsRelationship.class);
    }

    private void deleteTable(Class entityClass) {
        new Delete().from(entityClass).query();
    }

    public interface OnUpdatedListener {
        void onUpdated(boolean result);
    }
}
