package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.feed.hashtag.HashtagSuggestion;
import com.worldventures.dreamtrips.modules.feed.service.api.HashtagSuggestionHttpAction;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class HashtagSuggestionCommand extends Command<List<HashtagSuggestion>> implements InjectableAction {

    @Inject
    Janet janet;
    private String fullQueryText;
    private String query;

    public HashtagSuggestionCommand(String fullText, String query) {
        this.fullQueryText = fullText;
        this.query = query;
    }

    public void run(Command.CommandCallback<List<HashtagSuggestion>> callback) {
        janet.createPipe(HashtagSuggestionHttpAction.class, Schedulers.io())
                .createObservableResult(new HashtagSuggestionHttpAction(query))
                .map(HashtagSuggestionHttpAction::hashtagSuggestions)
                .subscribe(callback::onSuccess, callback::onFail);
    }

    public String getFullQueryText() {
        return fullQueryText;
    }
}