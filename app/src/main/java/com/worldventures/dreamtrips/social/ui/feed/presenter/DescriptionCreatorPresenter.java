package com.worldventures.dreamtrips.social.ui.feed.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.social.ui.feed.service.CreatePostBodyInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostDescriptionCreatedCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class DescriptionCreatorPresenter extends Presenter<DescriptionCreatorPresenter.View> {

   private static final int MIN_QUERY_LENGTH = 3;

   @Inject HashtagInteractor hashtagsInteractor;
   @Inject CreatePostBodyInteractor createPostBodyInteractor;
   @State ArrayList<HashtagSuggestion> hashtagSuggestions = new ArrayList<>();
   @State String query = "";

   public void takeView(DescriptionCreatorPresenter.View view) {
      super.takeView(view);
      hashtagsInteractor.getSuggestionPipe().observeSuccess()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(command -> {
               hashtagSuggestions.clear();
               hashtagSuggestions.addAll(command.getResult());
               view.onSuggestionsReceived(command.getFullQueryText(), hashtagSuggestions);
               view.hideSuggestionProgress();
            }, throwable -> {
               Timber.e(throwable, "");
               view.hideSuggestionProgress();
            });
   }

   public void query(String fullQueryText, int cursorPosition) {
      if (fullQueryText.equals(query)) {
         return; //for restore state after rotation
      }
      query = fullQueryText;
      String subStr = fullQueryText.substring(0, cursorPosition);
      int lastDashIndex = subStr.lastIndexOf("#");
      int lastSpaceIndex = Math.max(subStr.lastIndexOf(" "), subStr.lastIndexOf("\n"));
      view.clearSuggestions();
      if (lastDashIndex >= 0 && cursorPosition > lastDashIndex + MIN_QUERY_LENGTH && lastDashIndex > lastSpaceIndex) {
         hashtagsInteractor.getSuggestionPipe()
               .send(new HashtagSuggestionCommand(fullQueryText, fullQueryText.substring(lastDashIndex + 1, cursorPosition)));
         view.showSuggestionProgress();
      } else {
         view.hideSuggestionProgress();
      }
   }

   public void done(String desc) {
      createPostBodyInteractor.getPostDescriptionPipe().send(new PostDescriptionCreatedCommand(desc));
   }

   public void onViewStateRestored() {
      view.onSuggestionsReceived(query, hashtagSuggestions);
   }

   public interface View extends RxView {
      void onSuggestionsReceived(String fullQueryText, List<HashtagSuggestion> suggestionList);

      void clearSuggestions();

      void showSuggestionProgress();

      void hideSuggestionProgress();
   }

}
