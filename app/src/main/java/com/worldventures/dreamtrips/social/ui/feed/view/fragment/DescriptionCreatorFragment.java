package com.worldventures.dreamtrips.social.ui.feed.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.worldventures.core.di.qualifier.ForActivity;
import com.worldventures.core.janet.Injector;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.view.DividerItemDecoration;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.custom.KeyCallbackEditText;
import com.worldventures.dreamtrips.modules.common.view.custom.ProgressEmptyRecyclerView;
import com.worldventures.dreamtrips.social.ui.feed.bundle.DescriptionBundle;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.social.ui.feed.presenter.DescriptionCreatorPresenter;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.HashtagSuggestionCell;
import com.worldventures.dreamtrips.social.ui.feed.view.util.HashtagSuggestionUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@Layout(R.layout.fragment_description_creator)
@MenuResource(R.menu.menu_add_post_description)
public class DescriptionCreatorFragment extends RxBaseFragmentWithArgs<DescriptionCreatorPresenter, DescriptionBundle> implements DescriptionCreatorPresenter.View {

   @Inject @ForActivity Injector injector;

   BaseDelegateAdapter<HashtagSuggestion> adapter;
   RecyclerViewStateDelegate stateDelegate;

   @InjectView(R.id.description) KeyCallbackEditText description;
   @InjectView(R.id.suggestions) ProgressEmptyRecyclerView suggestions;
   @InjectView(R.id.suggestionProgress) View suggestionsProgressView;

   private int cursorPositionWhenTextChanged;

   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
   }

   public void onSaveInstanceState(Bundle savedInstanceState) {
      stateDelegate.saveStateIfNeeded(savedInstanceState);
      super.onSaveInstanceState(savedInstanceState);
   }

   @Override
   public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
      super.onViewStateRestored(savedInstanceState);
      getPresenter().onViewStateRestored();
   }

   public void onDestroyView() {
      super.onDestroyView();
      stateDelegate.onDestroyView();
   }

   public DescriptionCreatorPresenter createPresenter(Bundle savedInstanceState) {
      return new DescriptionCreatorPresenter();
   }

   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      adapter = new BaseDelegateAdapter<>(getActivity(), injector);
      adapter.registerCell(HashtagSuggestion.class, HashtagSuggestionCell.class);
      adapter.registerDelegate(HashtagSuggestion.class, new HashtagSuggestionCell.Delegate() {
         public void onCellClicked(HashtagSuggestion model) {
            onSuggestionClicked(model.getName());
         }
      });

      stateDelegate.setRecyclerView(suggestions);

      suggestions.setAdapter(adapter);
      suggestions.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
      suggestions.addItemDecoration(dividerItemDecoration());
      suggestions.setProgressView(suggestionsProgressView);
      String text = getArgs() != null && getArgs().getText() != null ? getArgs().getText() : "";
      description.setText(text);
      description.setSelection(text.length());

      RxTextView.afterTextChangeEvents(description)
            .throttleLast(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(event -> {
               String fullQueryText = event.editable().toString();
               cursorPositionWhenTextChanged = description.getSelectionStart();
               getPresenter().query(fullQueryText, cursorPositionWhenTextChanged);
            }, throwable -> {
               Timber.e(throwable, "");
            });
      stateDelegate.restoreStateIfNeeded();
   }

   private void onSuggestionClicked(String suggestion) {
      String descriptionText = description.getText().toString();
      int endReplace = cursorPositionWhenTextChanged;

      int startReplace = HashtagSuggestionUtil.calcStartPosBeforeReplace(descriptionText, endReplace);
      String newText = HashtagSuggestionUtil.generateText(descriptionText, suggestion, endReplace);

      description.setText(newText);
      description.setSelection(startReplace + HashtagSuggestionUtil.replaceableText(suggestion).length());

      adapter.clear();
   }

   public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
         case R.id.action_done:
            getPresenter().done(description.getText().toString());
            router.back();
            break;
         default:
            break;
      }
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onSuggestionsReceived(String fullQueryText, @NonNull List<HashtagSuggestion> suggestionList) {
      adapter.clear();
      if ((description == null || fullQueryText.equals(description.getText()
            .toString())) && !suggestionList.isEmpty()) {
         suggestions.setVisibility(View.VISIBLE);
         adapter.addItems(suggestionList);
      } else {
         suggestions.setVisibility(View.GONE);
      }
   }

   public void clearSuggestions() {
      adapter.clear();
   }

   @Override
   public void showSuggestionProgress() {
      if (suggestions != null) {
         suggestions.showProgress();
      }
   }

   @Override
   public void hideSuggestionProgress() {
      if (suggestions != null) {
         suggestions.hideProgress();
      }
   }

   private DividerItemDecoration dividerItemDecoration() {
      DividerItemDecoration decor = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST);
      decor.setShowDividerAfterLastCell(false);
      decor.setLeftMarginRes(R.dimen.spacing_normal);
      return decor;
   }
}
