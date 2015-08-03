package com.worldventures.dreamtrips.modules.feed.presenter;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.techery.spares.adapter.IRoboSpiceAdapter;
import com.worldventures.dreamtrips.core.utils.DreamSpiceAdapterController;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.api.GetFeedQuery;
import com.worldventures.dreamtrips.modules.feed.event.CommentsPressedEvent;
import com.worldventures.dreamtrips.modules.feed.model.BaseFeedModel;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;

import java.util.ArrayList;
import java.util.List;

public class FeedPresenter extends Presenter<FeedPresenter.View> {

    private static final int PER_PAGE = GetFeedQuery.LIMIT;

    private DreamSpiceAdapterController<BaseFeedModel> adapterController = new DreamSpiceAdapterController<BaseFeedModel>() {
        @Override
        public SpiceRequest<ArrayList<BaseFeedModel>> getReloadRequest() {
            return new GetFeedQuery(0);
        }

        @Override
        public SpiceRequest<ArrayList<BaseFeedModel>> getNextPageRequest(int currentCount) {
            return new GetFeedQuery(currentCount / PER_PAGE + 1);
        }

        @Override
        public void onStart(LoadType loadType) {
            view.startLoading();
        }

        @Override
        public void onFinish(LoadType type, List<BaseFeedModel> items, SpiceException spiceException) {
            if (adapterController != null) {
                view.finishLoading(items);
                if (spiceException != null) {
                    handleError(spiceException);
                }
            }
        }
    };

    public void reload() {
        adapterController.reload();
    }

    public interface View extends Presenter.View {
        IRoboSpiceAdapter<BaseFeedModel> getAdapter();

        void finishLoading(List<BaseFeedModel> items);

        void startLoading();
    }
}
