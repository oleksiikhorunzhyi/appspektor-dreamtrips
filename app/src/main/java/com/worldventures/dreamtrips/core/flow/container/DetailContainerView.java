package com.worldventures.dreamtrips.core.flow.container;

import android.content.Context;
import android.util.AttributeSet;

import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.worldventures.dreamtrips.R;

import flow.path.Path;
import flow.path.PathContextFactory;

public class DetailContainerView extends FramePathContainerView {

    public DetailContainerView(Context context, AttributeSet attrs) {
        super(context, attrs, null);
        DetailPathContainer container = new DetailPathContainer(context,
                R.id.screen_switcher_tag, Path.contextFactory(), getEmptyPath());
        setContainer(container);
    }

    static class DetailPathContainer extends SimplePathContainer {

        private Path emptyPath;

        DetailPathContainer(Context context, int tagKey, PathContextFactory contextFactory, Path emptyPath) {
            super(context,tagKey, contextFactory);
            this.emptyPath = emptyPath;
        }

        @Override
        protected int getLayout(Path path) {
            MasterDetailPath mdPath = (MasterDetailPath) path;
            return super.getLayout(mdPath.isMaster() ? emptyPath : mdPath);
        }
    }

    protected Path getEmptyPath() {
        return EmptyPath.EMPTY;
    }

    @Layout(R.layout.screen_empty)
    public static class EmptyPath extends Path {
        static final EmptyPath EMPTY = new EmptyPath();
    }
}