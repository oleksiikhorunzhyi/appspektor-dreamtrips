package com.worldventures.dreamtrips.modules.feed.view.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.common.presenter.ApiErrorPresenter;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;

import java.util.List;

import io.techery.janet.helper.ActionStateSubscriber;

public final class TextualPostTranslationDelegate<V extends RxView & TextualPostTranslationDelegate.View> {

    private V view;
    private List<FeedItem> feedItems;
    private ApiErrorPresenter apiErrorPresenter;

    private TranslationFeedInteractor translationFeedInteractor;

    public TextualPostTranslationDelegate(TranslationFeedInteractor translationFeedInteractor) {
        this.translationFeedInteractor = translationFeedInteractor;
    }

    public void onTakeView(V view, List<FeedItem> feedItems, ApiErrorPresenter apiErrorPresenter) {
        this.view = view;
        this.feedItems = feedItems;
        this.apiErrorPresenter = apiErrorPresenter;
        subscribeToPostTranslation();
    }

    public void onTakeView(V view, FeedItem feedItem, ApiErrorPresenter apiErrorPresenter) {
        onTakeView(view, Queryable.from(feedItem).toList(), apiErrorPresenter);
    }

    public void onDropView() {
        view = null;
    }

    public void translate(TextualPost textualPost, String languageTo) {
        translationFeedInteractor.translatePostPipe().send(
                TranslateUidItemCommand.forPost(textualPost, languageTo));
    }

    private void subscribeToPostTranslation() {
        view.bindUntilDropView(translationFeedInteractor.translatePostPipe().observe()
                .compose(new IoToMainComposer<>()))
                .subscribe(new ActionStateSubscriber<TranslateUidItemCommand.TranslatePostCommand>()
                        .onSuccess(translatePostCommand -> translateSuccess(translatePostCommand.getResult()))
                        .onFail(this::translateFail));
    }

    private void translateSuccess(TextualPost textualPost) {
        int textualPostIndex = getFeedItemPositionForPost(textualPost);
        if (textualPostIndex != -1) {
            feedItems.get(textualPostIndex).setItem(textualPost);
            view.updateItem(feedItems.get(textualPostIndex));
        }
    }

    private void translateFail(Object action, Throwable throwable) {
        apiErrorPresenter.handleActionError(action, throwable);
        view.updateItem(null);
    }

    private int getFeedItemPositionForPost(TextualPost textualPost) {
        for (int i = 0; i < feedItems.size(); i++) {
            if (feedItems.get(i).getItem().equals(textualPost)) return i;
        }
        return -1;
    }

    public interface View {

        void updateItem(FeedItem feedItem);
    }
}
