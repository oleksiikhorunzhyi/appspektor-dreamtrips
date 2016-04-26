package com.worldventures.dreamtrips.modules.common.presenter.delegate;

import com.worldventures.dreamtrips.modules.common.model.FlagData;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.feed.api.FlagItemCommand;
import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;
import com.worldventures.dreamtrips.modules.tripsimages.api.GetFlagContentQuery;
import com.worldventures.dreamtrips.modules.tripsimages.model.Flag;

import java.util.List;

public class UidItemDelegate {
    private RequestingPresenter requestingPresenter;

    private List<Flag> flagsList;

    public UidItemDelegate(RequestingPresenter requestingPresenter) {
        this.requestingPresenter = requestingPresenter;
    }

    public void loadFlags(Flaggable flaggable) {
        if (flagsList == null) {
            requestingPresenter.doRequest(new GetFlagContentQuery(), flags -> {
                flagsList = flags;
                flaggable.showFlagDialog(flagsList);
            });
        } else {
            flaggable.showFlagDialog(flagsList);
        }
    }

    public void flagItem(FlagData data, View view) {
        requestingPresenter.doRequest(new FlagItemCommand(data), aVoid -> {
            if (view != null) {
                view.flagSentSuccess();
            }
        }, spiceException -> {
            if (view != null) {
                view.flagSentError(spiceException);
            }
        });
    }

    public interface View {
        void flagSentSuccess();

        void flagSentError(Throwable throwable);
    }
}
