package com.messenger.ui.view.edit_member;

import android.content.Context;
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

import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.ActionButtonsContactsCursorAdapter;
import com.messenger.ui.presenter.EditChatMembersScreenPresenter;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.util.ScrollStatePersister;
import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditChatMembersScreenImpl extends MessengerPathLayout<EditChatMembersScreen,
        EditChatMembersScreenPresenter, EditChatPath> implements EditChatMembersScreen {

    @InjectView(R.id.edit_chat_members_content_view)
    ViewGroup contentView;
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
    private LinearLayoutManager linearLayoutManager;
    private ScrollStatePersister scrollStatePersister = new ScrollStatePersister();

    public EditChatMembersScreenImpl(Context context) {
        super(context);
    }

    public EditChatMembersScreenImpl(Context context, AttributeSet attrs) {
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
        if (inflateToolbarMenu(toolbar)) {
            prepareOptionsMenu(toolbar.getMenu());
        }
        getPresenter().requireAdapterInfo();
    }

    public void setAdapterWithInfo(DataUser user, boolean isOwner) {
        Context context = getContext();
        EditChatMembersScreenPresenter presenter = getPresenter();

        // Use this class until sorting logic in ContactCursorAdapter is checked
        // to be working (provided users are sorted alphabetically) or fixed if needed if
        // it does not work when contacts sorting is fixed.
        adapter = new ActionButtonsContactsCursorAdapter(context, user, isOwner);
        adapter.setDeleteRequestListener(presenter::onDeleteUserFromChat);
        adapter.setUserClickListener(presenter::onUserClicked);

        recyclerView.setSaveEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new VerticalDivider(
                ContextCompat.getDrawable(context, R.drawable.divider_list)));
        scrollStatePersister.restoreInstanceState(getLastRestoredInstanceState(), linearLayoutManager);
    }

    private void initUi() {
        ButterKnife.inject(this);
        //
        toolbarPresenter = new ToolbarPresenter(toolbar, getContext());
        toolbarPresenter.attachPathAttrs(getPath().getAttrs());
        toolbarPresenter.setTitle(null);
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
    public void setTitle(String title) {
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
    public void invalidateAllSwipedLayouts() {
        adapter.closeAllItems();
    }

    @Override
    public void showDeletionConfirmationDialog(DataUser user) {
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

    public void prepareOptionsMenu(Menu menu) {
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
            searchView.setQueryHint(getContext().getString(R.string.edit_chat_members_search_hint));
            searchView.setOnCloseListener(() -> false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    getPresenter().onSearchFilterSelected(newText);
                    return false;
                }
            });
        }
    }

    @NonNull
    @Override
    public EditChatMembersScreenPresenter createPresenter() {
        return new EditChatMembersScreenPresenterImpl(getContext(), getPath().getConversationId());
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return scrollStatePersister.saveScrollState(super.onSaveInstanceState(),
                linearLayoutManager);
    }
}