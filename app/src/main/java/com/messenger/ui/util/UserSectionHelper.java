package com.messenger.ui.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.model.Participant;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.model.Group;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.model.SwipeDataUser;
import com.messenger.ui.util.recyclerview.Header;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

public class UserSectionHelper {
    public static final String ADMIN_TYPE = "Admin";
    public static final String HOST_TYPE = "Host";

    private final Context context;
    private final SessionHolder<UserSession> userSessionHolder;

    public UserSectionHelper(Context context, SessionHolder<UserSession> userSessionHolder) {
        this.context = context;
        this.userSessionHolder = userSessionHolder;
    }

    public Observable.Transformer<List<DataUser>, Pair<List<Object>, Integer>> prepareItemInCheckableList(Collection<DataUser> selectedUsers) {
        return listObservable -> listObservable
                .map(dataUsers -> {
                    List<SelectableDataUser> res = new ArrayList<>(selectedUsers.size());
                    for (DataUser user : dataUsers) {
                        res.add(new SelectableDataUser(user, selectedUsers.contains(user)));
                    }
                    return res;
                })
                .map(userItems -> new Pair<>(prepareContactGroups(userItems), userItems.size()))
                .map(this::mapToItem);
    }

    private String getCurrentUserId() {
        return userSessionHolder.get().get().getUser().getUsername();
    }

    public Observable.Transformer<List<Pair<DataUser, String>>, Pair<List<Object>, Integer>> groupTransformer(
            Observable<DataConversation> collectionObservable) {
        return pairObservable -> pairObservable.withLatestFrom(collectionObservable,
                (pairs, conversation) -> new Pair<>(prepareMemberGroups(pairs, conversation), pairs.size()))
                .map(this::mapToItem);
    }

    private <T> Pair<List<Object>, Integer> mapToItem(Pair<List<Group<T>>, Integer> groupsWithPair) {
        List<Group<T>> groups = groupsWithPair.first;
        sortGroups(groups);
        Integer usersCount = groupsWithPair.second;
        return new Pair<>(prepareAdapterItems(groups, usersCount + groups.size()), usersCount);
    }

    private List<Group<SelectableDataUser>> prepareContactGroups(List<SelectableDataUser> userItems) {
        Map<String, Collection<SelectableDataUser>> groupedMap = Queryable.from(userItems)
                .groupToMap(item -> getUserGroup(item.getDataUser(), null, false));

        return convertToGroups(groupedMap);
    }

    private List<Group<SwipeDataUser>> prepareMemberGroups(List<Pair<DataUser, String>> members,
                                                           DataConversation conversation) {
        boolean isAdmin = TextUtils.equals(conversation.getOwnerId(), getCurrentUserId());
        boolean isTripConversation = ConversationHelper.isTripChat(conversation);
        Map<String, Collection<SwipeDataUser>> groupedMap = Queryable.from(members)
                .groupToMap(pair -> getUserGroup(pair.first, pair.second, isTripConversation),
                        pair -> toSwipeDataUser(pair.first, pair.second, isAdmin));

        return convertToGroups(groupedMap);
    }

    private <T> List<Group<T>> convertToGroups(Map<String, Collection<T>> groupedMap) {
        Set<String> names = groupedMap.keySet();
        List<Group<T>> groups = new LinkedList<>();
        for (String groupName : names) groups.add(new Group<>(groupName, groupedMap.get(groupName)));
        return groups;
    }

    private <T> void sortGroups(List<Group<T>> groups) {
        Collections.sort(groups, (lhs, rhs) -> {
            if (TextUtils.equals(lhs.groupName, ADMIN_TYPE)) return -1;
            if (TextUtils.equals(rhs.groupName, ADMIN_TYPE)) return 1;

            if (TextUtils.equals(lhs.groupName, HOST_TYPE)) return -1;
            if (TextUtils.equals(rhs.groupName, HOST_TYPE)) return 1;

            return lhs.groupName.compareTo(rhs.groupName);
        });
    }

    private <T> List<Object> prepareAdapterItems(List<Group<T>> groups, int capacity) {
        List<Object> items = new ArrayList<>(capacity);
        for (Group<T> group : groups) {
            items.add(new Header(getGroupName(group.groupName)));
            items.addAll(group.items);
        }
        return items;
    }

    private String getGroupName(@NonNull String name) {
        switch (name) {
            case UserSectionHelper.ADMIN_TYPE:
                return context.getString(R.string.edit_chat_members_admin_section);
            case UserSectionHelper.HOST_TYPE:
                return context.getString(R.string.edit_chat_members_host_section);
            default:
                return name;
        }
    }

    private SwipeDataUser toSwipeDataUser(DataUser user, String affiliation, boolean isOwner) {
        return new SwipeDataUser(user, isOwner && !TextUtils.equals(affiliation, Participant.Affiliation.OWNER));
    }

    private String getUserGroup(DataUser user, String affiliation, boolean isTripConversation) {
        if (TextUtils.equals(affiliation, Participant.Affiliation.OWNER)) return ADMIN_TYPE;
        else if (isTripConversation && user.isHost()) return HOST_TYPE;
        else return user.getFirstName().substring(0, 1).toUpperCase();
    }
}
