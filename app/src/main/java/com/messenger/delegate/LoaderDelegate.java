package com.messenger.delegate;

import android.content.Context;
import android.util.Log;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.loaders.Loader;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.runtime.TransactionManager;
import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

public class LoaderDelegate {

    final Context context;
    final MessengerServerFacade messengerServerFacade;

    public LoaderDelegate(Context context, MessengerServerFacade messengerServerFacade) {
        this.context = context;
        this.messengerServerFacade = messengerServerFacade;
    }

    public void loadConversations(){
        Loader<Conversation> conversationLoader = messengerServerFacade.getLoaderManager().createConversationLoader();
        conversationLoader.setPersister(conversations -> {
            Log.e("Conversation loaded: ", conversations.size() + " to " + Conversation.CONTENT_URI);
            Log.e("Message is null ", Boolean.toString(conversations.get(0).getLastMessage() == null));
            ContentUtils.bulkInsert(Conversation.CONTENT_URI, Conversation.class, conversations);
        });
        conversationLoader.load();
    }

}
