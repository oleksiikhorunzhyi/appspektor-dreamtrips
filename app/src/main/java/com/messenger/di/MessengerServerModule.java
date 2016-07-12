package com.messenger.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.messenger.delegate.conversation.LoadConversationDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.xmpp.XmppGlobalEventEmitter;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.messengerservers.xmpp.XmppServerParams;
import com.messenger.messengerservers.xmpp.filter.incoming.AbandonedConversationMessageFilter;
import com.messenger.messengerservers.xmpp.filter.incoming.GeneralIncomingMessageFilter;
import com.messenger.messengerservers.xmpp.filter.incoming.IncomingMessageFilter;
import com.messenger.messengerservers.xmpp.filter.incoming.IncomingMessageFilterType;
import com.messenger.messengerservers.xmpp.providers.GsonAttachmentAdapter;
import com.worldventures.dreamtrips.BuildConfig;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class MessengerServerModule {

    @Singleton
    @Provides
    MessengerServerFacade provideXmppServerFacade(Set<IncomingMessageFilter> incomingMessageFilters) {
        Gson gson = new GsonBuilder().registerTypeAdapter(AttachmentHolder.class, new GsonAttachmentAdapter()).create();

        Map<IncomingMessageFilterType, List<IncomingMessageFilter>> filters = new EnumMap<>(IncomingMessageFilterType.class);
        filters.put(IncomingMessageFilterType.MESSAGE, new ArrayList<>(incomingMessageFilters));

        XmppGlobalEventEmitter emitter = new XmppGlobalEventEmitter(filters);

        return new XmppServerFacade(new XmppServerParams(BuildConfig.MESSENGER_API_URL,
                BuildConfig.MESSENGER_API_PORT), emitter, gson);
    }

    @Provides(type = Provides.Type.SET)
    IncomingMessageFilter provideGeneralIncomingMessageFilter() {
        return new GeneralIncomingMessageFilter();
    }

    @Provides(type = Provides.Type.SET)
    IncomingMessageFilter provideAbandonedConversationMessageFilter(LoadConversationDelegate loadConversationDelegate) {
        return new AbandonedConversationMessageFilter(loadConversationDelegate);
    }
}
