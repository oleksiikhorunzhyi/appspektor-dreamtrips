package com.messenger.ui.view.add_member;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.messenger.entities.DataUser;
import com.messenger.flow.path.StyledPath;
import com.messenger.ui.adapter.CheckableContactsCursorAdapter;
import com.messenger.ui.anim.WeightSlideAnimator;
import com.messenger.ui.presenter.ChatMembersScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.ui.widget.SelectionListenerEditText;
import com.messenger.util.ScrollStatePersister;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class ChatMembersScreenImpl<P extends StyledPath>
        extends MessengerPathLayout<ChatMembersScreen, ChatMembersScreenPresenter, P>
        implements ChatMembersScreen {

    @InjectView(R.id.new_chat_content_view)
    ViewGroup contentView;
    @InjectView(R.id.new_chat_loading_view)
    View loadingView;
    @InjectView(R.id.new_chat_error_view)
    View errorView;

    @InjectView(R.id.new_chat_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.new_chat_recycler_view)
    RecyclerView recyclerView;

    @InjectView(R.id.new_chat_conversation_name_layout)
    View conversationNameEditTextLayout;
    @InjectView(R.id.new_chat_conversation_name)
    EditText conversationNameEditText;
    @InjectView(R.id.new_chat_chosen_contacts_edittext)
    SelectionListenerEditText chosenContactsEditText;

    private ToolbarPresenter toolbarPresenter;

    private CheckableContactsCursorAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ScrollStatePersister scrollStatePersister = new ScrollStatePersister();

    private WeightSlideAnimator conversationNameAnimator;

    public ChatMembersScreenImpl(Context context) {
        super(context);
    }

    public ChatMembersScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepared() {
        super.onPrepared();
        initUi();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inflateToolbarMenu(toolbar);
    }

    @SuppressWarnings("Deprecated")
    private void initUi() {
        ButterKnife.inject(this, this);
        toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
        toolbarPresenter.attachPathAttrs(getPath().getAttrs());

        adapter = new CheckableContactsCursorAdapter(getContext(), null);
        adapter.setAvatarClickListener(user -> getPresenter().openUserProfile(user));
        adapter.setSelectionListener((selectedUsers) -> {
            setSelectedContacts(selectedUsers);
            getPresenter().onSelectedUsersStateChanged(selectedUsers);
        });

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalDivider(getResources()
                .getDrawable(R.drawable.divider_list)));
        scrollStatePersister.restoreInstanceState(getLastRestoredInstanceState(), linearLayoutManager);

        final String chosenContactsEditTextStartValue
                = getContext().getString(R.string.new_chat_chosen_contacts_header_empty);
        resetChoseContactsEditText();
        chosenContactsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= chosenContactsEditTextStartValue.length()
                        && getPresenter() != null) {
                    getPresenter().onTextChangedInChosenContactsEditText(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() <= chosenContactsEditTextStartValue.length()) {
                    if (editable.length() < chosenContactsEditTextStartValue.length()) {
                        resetChoseContactsEditText();
                    }
                    return;
                }
                int spannableColor = getContext()
                        .getResources().getColor(R.color.contact_list_header_selected_contacts);
                editable.setSpan(new UnderlineSpan(), editable.length() - 1,
                        editable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editable.setSpan(new ForegroundColorSpan(spannableColor),
                        editable.length() - 1,
                        editable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });
        chosenContactsEditText.setSelectionListener((s, a)
                -> chosenContactsEditText.setSelection(chosenContactsEditText.getText().length()));
        conversationNameAnimator =
                new WeightSlideAnimator(conversationNameEditTextLayout);
    }

    @Override
    public void setConversationNameEditTextVisibility(int visibility) {
        conversationNameEditTextLayout.setVisibility(visibility);
    }

    @Override
    public void slideInConversationNameEditText() {
        conversationNameAnimator.slideIn();
    }

    @Override
    public void slideOutConversationNameEditText() {
        conversationNameAnimator.slideOut();
    }

    private void resetChoseContactsEditText() {
        String chosenContactsEditTextStartValue = getContext()
                .getString(R.string.new_chat_chosen_contacts_header_empty);
        chosenContactsEditText.setText(chosenContactsEditTextStartValue);
        chosenContactsEditText.setSelection(chosenContactsEditTextStartValue.length());
    }

    @Override
    public void showLoading() {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e) {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public ViewGroup getContentView() {
        return contentView;
    }

    @Override
    public void setContacts(Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void setContacts(Cursor cursor, String query, String queryColumn) {
        adapter.swapCursor(cursor, query, queryColumn);
    }

    @Override
    public void setSelectedContacts(List<DataUser> selectedContacts) {
        adapter.setSelectedContacts(selectedContacts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setSelectedUsersHeaderText(CharSequence text) {
        chosenContactsEditText.setText(text);
        chosenContactsEditText.setSelection(text.length());
    }

    @Override
    public void setTitle(String title) {
        toolbarPresenter.setTitle(title);
    }

    @Override
    public void setTitle(@StringRes int title) {
        toolbarPresenter.setTitle(title);
    }

    @Override
    public String getConversationName() {
        return conversationNameEditText.getText().toString();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return scrollStatePersister.saveScrollState(super.onSaveInstanceState(),
                linearLayoutManager);
    }
}
