package com.messenger.delegate;

import android.content.Context;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

public class LoaderDelegate {

    final Context context;
    final MessengerServerFacade messengerServerFacade;

    public LoaderDelegate(Context context, MessengerServerFacade messengerServerFacade) {
        this.context = context;
        this.messengerServerFacade = messengerServerFacade;
    }

    public void loadConversations() {
        Loader<Conversation> conversationLoader = messengerServerFacade.getLoaderManager().createConversationLoader();
        conversationLoader.setPersister(conversations -> {
            ContentUtils.bulkInsert(Conversation.CONTENT_URI, Conversation.class, conversations);
        });
        conversationLoader.load();
    }

    public void loadContacts() {
        Loader<User> contactLoader = messengerServerFacade.getLoaderManager().createContactLoader();
        contactLoader.setPersister(users -> {
            ContentUtils.bulkInsert(User.CONTENT_URI, User.class, users);
        });
        contactLoader.load();
    }
}
