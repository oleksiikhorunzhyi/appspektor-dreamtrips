package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.service.HashtagInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.HashtagSuggestionCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostDescriptionCreatedCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class DescriptionCreatorPresenter extends Presenter<DescriptionCreatorPresenter.View> {

    private static final int MIN_QUERY_LENGTH = 3;

    @Inject
    HashtagInteractor interactor;
    @State
    ArrayList<HashtagSuggestion> hashtagSuggestions = new ArrayList<>();

    public void takeView(DescriptionCreatorPresenter.View view) {
        super.takeView(view);
        view.bind(interactor.getSuggestionPipe()
                .observeSuccess()
                .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(hashtagSuggestionCommand -> {
                    view.onSuggestionsReceived(hashtagSuggestionCommand.getResult());
                }, throwable -> {
                    Timber.e(throwable, "");
                });

        view.onSuggestionsReceived(hashtagSuggestions);
    }

    public void query(String desc, int cursorPosition) {
        int lastDashIndex = desc.substring(0, cursorPosition).lastIndexOf("#");
        int lastSpaceIndex = desc.substring(0, cursorPosition).lastIndexOf(" ");
        if (lastDashIndex >= 0 && cursorPosition > lastDashIndex + MIN_QUERY_LENGTH && lastDashIndex > lastSpaceIndex) {
            interactor.getSuggestionPipe().send(new HashtagSuggestionCommand(desc.substring(lastDashIndex + 1, cursorPosition)));
        } else {
            view.clearSuggestions();
        }
    }

    public void done(String desc) {
        interactor.getDescPickedPipe().send(new PostDescriptionCreatedCommand(desc));
    }

    public interface View extends RxView {
        void onSuggestionsReceived(List<HashtagSuggestion> suggestionList);

        void clearSuggestions();
    }
}
