package com.messenger.delegate;

import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.api.GetShortProfilesQuery;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.ConversationWithParticipants;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.ParticipantsRelationship;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.OnLoadedListener;
import com.messenger.messengerservers.loaders.Loader;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.utils.TextUtils;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

import static com.innahema.collections.query.queriables.Queryable.from;

//todo check for leak
public class LoaderDelegate {
    private final MessengerServerFacade messengerServerFacade;
    private final DreamSpiceManager requester;

    public LoaderDelegate(MessengerServerFacade messengerServerFacade, DreamSpiceManager requester) {
        this.messengerServerFacade = messengerServerFacade;
        this.requester = requester;
    }

    public void synchronizeCache(@NotNull OnSynchronised listener) {
        Observable<Boolean> loadContacts = Observable.<Boolean>create(subscriber ->
                loadContacts(createLoaderListener(subscriber)));

        Observable<Boolean> loadConversations = Observable.<Boolean>create(subscriber ->
                loadConversations(createLoaderListener(subscriber)));

        Observable.combineLatest(loadContacts, loadConversations, (o, o2) -> o && o2)
                .subscribe(result -> {
                    listener.onSynchronized(result);
                });
    }

    private OnLoadedListener createLoaderListener(Subscriber subscriber) {
        return new OnLoadedListener() {
            @Override
            public void onLoaded(List entities) {
                if (subscriber.isUnsubscribed()) return;
                subscriber.onNext(true);
                subscriber.onCompleted();
            }

            @Override
            public void onError(Exception e) {
                if (subscriber.isUnsubscribed()) return;
                subscriber.onNext(false);
                subscriber.onCompleted();
            }
        };
    }

    public void loadConversations(@Nullable OnLoadedListener listener) {
        Loader<ConversationWithParticipants> conversationLoader = messengerServerFacade.getLoaderManager().createConversationLoader();
        conversationLoader.setPersister(data -> {
            // save convs
            List<Conversation> convs = from(data).map(d -> d.conversation).toList();
            List<Message> messages = from(data).map(c -> c.lastMessage).notNulls().toList();
            ContentUtils.bulkInsert(Conversation.CONTENT_URI, Conversation.class, convs);
            ContentUtils.bulkInsert(Message.CONTENT_URI, Message.class, messages);
            // save relationships
            List<ParticipantsRelationship> relationships = data.isEmpty() ? Collections.emptyList() : from(data)
                    .mapMany(d -> from(d.participants).map(p -> new ParticipantsRelationship(d.conversation.getId(), p)))
                    .toList();
            ContentUtils.bulkInsert(ParticipantsRelationship.CONTENT_URI, ParticipantsRelationship.class, relationships);
            // save users
            List<User> users = data.isEmpty() ? Collections.emptyList() : from(data).mapMany(d -> d.participants).distinct().toList();
            updateUsersViaApi(users);
        });
        if (listener != null) conversationLoader.setOnEntityLoadedListener(listener);
        conversationLoader.load();
    }

    public void loadContacts(@Nullable OnLoadedListener listener) {
        Loader<User> contactLoader = messengerServerFacade.getLoaderManager().createContactLoader();
        contactLoader.setPersister(this::updateUsersViaApi);
        if (listener != null) contactLoader.setOnEntityLoadedListener(listener);
        contactLoader.load();
    }

    private void updateUsersViaApi(List<User> users) {
        List<String> usernames = Queryable.from(users).map(User::getUserName).toList();
        requester.execute(new GetShortProfilesQuery(usernames), userz -> {
            Collections.sort(users, (lhs, rhs) -> lhs.getId().compareTo(rhs.getId()));
            Collections.sort(userz, (lhs, rhs) -> lhs.getUsername().compareTo(rhs.getUsername()));
            from(users).zip(userz, (u, z) -> {
                u.setSocialId(z.getId());
                u.setName(TextUtils.join(" ", z.getFirstName(), z.getLastName()));
                u.setAvatarUrl(z.getAvatar() == null ? null : z.getAvatar().getThumb());
                u.save();
                return u;
            }).toList();
        }, spiceException -> Timber.w(spiceException, "Can't get users by usernames"));
    }

    public interface OnSynchronised {
        void onSynchronized(boolean synchResult);
    }
}
