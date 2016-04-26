package com.messenger.synchmechanism;


import com.messenger.delegate.LoaderDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataParticipant;
import com.messenger.entities.DataUser;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.raizlabs.android.dbflow.sql.language.Delete;

import timber.log.Timber;

public class MessengerCacheSynchronizer {

    private LoaderDelegate loaderDelegate;

    public MessengerCacheSynchronizer(LoaderDelegate loaderDelegate) {
        this.loaderDelegate = loaderDelegate;
    }

    public void updateCache(OnUpdatedListener listener) {
        Timber.i("Sync started :: at %s", System.currentTimeMillis());
        loaderDelegate.synchronizeCache(syncResult -> {
            Timber.i("Sync finished %s :: at %s", syncResult ? "ok" : "failed", System.currentTimeMillis());
            listener.onUpdated(syncResult);
        });
    }

    public void clearCache() {
        deleteTable(DataMessage.class);
        deleteTable(DataConversation.class);
        deleteTable(DataUser.class);
        deleteTable(DataParticipant.class);
    }

    private void deleteTable(Class entityClass) {
        new Delete().from(entityClass).query();
    }

    public interface OnUpdatedListener {
        void onUpdated(boolean result);
    }
}
