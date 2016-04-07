package com.worldventures.dreamtrips.core.flow.container;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;

import flow.path.Path;
import flow.path.PathContextFactory;

public class DetailContainerView extends FramePathContainerView {

    public DetailContainerView(Context context, AttributeSet attrs) {
        super(context, attrs, null);
        DetailPathContainer container = new DetailPathContainer(context, R.id.screen_switcher_tag, Path.contextFactory());
        setContainer(container);
    }

    static class DetailPathContainer extends SimplePathContainer {

        DetailPathContainer(Context context, int tagKey, PathContextFactory contextFactory) {
            super(context, tagKey, contextFactory);
        }

        @Override
        protected int getLayout(Path path) {
            MasterDetailPath mdPath = (MasterDetailPath) path;
            Path emptyPath = mdPath.getEmpty();
            return super.getLayout(mdPath.isMaster() ? emptyPath : mdPath);
        }
    }

}