package com.messenger.ui.view.add_member;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.cell.CheckableUserCell;
import com.messenger.ui.adapter.cell.HeaderCell;
import com.messenger.ui.anim.WeightSlideAnimator;
import com.messenger.ui.model.SelectableDataUser;
import com.messenger.ui.presenter.ChatMembersScreenPresenter;
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.util.recyclerview.Header;
import com.messenger.ui.util.recyclerview.VerticalDivider;
import com.messenger.ui.view.layout.MessengerPathLayout;
import com.messenger.ui.widget.AddMemberView;
import com.messenger.util.ScrollStatePersister;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;

public abstract class ChatMembersScreenImpl<P extends StyledPath> extends MessengerPathLayout<ChatMembersScreen, ChatMembersScreenPresenter, P> implements ChatMembersScreen {

   @InjectView(R.id.content_layout) ViewGroup contentView;
   @InjectView(R.id.new_chat_loading_view) View loadingView;
   @InjectView(R.id.new_chat_error_view) View errorView;

   @InjectView(R.id.new_chat_toolbar) Toolbar toolbar;
   @InjectView(R.id.new_chat_recycler_view) RecyclerView recyclerView;

   @InjectView(R.id.new_chat_conversation_name_layout) View conversationNameEditTextLayout;
   @InjectView(R.id.new_chat_conversation_name) EditText conversationNameEditText;
   @InjectView(R.id.add_member_search_view) AddMemberView addMemberView;

   private ToolbarPresenter toolbarPresenter;

   private BaseDelegateAdapter<Object> adapter;
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

   private BaseDelegateAdapter<Object> createAdapter() {
      BaseDelegateAdapter<Object> adapter = new BaseDelegateAdapter<>(getContext(), injector);
      adapter.registerCell(SelectableDataUser.class, CheckableUserCell.class);
      adapter.registerCell(Header.class, HeaderCell.class);
      adapter.registerDelegate(SelectableDataUser.class, new CellDelegate<SelectableDataUser>() {
         @Override
         public void onCellClicked(SelectableDataUser model) {
            getPresenter().onItemSelectChange(model);
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
      conversationNameAnimator = new WeightSlideAnimator(conversationNameEditTextLayout);
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
   public void setAdapterItems(List<Object> item) {
      adapter.setItems(item);
   }

   @Override
   public void setChosenUsers(List<DataUser> users) {
      addMemberView.setChosenUsers(users);
   }

   @Override
   public Observable<CharSequence> getSearchQueryObservable() {
      return addMemberView.getQueryObservable();
   }

   @Override
   public Observable<DataUser> getRemovedUserObservable() {
      return addMemberView.getRemovedUserObservable();
   }

   @Override
   public void setSearchQuery(@Nullable CharSequence query) {
      addMemberView.setQuery(query);
   }

   @Override
   public void setTitle(String title) {
      toolbarPresenter.setTitle(title);
   }

   @Override
   public void setTitle(@StringRes int title) {
      toolbarPresenter.setTitle(title);
   }

   @NonNull
   @Override
   public String getConversationName() {
      return conversationNameEditText.getText().toString();
   }

   @Override
   public Parcelable onSaveInstanceState() {
      return scrollStatePersister.saveScrollState(super.onSaveInstanceState(), linearLayoutManager);
   }
}
