package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.messenger.messengerservers.entities.Conversation;
import com.messenger.ui.adapter.ConversationsCursorAdapter;
import com.messenger.ui.presenter.ConversationListScreenPresenter;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ConversationListScreenImpl extends BaseViewStateLinearLayout<ConversationListScreen,
        ConversationListScreenPresenter> implements ConversationListScreen,
        ConversationsCursorAdapter.SwipeButtonsListener {

    @InjectView(R.id.conversation_list_recycler_view) RecyclerView recyclerView;
    @InjectView(R.id.conversation_list_loading_view) View loadingView;
    @InjectView(R.id.conversation_list_error_view) View errorView;

    @InjectView(R.id.conversation_list_toolbar) Toolbar toolbar;
    @InjectView(R.id.conversation_conversation_type_spinner)
    Spinner conversationsDropDownSpinner;

    private SearchView searchView;
    private String savedSearchFilter;

    private ToolbarPresenter toolbarPresenter;

    private ConversationsCursorAdapter adapter;

    public ConversationListScreenImpl(Context context) {
        super(context);
        initUi();
    }

    public ConversationListScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUi();
    }

    private void initUi() {
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.main_background));

        LayoutInflater.from(getContext()).inflate(R.layout.screen_conversation_list, this, true);
        ButterKnife.inject(this, this);

        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.enableUpNavigationButton();
        toolbarPresenter.disableTitle();

        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.conversation_list_spinner_items, R.layout.spinner_item_action_bar);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_action_bar);
        final String[] dropdownArray = getContext().getResources().getStringArray(R.array.
                conversation_list_spinner_items);
        conversationsDropDownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinnerAdapter.getItem(i).equals(dropdownArray[0])) {
                    getPresenter().onConversationsDropdownSelected(false);
                } else if (spinnerAdapter.getItem(i).equals(dropdownArray[1])) {
                    getPresenter().onConversationsDropdownSelected(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        conversationsDropDownSpinner.setAdapter(spinnerAdapter);
    }

    @Override
    public void setPresenter(ConversationListScreenPresenter presenter) {
        super.setPresenter(presenter);
        setAdapters();
    }

    private void setAdapters() {
        ConversationListScreenPresenter presenter = getPresenter();

        adapter = new ConversationsCursorAdapter(getContext(), recyclerView, presenter.getUser());
        adapter.setClickListener(presenter::onConversationSelected);
        adapter.setSwipeButtonsListener(this);
        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new VerticalDivider(ContextCompat.getDrawable(getContext(), R.drawable.divider_list)));
        adapter.setClickListener(conversation -> getPresenter().onConversationSelected(conversation));
        recyclerView.setAdapter(adapter);
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
        if (adapter != null) {
            adapter.swapCursor(cursor);
        }
    }

    @Override
    public void showConversations(Cursor cursor, String searchFilter) {
        this.savedSearchFilter = searchFilter;
        if (adapter != null) {
            adapter.swapCursor(cursor, searchFilter);
        }
    }

    @Override
    public ConversationListScreenPresenter createPresenter() {
        return new ConversationListScreenPresenterImpl(getActivity());
    }

    @Override
    public void onDeleteButtonPressed(Conversation conversation) {
        getPresenter().onDeleteButtonPressed(conversation);
    }

    @Override
    public void showConversationDeletionConfirmationDialog(Conversation conversation) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.conversation_list_delete_dialog_message)
                .setPositiveButton(R.string.conversation_list_delete_dialog_pos_button,
                        (d, i) -> {
                    presenter.onDeletionConfirmed(conversation);
                    adapter.closeAllItems();
                })
                .setNeutralButton(R.string.cancel, (d, i) -> adapter.closeAllItems())
                .show();
    }

    @Override
    public void onMoreOptionsButtonPressed(Conversation conversation) {
        getPresenter().onMoreOptionsButtonPressed(conversation);
    }

    @Override
    public void showConversationMoreActionsDialog(Conversation conversation) {
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
                    adapter.closeAllItems();
                })
                .show();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Activity related
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public AppCompatActivity getActivity() {
        return (AppCompatActivity) getContext();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return presenter.onCreateOptionsMenu(menu);
    }

    @Override public void onPrepareOptionsMenu(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
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
            if (savedSearchFilter != null) {
                searchItem.expandActionView();
                searchView.setQuery(savedSearchFilter, false);
            }
            searchView.setQueryHint(getContext().getString(R.string.conversation_list_search_hint));
            searchView.setOnCloseListener(() -> {
                return false;
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override public boolean onQueryTextChange(String newText) {
                    getPresenter().onConversationsSearchFilterSelected(newText);
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        presenter.onDestroy();
    }
}
