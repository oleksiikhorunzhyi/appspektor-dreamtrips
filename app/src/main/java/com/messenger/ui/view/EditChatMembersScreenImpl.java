package com.messenger.ui.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.StringRes;
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
import android.widget.LinearLayout;

import com.messenger.messengerservers.entities.User;
import com.messenger.ui.adapter.ActionButtonsContactsCursorAdapter;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.EditChatMembersScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditChatMembersScreenImpl extends BaseViewStateLinearLayout<EditChatMembersScreen,
        EditChatMembersScreenPresenter> implements EditChatMembersScreen {

    @InjectView(R.id.edit_chat_members_content_view)
    View contentView;
    @InjectView(R.id.edit_chat_members_loading_view)
    View loadingView;
    @InjectView(R.id.edit_chat_members_error_view)
    View errorView;

    @InjectView(R.id.edit_chat_members_toolbar)
    Toolbar toolbar;
    @InjectView(R.id.edit_chat_members_recycler_view)
    RecyclerView recyclerView;

    private ToolbarPresenter toolbarPresenter;

    private SearchView searchView;
    private String savedSearchFilter;

    private ActionButtonsContactsCursorAdapter adapter;

    public EditChatMembersScreenImpl(Context context) {
        super(context);
        init(context);
    }

    public EditChatMembersScreenImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.screen_edit_chat_members, this, true);
        ButterKnife.inject(this, this);
        initUi();
    }

    private void initUi() {
        toolbarPresenter = new ToolbarPresenter(toolbar, (AppCompatActivity) getContext());
        toolbarPresenter.enableUpNavigationButton();

        // Use this class until sorting logic in ContactCursorAdapter is checked
        // to be working (provided users are sorted alphabetically) or fixed if needed if
        // it does not work when contacts sorting is fixed.
        adapter = new ActionButtonsContactsCursorAdapter(getContext(), null);
        adapter.setRowButtonsActionListener(user -> getPresenter().onDeleteUserFromChat(user));

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalDivider(getResources()
                .getDrawable(R.drawable.divider_list)));
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
    public void setTitle(String title) {
        toolbarPresenter.setTitle(title);
    }

    @Override
    public void setTitle(@StringRes int title) {
        toolbarPresenter.setTitle(title);
    }

    @Override
    public void setMembers(Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void setMembers(Cursor cursor, String query, String queryColumn) {
        this.savedSearchFilter = query;
        adapter.swapCursor(cursor, query, queryColumn);
    }

    @Override
    public void showDeletionConfirmationDialog(User user) {
        new AlertDialog.Builder(getContext())
                .setNegativeButton(android.R.string.cancel, (dialogInterface, i)
                        -> adapter.closeAllItems())
                .setPositiveButton(R.string.edit_chat_dialog_confirm_user_deletion_button_delete, (dialog1, which1) -> {
                    getPresenter().onDeleteUserFromChatConfirmed(user);
                    adapter.closeAllItems();
                })
                .setMessage(R.string.edit_chat_dialog_confirm_user_deletion_message)
                .create()
                .show();
    }

    @Override
    public AppCompatActivity getActivity() {
        return (AppCompatActivity)getContext();
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
                    getPresenter().onSearchFilterSelected(null);
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
                    getPresenter().onSearchFilterSelected(newText);
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

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public EditChatMembersScreenPresenter createPresenter() {
        return new EditChatMembersScreenPresenterImpl(getActivity());
    }
}
