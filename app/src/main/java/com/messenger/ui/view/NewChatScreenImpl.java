package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.ContactCursorAdapter;
import com.messenger.ui.widget.SelectionListenerEditText;
import com.worldventures.dreamtrips.R;
import com.messenger.ui.presenter.NewChatLayoutPresenter;
import com.messenger.ui.presenter.NewChatLayoutPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.VerticalDivider;

import java.util.List;

import butterknife.InjectView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewChatScreenImpl extends BaseViewStateLinearLayout<NewChatScreen, NewChatLayoutPresenter>
        implements NewChatScreen {

    @InjectView(R.id.new_chat_content_view)
    View contentView;
    @InjectView(R.id.new_chat_loading_view)
    View loadingView;
    @InjectView(R.id.new_chat_error_view)
    View errorView;

    @InjectView(R.id.new_chat_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.new_chat_recycler_view)
    RecyclerView recyclerView;

    @InjectView(R.id.new_chat_conversation_icon)
    ImageView conversationIcon;
    @InjectView(R.id.new_chat_conversation_name)
    EditText conversationNameEditText;
    @InjectView(R.id.new_chat_chosen_contacts_edittext)
    SelectionListenerEditText chosenContactsEditText;

    private ToolbarPresenter toolbarPresenter;

    private ContactCursorAdapter adapter;

    public NewChatScreenImpl(Context context) {
        super(context);
        init(context);
    }

    public NewChatScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.screen_new_chat, this, true);
        ButterKnife.inject(this, this);
        initUi();
    }

    @SuppressWarnings("Deprecated")
    private void initUi() {
        setBackgroundColor(getResources().getColor(R.color.main_background));
        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.setTitle(R.string.new_chat_title);
        toolbarPresenter.enableUpNavigationButton();

        adapter = new ContactCursorAdapter(getContext(), null);
        adapter.setSelectionListener((selectedUsers) -> {
            setSelectedContacts(selectedUsers);
            getPresenter().onSelectedUsersStateChanged(selectedUsers);
        });

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalDivider(getResources()
                .getDrawable(R.drawable.divider_list)));

        final String chosenContactsEditTextStartValue
                = getContext().getString(R.string.new_chat_chosen_contacts_header_empty);
        resetChoseContactsEditText();
        chosenContactsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (charSequence.length() >= chosenContactsEditTextStartValue.length()) {
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
                int spannableColor = getActivity()
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
    }

    private void resetChoseContactsEditText() {
        String chosenContactsEditTextStartValue = getContext()
                .getString(R.string.new_chat_chosen_contacts_header_empty);
        chosenContactsEditText.setText(chosenContactsEditTextStartValue);
        chosenContactsEditText.setSelection(chosenContactsEditTextStartValue.length());
    }

    @Override
    public NewChatLayoutPresenter createPresenter() {
        return new NewChatLayoutPresenterImpl(getActivity());
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
    public void setContacts(Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void setContacts(Cursor cursor, String query, String queryColumn) {
        adapter.swapCursor(cursor, query, queryColumn);
    }

    @Override
    public void setSelectedContacts(List<User> selectedContacts) {

        adapter.setSelectedContacts(selectedContacts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setSelectedUsersHeaderText(CharSequence text) {
        chosenContactsEditText.setText(text);
        chosenContactsEditText.setSelection(text.length());
    }

    @Override
    public void setConversationIcon(Bitmap bitmap) {
        conversationIcon.setImageBitmap(bitmap);
    }

    @Override
    public AppCompatActivity getActivity() {
        return (AppCompatActivity) getContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return presenter.onCreateOptionsMenu(menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        NewChatLayoutPresenter presenter = getPresenter();
        if (presenter != null){
            presenter.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        getPresenter().onDestroy();
    }

    @OnClick(R.id.new_chat_conversation_icon)
    public void onConversationItemClick() {
        getPresenter().onHandleTakePictureIntent();
    }

    @Override
    public String getConversationName() {
        return conversationNameEditText.getText().toString();
    }
}
