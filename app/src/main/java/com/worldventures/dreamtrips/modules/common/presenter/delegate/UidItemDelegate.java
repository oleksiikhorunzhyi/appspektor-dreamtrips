package com.worldventures.dreamtrips.modules.common.presenter.delegate;

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

    public void loadFlags(Flaggable cell) {
        if (flagsList == null) {
            requestingPresenter.doRequest(new GetFlagContentQuery(), flags -> {
                flagsList = flags;
                cell.showFlagDialog(flagsList);
            });
        } else {
            cell.showFlagDialog(flagsList);
        }
    }

    public void flagItem(String uid, String nameOfReason) {
        requestingPresenter.doRequest(new FlagItemCommand(uid, nameOfReason), aVoid -> {
        });
    }
}
