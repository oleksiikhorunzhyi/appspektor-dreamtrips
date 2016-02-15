package com.messenger.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataConversation$Table;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.entities.DataUser$Table;
import com.messenger.messengerservers.constant.AttachmentType;
import com.messenger.messengerservers.constant.ConversationStatus;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.ui.adapter.holder.BaseConversationViewHolder;
import com.messenger.ui.adapter.holder.CloseGroupConversationViewHolder;
import com.messenger.ui.adapter.holder.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.OneToOneConversationViewHolder;
import com.messenger.ui.adapter.holder.TripConversationViewHolder;
import com.messenger.ui.adapter.swipe.SwipeLayoutContainer;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.util.ChatDateUtils;
import com.messenger.util.MessageVersionHelper;
import com.messenger.util.ParticipantsDaoHelper;
import com.raizlabs.android.dbflow.sql.SqlUtils;
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

import static com.messenger.messengerservers.constant.ConversationType.CHAT;
import static com.messenger.messengerservers.constant.ConversationType.GROUP;
import static com.messenger.messengerservers.constant.ConversationType.TRIP;

public class ConversationsCursorAdapter
        extends CursorRecyclerViewAdapter<BaseConversationViewHolder>
        implements SwipeLayoutContainer {

    private static final int VIEW_TYPE_ONE_TO_ONE_CONVERSATION = 1;
    private static final int VIEW_TYPE_GROUP_CONVERSATION = 2;
    private static final int VIEW_TYPE_TRIP_CONVERSATION = 3;
    private static final int VIEW_TYPE_GROUP_CLOSE_CONVERSATION = 4;

    private final Context context;
    private final RecyclerView recyclerView;
    private final DataUser currentUser;

    private SwipeButtonsListener swipeButtonsListener;
    private ConversationClickListener conversationClickListener;

    private ConversationHelper conversationHelper;
    private ParticipantsDaoHelper participantsDaoHelper;
    private SimpleDateFormat todayDateFormat;
    private SimpleDateFormat moreThanTwoDaysAgoFormat;

    // for filter
    private Subscription mainSubscription;

    //
    private String selectedConversationId;

    public interface ConversationClickListener {
        void onConversationClick(DataConversation conversation);
    }

    public interface SwipeButtonsListener {
        void onDeleteButtonPressed(DataConversation conversation);

        void onMoreOptionsButtonPressed(DataConversation conversation);
    }

    public ConversationsCursorAdapter(Context context, RecyclerView recyclerView, DataUser currentUser, ParticipantsDAO participantsDAO) {
        super(null);
        this.context = context;
        this.recyclerView = recyclerView;
        this.currentUser = currentUser;

        conversationHelper = new ConversationHelper();
        participantsDaoHelper = new ParticipantsDaoHelper(participantsDAO);
        todayDateFormat = new SimpleDateFormat(context
                .getString(R.string.conversation_list_last_message_date_format_today));
        moreThanTwoDaysAgoFormat = new SimpleDateFormat(context
                .getString(R.string.conversation_list_last_message_date_format_more_than_one_day_ago));
    }

    protected boolean deleteButtonEnable(DataConversation conversation) {
        // TODO: 1/2/16 remove checking conversation type
        return conversationHelper.isGroup(conversation) && !conversationHelper.isOwner(conversation, currentUser);
    }

    @Override
    public void onBindViewHolderCursor(BaseConversationViewHolder holder, Cursor cursor) {
        DataConversation conversation = SqlUtils.convertToModel(true, DataConversation.class, cursor);
        DataMessage message = SqlUtils.convertToModel(true, DataMessage.class, cursor);
        String attachmentType = cursor.getString(cursor.getColumnIndex(ConversationsDAO.ATTACHMENT_TYPE_COLUMN));

        holder.bindConversation(conversation, selectedConversationId);
        holder.setConversationClickListener(conversationClickListener);
        holder.setSwipeButtonsListener(swipeButtonsListener);

        holder.setDeleteButtonVisibility(deleteButtonEnable(conversation));

        holder.setDate(formatLastConversationMessage(new Date(conversation.getLastActiveDate())));
        String userName = cursor.getString(cursor.getColumnIndex(DataUser$Table.USERNAME));

        setLastMessage(holder, message, userName, ConversationHelper.isGroup(conversation), attachmentType);

        //// TODO: 1/11/16 enable swipe and use comments below for future functional
        holder.getSwipeLayout().setSwipeEnabled(false);
    }

    public void setSelectedConversationId(String selectedConversationId) {
        this.selectedConversationId = selectedConversationId;
        notifyDataSetChanged();
    }

    private void setLastMessage(BaseConversationViewHolder holder, DataMessage message, String userName,
                                boolean isGroupConversation, String attachmentType) {
        boolean hasImageAttachment = TextUtils.equals(attachmentType, AttachmentType.IMAGE);
        holder.setLastMessage(hasImageAttachment ?
                createAttachmentText(message, userName) :
                createMessageText(message, userName, attachmentType, isGroupConversation));
    }

    private String createMessageText(DataMessage message, String userName, String attachmentType,
                                     boolean isGroupConversation) {
        String messageText = null;
        if (!TextUtils.isEmpty(message.getText())) {
            if (MessageVersionHelper.isUnsupported(message.getVersion(), attachmentType))
                messageText = Html.fromHtml(context.getString(R.string.chat_update_proposition)).toString();
            else messageText = message.getText();

            if (TextUtils.equals(message.getFromId(), currentUser.getId())) {
                messageText = String.format(context.getString(R.string.conversation_list_item_last_message_text_format_you),
                        messageText);
            } else if (isGroupConversation && !TextUtils.isEmpty(userName)) {
                messageText = userName + ": " + messageText;
            }
        }
        return messageText;
    }

    private String createAttachmentText(DataMessage message, String userName) {
        if (TextUtils.equals(message.getFromId(), currentUser.getId())) {
            return context.getString(R.string.conversation_list_item_last_message_image_format_you);
        } else {
            return userName + " " + context.getString(R.string.conversation_list_item_last_message_image);
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
            case VIEW_TYPE_GROUP_CLOSE_CONVERSATION:
                View closeGroupChatLayout = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_conversation_group, parent, false);
                return new CloseGroupConversationViewHolder(closeGroupChatLayout);
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
        final String type = cursor.getString(cursor.getColumnIndex(DataConversation$Table.TYPE));
        boolean abandoned = !TextUtils.equals(cursor.getString(cursor.getColumnIndex(DataConversation$Table.STATUS)), ConversationStatus.PRESENT);
        cursor.moveToPosition(previousPosition);
        switch (type) {
            case CHAT:
                return VIEW_TYPE_ONE_TO_ONE_CONVERSATION;
            case TRIP:
                return VIEW_TYPE_TRIP_CONVERSATION;
            case GROUP:
            default:
                if (abandoned) return VIEW_TYPE_GROUP_CLOSE_CONVERSATION;
                return VIEW_TYPE_GROUP_CONVERSATION;
        }
    }

    public void changeCursor(Cursor newCursor, String filter) {
        if (mainSubscription != null && !mainSubscription.isUnsubscribed()) {
            mainSubscription.unsubscribe();
        }
        //
        if (TextUtils.isEmpty(filter)) {
            closeCursorIfNeed(swapCursor(newCursor));
            return;
        }
        mainSubscription = Observable.from(SqlUtils.convertToList(DataConversation.class, newCursor))
                .flatMap(conversation -> participantsDaoHelper.obtainParticipantsStream(conversation, currentUser)
                                .first()
                                .map(users -> new Pair<>(conversation, users))
                )
                .toMap(p -> p.first, p -> p.second)
                .compose(new IoToMainComposer<>())
                .compose(RxLifecycle.bindView(recyclerView))
                .subscribe(map -> {
                    swapCursor(new FilterCursorWrapper(conversationHelper, newCursor, filter, map));
                });
    }

    private void closeCursorIfNeed(Cursor oldCursor) {
        if (oldCursor instanceof FilterCursorWrapper) {
            oldCursor = ((FilterCursorWrapper) oldCursor).getWrappedCursor();
        }

        if (oldCursor != null && oldCursor != getCursor()) oldCursor.close();
    }

    public void setConversationClickListener(ConversationClickListener conversationClickListener) {
        this.conversationClickListener = conversationClickListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Swipe layout
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public void setSwipeButtonsListener(SwipeButtonsListener swipeButtonsListener) {
        this.swipeButtonsListener = swipeButtonsListener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Search related
    ///////////////////////////////////////////////////////////////////////////

    public static class FilterCursorWrapper extends CursorWrapper {
        private int[] index;
        private int count;
        private int pos;

        public FilterCursorWrapper(ConversationHelper helper, Cursor cursor, String filter, Map<DataConversation, List<DataUser>> map) {
            super(cursor);
            filter = filter.toLowerCase();

            if (!TextUtils.isEmpty(filter)) {
                this.count = super.getCount();
                this.index = new int[this.count];
                for (int i = 0; i < this.count; i++) {
                    super.moveToPosition(i);
                    DataConversation conversation
                            = SqlUtils.convertToModel(true, DataConversation.class, cursor);
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

        private String getGroupConversationName(DataConversation conversation, List<DataUser> participants) {
            if (TextUtils.isEmpty(conversation.getSubject())) {
                return TextUtils.join(", ", Queryable.from(participants).map(DataUser::getName).toList());
            } else {
                return conversation.getSubject();
            }
        }

        private String getOneToOneConversationName(DataConversation conversation, List<DataUser> participants) {
            if (participants.isEmpty()) return "";
            else return participants.get(0).getName();
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
