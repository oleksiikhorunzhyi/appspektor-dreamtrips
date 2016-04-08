package com.messenger.ui.view.chat;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataMessage;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.MessagesCursorAdapter;
import com.messenger.ui.adapter.holder.chat.MessageHolder;
import com.messenger.ui.helper.ConversationHelper;
import com.messenger.ui.presenter.ChatScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.ui.widget.ChatUsersTypingView;
import com.messenger.util.ScrollStatePersister;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayout;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import rx.Observable;
import timber.log.Timber;

public class ChatScreenImpl extends MessengerPathLayout<ChatScreen, ChatScreenPresenter, ChatPath>
        implements ChatScreen {

    private static final int THRESHOLD = 5;

    @Inject
    PhotoPickerLayoutDelegate photoPickerLayoutDelegate;
    @Inject
    LocaleHelper localeHelper;
    @Inject
    SessionHolder<UserSession> sessionHolder;

    @InjectView(R.id.chat_content_view)
    ViewGroup contentView;
    @InjectView(R.id.chat_loading_view)
    View loadingView;
//    @InjectView(R.id.chat_error_view)
//    View errorView;

    @InjectView(R.id.chat_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.chat_toolbar_title)
    TextView title;
    @InjectView(R.id.chat_toolbar_subtitle)
    TextView subtitle;
    @InjectView(R.id.chat_recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.chat_users_typing_view)
    ChatUsersTypingView chatUsersTypingView;

    @InjectView(R.id.chat_message_edit_text)
    EditText messageEditText;
    @InjectView(R.id.chat_message_send_button)
    View sendMessageButton;
    @InjectView(R.id.input_holder)
    ViewGroup inputHolder;

    private ToolbarPresenter toolbarPresenter;

    private MessagesCursorAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ConversationHelper conversationHelper;
    private ScrollStatePersister scrollStatePersister = new ScrollStatePersister();

    public ChatScreenImpl(Context context) {
        super(context);
    }

    public ChatScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepared() {
        super.onPrepared();
        initUi();
    }

    @NonNull
    @Override
    public ChatScreenPresenter createPresenter() {
        return new ChatScreenPresenterImpl(getContext(), injector, getPath().getConversationId());
    }

    @SuppressWarnings("Deprecated")
    private void initUi() {
        ButterKnife.inject(this);
        injector.inject(this);
        //
        toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
        toolbarPresenter.attachPathAttrs(getPath().getAttrs());
        toolbarPresenter.hideBackButtonInLandscape();
        toolbarPresenter.setTitle("");
        toolbarPresenter.setSubtitle("");

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (adapter == null || adapter.getCursor() == null) return;

                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                int totalItem = linearLayoutManager.getItemCount();
                getPresenter().onLastVisibleMessageChanged(adapter.getCursor(), lastVisibleItem);

                if (dy > 0) return;

                if (firstVisibleItem <= THRESHOLD && totalItem > 0) {
                    getPresenter().onNextPageReached();
                }
            }
        });
        recyclerView.setItemAnimator(null);
        scrollStatePersister.restoreInstanceState(getLastRestoredInstanceState(), linearLayoutManager);

        initPhotoPicker();

        messageEditText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) photoPickerLayoutDelegate.hidePicker();
        });
        photoPickerLayoutDelegate.disableEditTextUntilPickerIsShown(messageEditText);
    }

    @Override
    protected void onAttachedToWindow() {
        conversationHelper = new ConversationHelper();
        super.onAttachedToWindow();
        recyclerView.setAdapter(adapter = createAdapter());
        inflateToolbarMenu(toolbar);
    }


    protected MessagesCursorAdapter createAdapter() {
        MessagesCursorAdapter adapter = new MessagesCursorAdapter(getContext(), getPresenter().getUser(),
                localeHelper.getAccountLocaleFormatted(sessionHolder.get().get().getUser()), null);
        ChatScreenPresenter presenter = getPresenter();
        adapter.setOnRepeatMessageSend(presenter::retrySendMessage);
        adapter.setAvatarClickListener(presenter::openUserProfile);
        adapter.setOnImageClickListener(presenter::onImageClicked);
        adapter.setMessageLongClickListener(presenter::onShowContextualMenu);
        adapter.setNeedMarkUnreadMessages(true);
        //adapter.setMessageClickListener(message -> //do something);
        return adapter;
    }

    @Override
    public void enableSendMessageButton(boolean enable) {
        sendMessageButton.setEnabled(enable);
    }

    @OnEditorAction(R.id.chat_message_edit_text)
    protected boolean onEditorAction(int action) {
        if (action == EditorInfo.IME_ACTION_GO) {
            onSendMessage();
            return true;
        }
        return false;
    }

    @OnClick(R.id.chat_message_send_button)
    protected void onSendMessage() {
        if (getPresenter().sendMessage(messageEditText.getText().toString())) {
            messageEditText.getText().clear();
            recyclerView.smoothScrollToPosition(adapter.getItemCount());
        }
    }

    @OnClick(R.id.chat_message_add_button)
    protected void onAttachmentButtonClicked() {
        if (photoPickerLayoutDelegate.isPanelVisible()) photoPickerLayoutDelegate.hidePicker();
        else {
            messageEditText.clearFocus();
            // put delay to prevent wrong resizing of photo picker panel
            postDelayed(() -> photoPickerLayoutDelegate.showPicker(), 400);
        }
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContent() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e) {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(DataConversation conversation, List<DataUser> members) {
        conversationHelper.setTitle(title, conversation, members, false);
        conversationHelper.setSubtitle(subtitle, conversation, members);
    }

    @Override
    public void setShowMarkUnreadMessage(boolean needShow) {
        if (adapter != null) adapter.setNeedMarkUnreadMessages(needShow);
    }

    @Override
    public void addTypingUser(DataUser user) {
        chatUsersTypingView.addTypingUser(user);
    }

    @Override
    public void removeTypingUser(DataUser user) {
        chatUsersTypingView.removeTypingUser(user);
    }

    @Override
    public void removeAllTypingUsers() {
        chatUsersTypingView.removeAllTypingUsers();
    }

    @Override
    public void showMessages(Cursor cursor, DataConversation conversation) {
        Timber.i("Show Cursor with size " + cursor.getCount());
        adapter.setConversation(conversation);

        int firstVisibleViewTop = 0;
        int cursorCountDiff = 0;
        View firstItemView = null;
        if (cursor != null && adapter.getCursor() != null) {
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            firstItemView = linearLayoutManager.findViewByPosition(firstItem);
            if (firstItemView != null) {
                firstVisibleViewTop = firstItemView.getTop();
            }
            cursorCountDiff = Math.max(0, cursor.getCount() - adapter.getCursor().getCount());
        }

        adapter.changeCursor(cursor);

        if (firstItemView != null && cursorCountDiff > 0) {
            int position = linearLayoutManager.findFirstVisibleItemPosition() + cursorCountDiff;

            // to calculate proper offset measure if first visible view will be different in height
            // (e.g. because of the missing date divider) when new cursor will is assigned
            MessageHolder messageHolder = adapter.onCreateViewHolder(recyclerView,
                    adapter.getItemViewType(position));
            adapter.onBindViewHolderCursor(messageHolder, cursor);
            messageHolder.itemView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int diffHeight = firstItemView.getHeight() - messageHolder.itemView.getMeasuredHeight();
            int offset = firstVisibleViewTop + diffHeight;

            linearLayoutManager.scrollToPositionWithOffset(position, offset);
        } else if (cursor != null && cursor.getCount() == 1) {
            getPresenter().onLastVisibleMessageChanged(cursor, 0);
        }
    }

    @Override
    public void showContextualAction(Menu menu, DataMessage message) {
        CharSequence[] menuItems = new CharSequence[menu.size()];
        for (int i = 0; i < menu.size(); i++) {
            menuItems[i] = menu.getItem(i).getTitle();
        }
        new AlertDialog.Builder(getContext()).setItems(menuItems, ((dialogInterface, i) -> {
            switch (menu.getItem(i).getItemId()) {
                case R.id.action_copy_message:
                    getPresenter().onCopyMessageTextToClipboard(message);
                    break;
                case R.id.action_translate:
                    getPresenter().onTranslateMessage(message);
                    break;
                case R.id.action_revert_translate:
                    getPresenter().onRevertTranslate(message);
                    break;
                case R.id.action_start_chat:
                    getPresenter().onStartNewChatForMessageOwner(message);
                    break;
            }
        }))
                .show();
    }

    @Override
    public Observable<TextViewTextChangeEvent> getEditMessageObservable() {
        return RxTextView.textChangeEvents(messageEditText);
    }

    @Override
    public ViewGroup getContentView() {
        return contentView;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return scrollStatePersister.saveScrollState(super.onSaveInstanceState(), linearLayoutManager);
    }

    ////////////////////////////////////////
    /////// Photo picking
    ////////////////////////////////////////

    //TODO Feb 4, 2016 Refactor this part after new picker implemented
    private PhotoPickerLayout.PhotoPickerListener photoPickerListener = new PhotoPickerLayout.PhotoPickerListener() {
        @Override
        public void onClosed() {
            MarginLayoutParams params = (MarginLayoutParams) inputHolder.getLayoutParams();
            params.bottomMargin = 0;
            inputHolder.setLayoutParams(params);
        }

        @Override
        public void onOpened() {
            MarginLayoutParams params = (MarginLayoutParams) inputHolder.getLayoutParams();
            params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.picker_panel_height);
            inputHolder.setLayoutParams(params);
        }
    };

    private void initPhotoPicker() {
        photoPickerLayoutDelegate.setOnDoneClickListener((chosenImages, type) -> this.onImagesPicked(chosenImages));
        photoPickerLayoutDelegate.setPhotoPickerListener(photoPickerListener);
    }

    @Override
    public void hidePicker() {
        photoPickerLayoutDelegate.hidePicker();
    }

    private void onImagesPicked(List<ChosenImage> images) {
        photoPickerLayoutDelegate.hidePicker();
        //
        getPresenter().onImagesPicked(images);
    }
}
