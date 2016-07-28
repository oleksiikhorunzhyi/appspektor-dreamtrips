package com.worldventures.dreamtrips.modules.feed.view.util;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.service.TranslationFeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.TranslateUidItemCommand;

import java.util.ArrayList;
import java.util.List;

import io.techery.janet.helper.ActionStateToActionTransformer;
import timber.log.Timber;

public final class TextualPostTranslationDelegate<V extends RxView & TextualPostTranslationDelegate.View> {

    private TranslationFeedInteractor translationFeedInteractor;
    private V view;
    private List<FeedItem> feedItems;

    public TextualPostTranslationDelegate(TranslationFeedInteractor translationFeedInteractor) {
       this.translationFeedInteractor = translationFeedInteractor;
    }

    public void onTakeView(V view, List<FeedItem> feedItems) {
        this.view = view;
        this.feedItems = feedItems;
        subscribeToPostTranslation();
    }

    public void onTakeView(V view, FeedItem feedItem) {
        onTakeView(view, Queryable.from(feedItem).toList());
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
                .compose(new ActionStateToActionTransformer<>())
                .map(TranslateUidItemCommand.TranslatePostCommand::getResult))
                .compose(new IoToMainComposer<>())
                .subscribe(this::translateSuccess, this::translateFail);
    }

    private void translateSuccess(TextualPost textualPost) {
        int textualPostIndex = getFeedItemPositionForPost(textualPost);
        if (textualPostIndex != -1) {
            feedItems.get(textualPostIndex).setItem(textualPost);
            view.updateItem(feedItems.get(textualPostIndex));
        }
    }

    private void translateFail(Throwable throwable) {
        Timber.e(throwable, "translateFail");
        view.informUser(R.string.smth_went_wrong);
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
