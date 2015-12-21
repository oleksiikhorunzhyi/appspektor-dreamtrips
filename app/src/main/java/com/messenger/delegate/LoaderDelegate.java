package com.messenger.delegate;

import android.content.Context;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.api.GetShortProfilesQuery;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ConversationWithParticipants;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

//todo check for leak
public class LoaderDelegate {
    private final MessengerServerFacade messengerServerFacade;
    private final DreamSpiceManager requester;

    public LoaderDelegate(Context context, MessengerServerFacade messengerServerFacade, DreamSpiceManager requester) {
        this.messengerServerFacade = messengerServerFacade;
        this.requester = requester;
    }

    public void loadConversations() {
        Loader<ConversationWithParticipants> conversationLoader = messengerServerFacade.getLoaderManager().createConversationLoader();
        conversationLoader.setPersister(data -> {
            if (data == null || data.size() == 0) return;

            // save convs
            List<Conversation> convs = from(data).map(d -> d.conversation).toList();
            List<Message> messages = from(data).map(c -> c.lastMessage).toList();

            ContentUtils.bulkInsert(Conversation.CONTENT_URI, Conversation.class, convs);
            ContentUtils.bulkInsert(Message.CONTENT_URI, Message.class, messages);
            // save relationships
            List<ParticipantsRelationship> relationships = from(data)
                    .mapMany(d -> from(d.participants).map(p -> new ParticipantsRelationship(d.conversation.getId(), p)))
                    .toList();
            ContentUtils.bulkInsert(ParticipantsRelationship.CONTENT_URI, ParticipantsRelationship.class, relationships);
            // save users
            List<User> users = data.isEmpty() ? new ArrayList<>() : from(data).mapMany(d -> d.participants).distinct().toList();
            updateUsersViaApi(users);
        });
        conversationLoader.load();
    }

    public void loadContacts() {
        Loader<User> contactLoader = messengerServerFacade.getLoaderManager().createContactLoader();
        contactLoader.setPersister(this::updateUsersViaApi);
        contactLoader.load();
    }

    private void updateUsersViaApi(List<User> users) {
        List<String> usernames = Queryable.from(users).map(User::getUserName).toList();
        requester.execute(new GetShortProfilesQuery(usernames), userz -> {
            Collections.sort(users, (lhs, rhs) -> lhs.getId().compareTo(rhs.getId()));
            Collections.sort(userz, (lhs, rhs) -> lhs.getUsername().compareTo(rhs.getUsername()));
            from(users).zip(userz, (u, z) -> {
                u.setName(TextUtils.join(" ", z.getFirstName(), z.getLastName()));
                u.setAvatarUrl(z.getAvatar() == null ? null : z.getAvatar().getThumb());
                u.save();
                return u;
            }).toList();
        }, spiceException -> Timber.w(spiceException, "Can't get users by usernames"));
    }
}
