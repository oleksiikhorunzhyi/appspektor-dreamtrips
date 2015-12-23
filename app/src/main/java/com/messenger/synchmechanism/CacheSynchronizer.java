package com.messenger.synchmechanism;


import com.messenger.delegate.LoaderDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

public class CacheSynchronizer {

    private LoaderDelegate loaderDelegate;

    public CacheSynchronizer(MessengerServerFacade messengerServerFacade, DreamSpiceManager spiceManager) {
        this.loaderDelegate = new LoaderDelegate(messengerServerFacade, spiceManager);
    }

    public void updateCache(OnUpdatedListener listener) {
        loaderDelegate.synchronizeCache(listener::onUpdated);
    }

    public void clearCache() {
        deleteTable(Message.class);
        deleteTable(Conversation.class);
        deleteTable(User.class);
        deleteTable(ParticipantsRelationship.class);
    }

    private void deleteTable(Class entityClass){
        new Delete().from(entityClass).query();
    }

    public interface OnUpdatedListener{
        void onUpdated(boolean result);
    }
}
