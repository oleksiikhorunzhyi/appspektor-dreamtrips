package com.messenger.ui.view.add_member;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.messenger.flow.path.StyledPath;
import com.messenger.ui.adapter.cell.CheckableUserCell;
import com.messenger.ui.adapter.cell.HeaderCell;
import com.messenger.ui.anim.WeightSlideAnimator;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.presenter.ChatMembersScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.Header;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.ui.widget.SelectionListenerEditText;
import com.messenger.util.ScrollStatePersister;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;

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

    private BaseDelegateAdapter<Object> adapter;
    private LinearLayoutManager linearLayoutManager;
    private ScrollStatePersister scrollStatePersister = new ScrollStatePersister();

    private WeightSlideAnimator conversationNameAnimator;
    private Observable<CharSequence> chosenObservable;

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

    private BaseDelegateAdapter<Object> createAdapter() {
        BaseDelegateAdapter<Object> adapter = new BaseDelegateAdapter<>(getContext(), injector);
        adapter.registerCell(SelectableDataUser.class, CheckableUserCell.class);
        adapter.registerCell(Header.class, HeaderCell.class);
        adapter.registerDelegate(SelectableDataUser.class, new CheckableUserCell.Delegate() {
            @Override
            public void onCellClicked(SelectableDataUser model) {
                getPresenter().openUserProfile(model.getDataUser());
            }

            @Override
            public void onItemSelectChanged(SelectableDataUser item) {
                getPresenter().onItemSelectChange(item);
            }
        });
        return adapter;
    }

    private void initUi() {
        ButterKnife.inject(this, this);
        toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
        toolbarPresenter.attachPathAttrs(getPath().getAttrs());

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter = createAdapter());
        recyclerView.addItemDecoration(new VerticalDivider(ContextCompat.getDrawable(getContext(), R.drawable.divider_list)));
        scrollStatePersister.restoreInstanceState(getLastRestoredInstanceState(), linearLayoutManager);

        chosenObservable = RxTextView.textChanges(chosenContactsEditText);
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
    public void setAdapterItems(List<Object> item) {
        adapter.setItems(item);
    }

    @Override
    public Observable<CharSequence> getChosenObservable() {
        return chosenObservable;
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
