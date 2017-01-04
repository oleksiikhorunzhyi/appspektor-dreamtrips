package com.messenger.ui.view.edit_member;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.SwipeableContactsAdapter;
import com.messenger.ui.adapter.cell.HeaderCell;
import com.messenger.ui.adapter.cell.SwipeableUserCell;
import com.messenger.ui.adapter.swipe.SwipeableAdapterManager;
import com.messenger.ui.model.SwipeDataUser;
import com.messenger.ui.presenter.EditChatMembersScreenPresenter;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.Header;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.util.ScrollStatePersister;
import com.worldventures.dreamtrips.R;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;

public class EditChatMembersScreenImpl extends MessengerPathLayout<EditChatMembersScreen, EditChatMembersScreenPresenter, EditChatPath> implements EditChatMembersScreen {

   @InjectView(R.id.content_layout) ViewGroup contentView;
   @InjectView(R.id.edit_chat_members_loading_view) View loadingView;
   @InjectView(R.id.edit_chat_members_error_view) View errorView;

   @InjectView(R.id.edit_chat_members_toolbar) Toolbar toolbar;
   @InjectView(R.id.edit_chat_members_recycler_view) RecyclerView recyclerView;

   private ToolbarPresenter toolbarPresenter;

   private SwipeableContactsAdapter<Object> adapter;
   private LinearLayoutManager linearLayoutManager;
   private SwipeableAdapterManager swipeableAdapterManager = new SwipeableAdapterManager();
   private ScrollStatePersister scrollStatePersister = new ScrollStatePersister();
   private Observable<CharSequence> searchObservable;
   private MenuItem searchItem;
   private SearchView searchView;

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
   protected void onPostAttachToWindowView() {
      super.onPostAttachToWindowView();
      if (inflateToolbarMenu(toolbar)) {
         prepareOptionsMenu(toolbar.getMenu());
      }
      setAdapterWithInfo();
   }

   private void setAdapterWithInfo() {
      Context context = getContext();
      EditChatMembersScreenPresenter presenter = getPresenter();

      adapter = new SwipeableContactsAdapter<>(context, injector);
      adapter.registerCell(SwipeDataUser.class, SwipeableUserCell.class);
      adapter.registerCell(Header.class, HeaderCell.class);
      adapter.registerDelegate(SwipeDataUser.class, new SwipeableUserCell.Delegate() {
         @Override
         public void onDeleteUserRequired(SwipeDataUser swipeDataUser) {
            presenter.onDeleteUserFromChat(swipeDataUser.user);
         }

         @Override
         public void onCellClicked(SwipeDataUser swipeDataUser) {
            presenter.onUserClicked(swipeDataUser.user);
         }
      });

      recyclerView.setSaveEnabled(true);
      recyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(context));
      recyclerView.setAdapter(swipeableAdapterManager.wrapAdapter(adapter));
      recyclerView.addItemDecoration(new VerticalDivider(ContextCompat.getDrawable(context, R.drawable.divider_list)));
      scrollStatePersister.restoreInstanceState(getLastRestoredInstanceState(), linearLayoutManager);
   }

   private void initUi() {
      ButterKnife.inject(this);
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
   public void setTitle(String title) {
      toolbarPresenter.setTitle(title);
   }

   @Override
   public void setAdapterData(List<Object> items) {
      adapter.setItems(items);
   }

   @Override
   public void invalidateAllSwipedLayouts() {
      swipeableAdapterManager.closeAllItems();
   }

   @Override
   public void showDeletionConfirmationDialog(DataUser user) {
      new AlertDialog.Builder(getContext()).setNegativeButton(R.string.action_cancel, (dialogInterface, i) -> swipeableAdapterManager
            .closeAllItems())
            .setPositiveButton(R.string.edit_chat_dialog_confirm_user_deletion_button_delete, (dialog1, which1) -> {
               getPresenter().onDeleteUserFromChatConfirmed(user);
               swipeableAdapterManager.closeAllItems();
            })
            .setMessage(R.string.edit_chat_dialog_confirm_user_deletion_message)
            .create()
            .show();
   }

   public void prepareOptionsMenu(Menu menu) {
      searchItem = menu.findItem(R.id.action_search);
      if (searchItem != null) {
         searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
         searchView.setQueryHint(getContext().getString(R.string.edit_chat_members_search_hint));
         searchObservable = RxSearchView.queryTextChanges(searchView);
      }
   }

   @Override
   public void restoreSearchQuery(String searchQuery) {
      if (!TextUtils.isEmpty(searchQuery)) {
         searchItem.expandActionView();
         searchView.setQuery(searchQuery, false);
      }
   }

   @Override
   public Observable<CharSequence> getSearchObservable() {
      return searchObservable;
   }

   @NonNull
   @Override
   public EditChatMembersScreenPresenter createPresenter() {
      return new EditChatMembersScreenPresenterImpl(getContext(), injector, getPath().getConversationId());
   }

   @Override
   public Parcelable onSaveInstanceState() {
      return scrollStatePersister.saveScrollState(super.onSaveInstanceState(), linearLayoutManager);
   }

   @Override
   public void showMessage(@StringRes int text) {
      Snackbar.make(this, text, Snackbar.LENGTH_SHORT).show();
   }
}
