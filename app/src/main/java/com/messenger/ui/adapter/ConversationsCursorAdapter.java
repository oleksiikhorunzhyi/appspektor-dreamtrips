package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;
import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.Message;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.entities.User$Table;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.adapter.holder.BaseConversationViewHolder;
import com.messenger.ui.adapter.holder.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.OneToOneConversationViewHolder;
import com.messenger.ui.adapter.holder.TripConversationViewHolder;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.Constants;
import com.messenger.util.SwipeClickListener;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.squareup.picasso.Picasso;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscription;

import static com.messenger.messengerservers.entities.Conversation.Type.CHAT;
import static com.messenger.messengerservers.entities.Conversation.Type.GROUP;
import static com.messenger.messengerservers.entities.Conversation.Type.TRIP;

public class ConversationsCursorAdapter
        extends CursorRecyclerViewAdapter<BaseConversationViewHolder>
        implements SwipeItemMangerInterface, SwipeAdapterInterface {

    private static final int VIEW_TYPE_ONE_TO_ONE_CONVERSATION = 1;
    private static final int VIEW_TYPE_GROUP_CONVERSATION = 2;
    private static final int VIEW_TYPE_TRIP_CONVERSATION = 3;

    private static final float CLOSED_CONVERSATION_ALPHA = 0.3f;

    private final Context context;
    private final RecyclerView recyclerView;

    private User currentUser;

    private SwipeItemRecyclerMangerImpl swipeButtonsManger = new SwipeItemRecyclerMangerImpl(this);
    private SwipeButtonsListener swipeButtonsListener;
    private ClickListener clickListener;

    private ConversationHelper conversationHelper;
    private SimpleDateFormat todayDateFormat;
    private SimpleDateFormat moreThanTwoDaysAgoFormat;
    private ParticipantsDAO participantsDAO;
    private Subscription mainSubscription;

    public interface ClickListener {
        void onConversationClick(Conversation conversation);
    }

    public interface SwipeButtonsListener {
        void onDeleteButtonPressed(Conversation conversation);

        void onMoreOptionsButtonPressed(Conversation conversation);
    }

    public ConversationsCursorAdapter(Context context, RecyclerView recyclerView, User currentUser) {
        super(null);
        this.context = context;
        this.recyclerView = recyclerView;
        this.currentUser = currentUser;
        participantsDAO = new ParticipantsDAO(context);
        //
        conversationHelper = new ConversationHelper();
        todayDateFormat = new SimpleDateFormat(context
                .getString(R.string.conversation_list_last_message_date_format_today));
        moreThanTwoDaysAgoFormat = new SimpleDateFormat(context
                .getString(R.string.conversation_list_last_message_date_format_more_than_one_day_ago));
    }

    protected boolean deleteButtonEnable(Conversation conversation) {
        // TODO: 1/2/16 remove checking conversation type
        return conversationHelper.isGroup(conversation) && !conversationHelper.isOwner(conversation, currentUser);
    }

    @Override
    public void onBindViewHolderCursor(BaseConversationViewHolder holder, Cursor cursor) {
        Conversation conversation = SqlUtils.convertToModel(true, Conversation.class, cursor);
        Message message = SqlUtils.convertToModel(true, Message.class, cursor);
        setUnreadMessageCount(holder, conversation.getUnreadMessageCount());

        holder.getDeleteButton()
                .setVisibility(deleteButtonEnable(conversation) ? View.VISIBLE : View.GONE);

        String userName = cursor.getString(cursor.getColumnIndex(User$Table.USERNAME));
        setLastMessage(holder, message, userName, conversationHelper.isGroup(conversation));

        if (conversation.isAbandoned()) {
            setClosedConversationUi(holder);
        } else {
            for (int i = 0; i < holder.getContentLayout().getChildCount(); i++) {
                holder.getContentLayout().getChildAt(i).setAlpha(1f);
            }
            setUnreadMessageCount(holder, conversation.getUnreadMessageCount());
        }

        ////// TODO: 12/28/15 attach listeners in holder  !!!!!!!
        final View.OnClickListener itemViewListener = v -> {
            if (clickListener != null) {
                clickListener.onConversationClick(conversation);
            }
        };
        holder.itemView.setOnClickListener(itemViewListener);

        // init swipe layout
        swipeButtonsManger.bindView(holder.itemView, cursor.getPosition());
        //// TODO: 1/11/16 enable swipe and use comments below for future functional
        holder.getSwipeLayout().setSwipeEnabled(false);
//        holder.getSwipeLayout().addSwipeListener(new SwipeClickListener(holder.itemView,
//                itemViewListener));
//        holder.getDeleteButton().setOnClickListener(view -> {
//            if (swipeButtonsListener != null) {
//                swipeButtonsListener.onDeleteButtonPressed(conversation);
//            }
//        });
//        holder.getMoreButton().setOnClickListener(view -> {
//            if (swipeButtonsListener != null) {
//                swipeButtonsListener.onMoreOptionsButtonPressed(conversation);
//            }
//        });

        holder.updateParticipants(conversation.getId(), users -> setNameAndAvatar(holder, conversation, users));
    }

    private void setClosedConversationUi(BaseConversationViewHolder holder) {
        holder.itemView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.conversation_list_read_conversation_bg));
        for (int i = 0; i < holder.getContentLayout().getChildCount(); i++) {
            View child = holder.getContentLayout().getChildAt(i);
            if (child.getId() != R.id.conversation_last_messages_layout) {
                child.setAlpha(CLOSED_CONVERSATION_ALPHA);
            }
        }
        holder.getLastMessageDateTextView().setVisibility(View.VISIBLE);
        holder.getLastMessageDateTextView().setTextColor(ContextCompat.getColor(context,
                R.color.conversation_list_closed_conversation));
        holder.getLastMessageDateTextView().setText(R.string.conversation_list_abandoned);
        holder.getUnreadMessagesCountTextView().setVisibility(View.GONE);
    }

    private void setUnreadMessageCount(BaseConversationViewHolder holder, int unreadMessageCount) {
        if (unreadMessageCount > 0) {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.conversation_list_unread_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.VISIBLE);
            holder.getUnreadMessagesCountTextView().setText(String.valueOf(unreadMessageCount));
        } else {
            holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.conversation_list_read_conversation_bg));
            holder.getUnreadMessagesCountTextView().setVisibility(View.GONE);
        }
    }

    private void setLastMessage(BaseConversationViewHolder holder, Message lastMessage, String userName, boolean isGroupConversation) {
        if (lastMessage == null) {
            holder.getLastMessageTextView().setVisibility(View.GONE);
            holder.getLastMessageDateTextView().setVisibility(View.INVISIBLE);
            return;
        }
        holder.getLastMessageTextView().setVisibility(View.VISIBLE);
        String messageText = lastMessage.getText();
        if (lastMessage.getFromId() != null && lastMessage.getFromId().equals(currentUser.getId())) {
            messageText = String.format(context.getString(R.string.conversation_list_item_last_message_format_you), messageText);
        } else if (isGroupConversation && lastMessage.getFromId() != null) {
            messageText = userName + ": " + messageText;
        }
        holder.getLastMessageTextView().setText(messageText);
        if (lastMessage.getDate() != null) {
            holder.getLastMessageDateTextView().setVisibility(View.VISIBLE);
            holder.getLastMessageDateTextView().setTextColor(ContextCompat
                    .getColor(context, R.color.conversation_list_last_message_date));
            holder.getLastMessageDateTextView().setText(formatLastConversationMessage(lastMessage.getDate()));
        } else {
            holder.getLastMessageDateTextView().setVisibility(View.INVISIBLE);
        }
    }

    public String formatLastConversationMessage(Date date) {
        Calendar today = ChatDateUtils.getToday();

        if (date.after(today.getTime())) {
            return todayDateFormat.format(date);
        } else {
            Calendar yesterday = today;
            yesterday.roll(Calendar.DAY_OF_YEAR, false);
            if (date.after(yesterday.getTime())) {
                return context.getString(R.string.conversation_list_last_message_date_format_yesterday);
            } else {
                return moreThanTwoDaysAgoFormat.format(date);
            }
        }
    }

    private void setNameAndAvatar(BaseConversationViewHolder holder, Conversation conversation, List<User> participants) {
        if (participants == null || participants.size() == 0) return;
        //
        conversationHelper.setTitle(holder.getNameTextView(), conversation, participants);
        if (conversationHelper.isGroup(conversation)) {
            if (conversation.getType().equals(TRIP)) return;
            //
            GroupConversationViewHolder groupHolder = (GroupConversationViewHolder) holder;
            groupHolder.getGroupAvatarsView().updateAvatars(participants);
        } else {
            User addressee = participants.get(0);
            OneToOneConversationViewHolder oneToOneHolder = (OneToOneConversationViewHolder) holder;
            Picasso.with(context)
                    .load(addressee.getAvatarUrl())
                    .placeholder(Constants.PLACEHOLDER_USER_AVATAR_BIG)
                    .into(oneToOneHolder.getAvatarView());
            oneToOneHolder.getAvatarView().setOnline(addressee.isOnline());
        }
    }

    @Override
    public BaseConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ONE_TO_ONE_CONVERSATION:
                View oneToOneLayout = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_conversation_one_to_one, parent, false);
                return new OneToOneConversationViewHolder(oneToOneLayout);
            case VIEW_TYPE_GROUP_CONVERSATION:
                View groupChatLayout = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_conversation_group, parent, false);
                return new GroupConversationViewHolder(groupChatLayout);
            case VIEW_TYPE_TRIP_CONVERSATION:
                View tripChatLayout = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_conversation_trip, parent, false);
                return new TripConversationViewHolder(tripChatLayout);
        }
        throw new IllegalStateException("There is no such view type in adapter");
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = getCursor();
        int previousPosition = cursor.getPosition();

        if (!getCursor().moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        Conversation conversation = SqlUtils.convertToModel(true, Conversation.class, cursor);
        cursor.moveToPosition(previousPosition);
        switch (conversation.getType()) {
            case CHAT:
                return VIEW_TYPE_ONE_TO_ONE_CONVERSATION;
            case TRIP:
                return VIEW_TYPE_TRIP_CONVERSATION;
            case GROUP:
            default:
                return VIEW_TYPE_GROUP_CONVERSATION;
        }
    }

    public void changeCursor(Cursor newCursor, String filter) {
        if (mainSubscription != null && !mainSubscription.isUnsubscribed()) {
            mainSubscription.unsubscribe();
        }
        //
        if (TextUtils.isEmpty(filter)) {
            super.swapCursor(newCursor);
            return;
        }
        mainSubscription = Observable.from(SqlUtils.convertToList(Conversation.class, newCursor))
                .flatMap(c -> participantsDAO.getParticipants(c.getId()).first()
                        .map(cursor -> SqlUtils.convertToList(User.class, cursor))
                        .map(users -> new Pair<>(c, users))
                )
                .toMap(p -> p.first, p -> p.second)
                .compose(new IoToMainComposer<>())
                .compose(RxLifecycle.bindView(recyclerView))
                .subscribe(map -> {
                    super.swapCursor(new FilterCursorWrapper(conversationHelper, newCursor, filter, map));
                });
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Swipe layout
    ///////////////////////////////////////////////////////////////////////////

    public void setSwipeButtonsListener(SwipeButtonsListener swipeButtonsListener) {
        this.swipeButtonsListener = swipeButtonsListener;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public void openItem(int position) {
        swipeButtonsManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        swipeButtonsManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        swipeButtonsManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        swipeButtonsManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return swipeButtonsManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return swipeButtonsManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        swipeButtonsManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return swipeButtonsManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return swipeButtonsManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        swipeButtonsManger.setMode(mode);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search related
    ///////////////////////////////////////////////////////////////////////////

    public static class FilterCursorWrapper extends CursorWrapper {
        private int[] index;
        private int count;
        private int pos;

        public FilterCursorWrapper(ConversationHelper helper, Cursor cursor, String filter, Map<Conversation, List<User>> map) {
            super(cursor);
            filter = filter.toLowerCase();

            if (!TextUtils.isEmpty(filter)) {
                this.count = super.getCount();
                this.index = new int[this.count];
                for (int i = 0; i < this.count; i++) {
                    super.moveToPosition(i);
                    Conversation conversation
                            = SqlUtils.convertToModel(true, Conversation.class, cursor);
                    String conversationName;
                    if (helper.isGroup(conversation)) {
                        conversationName = getGroupConversationName(conversation, map.get(conversation));
                    } else {
                        conversationName = getOneToOneConversationName(conversation, map.get(conversation));
                    }
                    if (conversationName.toLowerCase().contains(filter)) {
                        this.index[this.pos++] = i;
                    }
                }
                this.count = this.pos;
                this.pos = 0;
                super.moveToFirst();
            } else {
                this.count = super.getCount();
                this.index = new int[this.count];
                for (int i = 0; i < this.count; i++) {
                    this.index[i] = i;
                }
            }
        }

        private String getGroupConversationName(Conversation conversation, List<User> participants) {
            if (TextUtils.isEmpty(conversation.getSubject())) {
                return TextUtils.join(", ", Queryable.from(participants).map(User::getName).toList());
            } else {
                return conversation.getSubject();
            }
        }

        private String getOneToOneConversationName(Conversation conversation, List<User> participants) {
            return participants.get(0).getName();
        }

        @Override
        public boolean move(int offset) {
            return this.moveToPosition(this.pos + offset);
        }

        @Override
        public boolean moveToNext() {
            return this.moveToPosition(this.pos + 1);
        }

        @Override
        public boolean moveToPrevious() {
            return this.moveToPosition(this.pos - 1);
        }

        @Override
        public boolean moveToFirst() {
            return this.moveToPosition(0);
        }

        @Override
        public boolean moveToLast() {
            return this.moveToPosition(this.count - 1);
        }

        @Override
        public boolean moveToPosition(int position) {
            return !(position >= this.count || position < 0) && super.moveToPosition(this.index[position]);
        }

        @Override
        public int getCount() {
            return this.count;
        }

        @Override
        public int getPosition() {
            return this.pos;
        }
    }
}
