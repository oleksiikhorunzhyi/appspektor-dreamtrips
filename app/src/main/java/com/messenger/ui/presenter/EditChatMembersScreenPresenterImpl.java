package com.messenger.ui.presenter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.delegate.ProfileCrosser;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.messengerservers.model.Participant;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.model.Group;
import com.messenger.ui.model.SwipDataUser;
import com.messenger.ui.util.recyclerview.Header;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.messenger.ui.view.edit_member.EditChatMembersScreen;
import com.messenger.ui.viewstate.ChatLayoutViewState;
import com.messenger.ui.viewstate.EditChatMembersViewState;
import com.messenger.ui.viewstate.LceViewState;
import com.messenger.util.UsersDataTransformer;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.core.rx.composer.NonNullFilter;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import flow.Flow;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.worldventures.dreamtrips.core.module.RouteCreatorModule.PROFILE;

public class EditChatMembersScreenPresenterImpl extends MessengerPresenterImpl<EditChatMembersScreen,
        EditChatMembersViewState> implements EditChatMembersScreenPresenter {
    private static final String ADMIN_TYPE = "Admin";
    private static final String HOST_TYPE = "Host";

    @Inject
    @Named(PROFILE)
    RouteCreator<Integer> routeCreator;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    DataUser user;

    @Inject
    ParticipantsDAO participantsDAO;
    @Inject
    ConversationsDAO conversationsDAO;

    @Inject
    ActivityWatcher activityWatcher;

    private final ProfileCrosser profileCrosser;

    private final String conversationId;

    private Observable<DataConversation> conversationObservable;
    private Observable<List<Pair<DataUser, String>>> membersObservable;

    private UsersDataTransformer usersDataTransformer;

    public EditChatMembersScreenPresenterImpl(Context context, String conversationId) {
        super(context);
        ((Injector) context.getApplicationContext()).inject(this);

        this.conversationId = conversationId;

        this.profileCrosser = new ProfileCrosser(context, routeCreator);
        usersDataTransformer = new UsersDataTransformer(getContext());
        usersDataTransformer.setSearchAdmin(true);
        usersDataTransformer.setSearchHosts(true);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewState().setLoadingState(LceViewState.LoadingState.LOADING);
        getView().showLoading();

        connectConversation();
        connectParticipants();

        activityWatcher.addOnStartStopListener(startStopAppListener);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        activityWatcher.removeOnStartStopListener(startStopAppListener);
    }


    private void connectConversation() {
        conversationObservable = conversationsDAO.getConversation(conversationId)
                .compose(new NonNullFilter<>())
                .replay(1)
                .autoConnect();
    }

    private void connectParticipants() {
        membersObservable = participantsDAO.getParticipants(conversationId);

        Observable
                .combineLatest(membersObservable, getView().getSearchObservable(), this::applyFilter)
                .zipWith(conversationObservable, (pairs, conversation) -> {
                    return new Pair<>(prepareMemberGroups(pairs, conversation), pairs.size());
                })
                .map(pairGroupAndUserCount -> {
                    List<Group<SwipDataUser>> groups = pairGroupAndUserCount.first;
                    Integer usersCount = pairGroupAndUserCount.second;
                    List<Object> items = new ArrayList<>(usersCount + groups.size());
                    for (Group<SwipDataUser> group : groups) {
                        items.add(new Header(getGroupName(group.groupName)));
                        items.addAll(group.items);
                    }
                    return new Pair<>(items, usersCount);
                })
                .compose(bindView())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemsWithUserCount -> {
                    List<Object> items = itemsWithUserCount.first;
                    // items admin of group chat is also participant
                    if (items.size() <= 1) {
                        Flow.get(getContext()).set(ConversationsPath.MASTER_PATH);
                        return;
                    }

                    EditChatMembersScreen view = getView();
                    view.showContent();
                    view.setAdapterData(items);
                    view.setTitle(String.format(getContext().getString(R.string.edit_chat_members_title), itemsWithUserCount.second));
                });
    }

    private String getGroupName(@NonNull String name) {
        switch (name) {
            case ADMIN_TYPE:
                return context.getString(R.string.edit_chat_members_admin_section);
            case HOST_TYPE:
                return context.getString(R.string.edit_chat_members_host_section);
            default:
                return name;
        }
    }

    ActivityWatcher.OnStartStopAppListener startStopAppListener = new ActivityWatcher.OnStartStopAppListener() {
        @Override
        public void onStartApplication() {
        }

        @Override
        public void onStopApplication() {
            if (getView() != null) getView().invalidateAllSwipedLayouts();
        }
    };

    @Override
    public void onNewViewState() {
        state = new EditChatMembersViewState();
        state.setLoadingState(ChatLayoutViewState.LoadingState.CONTENT);
        getView().showContent();
    }

    @Override
    public void applyViewState() {
        EditChatMembersViewState editChatMembersViewState = getViewState();
        EditChatMembersScreen view = getView();

        assert view != null;
        switch (editChatMembersViewState.getLoadingState()) {
            case LOADING:
                view.showLoading();
                break;
            case CONTENT:
                view.showContent();
                break;
            case ERROR:
                view.showError(editChatMembersViewState.getError());
                break;
        }
    }

    private List<Pair<DataUser, String>> applyFilter(List<Pair<DataUser, String>> members, CharSequence searchQuery) {
        if (TextUtils.isEmpty(searchQuery)) return members;
        String query = searchQuery.toString();
        List<Pair<DataUser, String>> result = new ArrayList<>(members.size());
        for (Pair<DataUser, String> userData : members) {
            String displayName = userData.first.getDisplayedName();
            if (displayName.matches(query)) result.add(userData);
        }
        return result;
    }

    private List<Group<SwipDataUser>> prepareMemberGroups(List<Pair<DataUser, String>> members, DataConversation conversation) {
        boolean isAdmin = TextUtils.equals(conversation.getOwnerId(), user.getId());
        boolean isTripConversation = TextUtils.equals(conversation.getType(), ConversationType.TRIP);
        Map<String, Collection<SwipDataUser>> groupedMap = Queryable.from(members)
                .groupToMap(pair -> getUserGroup(pair.first, pair.second, isTripConversation), pair -> toSwipDataUser(pair.first, pair.second, isAdmin));

        Set<String> names = groupedMap.keySet();
        List<Group<SwipDataUser>> groups = new LinkedList<>();
        for (String groupName : names) groups.add(new Group<>(groupName, groupedMap.get(groupName)));
        return groups;
    }

    private SwipDataUser toSwipDataUser(DataUser user, String affiliation, boolean isOwner) {
        return new SwipDataUser(user, isOwner && !TextUtils.equals(affiliation, Participant.Affiliation.OWNER));
    }

    private String getUserGroup(DataUser user, String affiliation, boolean isTripConversation) {
        if (TextUtils.equals(affiliation, Participant.Affiliation.OWNER)) return ADMIN_TYPE;
        else if (isTripConversation && user.isHost()) return HOST_TYPE;
        else return user.getFirstName().substring(0, 1).toLowerCase();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Menu
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getToolbarMenuRes() {
        return R.menu.menu_edit_chat_members;
    }

    @Override
    public void onDeleteUserFromChat(DataUser user) {
        getView().showDeletionConfirmationDialog(user);
    }

    @Override
    public void onDeleteUserFromChatConfirmed(DataUser user) {
        messengerServerFacade.getChatManager()
                .createMultiUserChatObservable(conversationId, this.user.getId())
                .compose(bindViewIoToMainComposer())
                .subscribe(chat -> kickUser(chat, user.getId()),
                        e -> Timber.e(e, "Failed to create chat"));
    }

    private void kickUser(MultiUserChat chat, String userId) {
        chat.kick(Collections.singletonList(userId))
                .map(users -> users.get(0))
                .doOnNext(memberId -> participantsDAO.delete(conversationId, memberId))
                .subscribe(memberId -> chat.close(),
                        e -> Timber.e(e, "Failed to delete user"));
    }

    @Override
    public void onUserClicked(DataUser user) {
        profileCrosser.crossToProfile(user);
    }
}
