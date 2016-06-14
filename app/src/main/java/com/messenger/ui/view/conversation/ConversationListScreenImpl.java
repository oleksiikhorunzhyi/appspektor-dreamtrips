package com.messenger.ui.view.conversation;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;

import com.messenger.entities.DataConversation;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.messenger.ui.adapter.ConversationsCursorAdapter;
import com.messenger.ui.adapter.swipe.SwipeableAdapterManager;
import com.messenger.ui.presenter.ConversationListScreenPresenter;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.util.ScrollStatePersister;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.messenger.ui.adapter.ConversationsCursorAdapter.SwipeButtonsListener;
import static com.messenger.ui.presenter.ConversationListScreenPresenter.ChatTypeItem;

public class ConversationListScreenImpl extends MessengerPathLayout<ConversationListScreen,
        ConversationListScreenPresenter, StyledPath> implements ConversationListScreen, SwipeButtonsListener {

    @InjectView(R.id.conversation_list_content_view)
    ViewGroup contentView;

    @InjectView(R.id.conversation_list_loading_view)
    View loadingView;
    @InjectView(R.id.conversation_list_error_view)
    View errorView;

    @InjectView(R.id.conversation_list_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.conversation_list_recycler_view)
    RecyclerView recyclerView;
    @InjectView(R.id.conversation_conversation_type_spinner)
    Spinner conversationsDropDownSpinner;

    SearchView searchView;

    private ConversationsCursorAdapter adapter;
    private SwipeableAdapterManager swipeableAdapterManager = new SwipeableAdapterManager();

    private LinearLayoutManager linearLayoutManager;
    private ScrollStatePersister scrollStatePersister = new ScrollStatePersister();

    public ConversationListScreenImpl(Context context) {
        super(context);
    }

    public ConversationListScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepared() {
        super.onPrepared();
        initUi();
    }

    @NonNull
    @Override
    public ConversationListScreenPresenter createPresenter() {
        return new ConversationListScreenPresenterImpl(getContext(), injector);
    }

    @Override
    public void setPresenter(ConversationListScreenPresenter presenter) {
        super.setPresenter(presenter);
        setAdapters();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (inflateToolbarMenu(toolbar)) {
            prepareToolbarMenu(toolbar.getMenu());
        }
    }

    private void initUi() {
        ButterKnife.inject(this);
        injector.inject(this);
        //
        ToolbarPresenter toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
        injector.inject(toolbarPresenter);
        //
        toolbarPresenter.attachPathAttrs(getPath().getAttrs());
        toolbarPresenter.disableTitle();
        toolbarPresenter.hideBackButtonInLandscape();

        conversationsDropDownSpinner.setAdapter(createSpinnerAdapter());
        conversationsDropDownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                getPresenter().onConversationsDropdownSelected((ChatTypeItem) adapterView.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void setSelectedConversationId(String conversationId) {
        if (ViewUtils.isTablet(getContext()) && ViewUtils.isLandscapeOrientation(getContext())) {
            adapter.setSelectedConversationId(conversationId);
        } else {
            adapter.setSelectedConversationId(null);
        }
    }

    protected BaseAdapter createSpinnerAdapter() {
        Resources res = getResources();
        List<ChatTypeItem> listItems = Arrays.asList(
                new ChatTypeItem(ChatTypeItem.ALL_CHATS, res.getString(R.string.conversation_list_spinner_item_all_chats)),
                new ChatTypeItem(ChatTypeItem.GROUP_CHATS, res.getString(R.string.conversation_list_spinner_item_group_chats))
        );
        ArrayAdapter<ChatTypeItem> adapter = new ArrayAdapter<ChatTypeItem>(getContext(),
                R.layout.spinner_item_action_bar, listItems) {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                CheckedTextView textView = (CheckedTextView) super.getDropDownView(position, convertView, parent);
                textView.setChecked(conversationsDropDownSpinner.getSelectedItemPosition() == position);
                return textView;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_action_bar);
        return adapter;
    }

    private void setAdapters() {
        adapter = new ConversationsCursorAdapter();
        adapter.setSwipeButtonsListener(this);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new VerticalDivider(ContextCompat.getDrawable(getContext(), R.drawable.divider_list)));
        adapter.setConversationClickListener(getPresenter()::onConversationSelected);
        recyclerView.setAdapter(swipeableAdapterManager.wrapAdapter(adapter));
        //
        scrollStatePersister.restoreInstanceState(getLastRestoredInstanceState(), linearLayoutManager);
    }

    @Override
    public void showLoading() {
        recyclerView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showContent() {
        recyclerView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showError(Throwable e) {
        recyclerView.setVisibility(View.GONE);
        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showConversations(Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onDeleteButtonPressed(DataConversation conversation) {
        getPresenter().onDeleteButtonPressed(conversation);
    }

    @Override
    public void showConversationDeletionConfirmationDialog(DataConversation conversation) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.conversation_list_delete_dialog_message)
                .setPositiveButton(R.string.conversation_list_delete_dialog_pos_button,
                        (d, i) -> {
                            presenter.onDeletionConfirmed(conversation);
                            swipeableAdapterManager.closeAllItems();
                        })
                .setNeutralButton(R.string.action_cancel, (d, i) -> swipeableAdapterManager.closeAllItems())
                .show();
    }

    @Override
    public void onMoreOptionsButtonPressed(DataConversation conversation) {
        getPresenter().onMoreOptionsButtonPressed(conversation);
    }

    @Override
    public void showConversationMoreActionsDialog(DataConversation conversation) {
        new AlertDialog.Builder(getContext())
                .setItems(R.array.conversation_list_more_actions, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            getPresenter().onMarkAsUnreadButtonPressed(conversation);
                            break;
                        case 1:
                            getPresenter().onTurnOffNotificationsButtonPressed(conversation);
                            break;
                    }
                    swipeableAdapterManager.closeAllItems();
                })
                .show();
    }

    @Override
    protected ViewGroup getContentView() {
        return contentView;
    }

    private void prepareToolbarMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    TrackingHelper.conversationSearchSelected();
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    getPresenter().onConversationsSearchFilterSelected(null);
                    return true;
                }
            });
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            // search filter not null search was opened before (e.g. before orientation change), open it
            String filter = getPresenter().getViewState().getSearchFilter();
            if (filter != null) {
                searchItem.expandActionView();
                searchView.setQuery(filter, false);
            }
            searchView.setQueryHint(getContext().getString(R.string.conversation_list_search_hint));
            searchView.setOnCloseListener(() -> false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getPresenter().onConversationsSearchFilterSelected(newText);
                    return false;
                }
            });
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return scrollStatePersister.saveScrollState(super.onSaveInstanceState(),
                linearLayoutManager);
    }
}
