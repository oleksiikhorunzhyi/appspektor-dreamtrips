package com.worldventures.dreamtrips.presentation.fullscreen;

import com.worldventures.dreamtrips.core.model.Inspiration;
import com.worldventures.dreamtrips.utils.anotation.IgnoreRobobinding;

@IgnoreRobobinding
public class FSInspireMePM extends BaseFSViewPM<Inspiration> {
    public FSInspireMePM(View view) {
        super(view);
    }

    @Override
    protected boolean isLiked() {
        return false;
    }

    @Override
    protected boolean isFlagVisible() {
        return false;
    }

    @Override
    protected boolean isDeleteVisible() {
        return false;
    }

    @Override
    protected boolean isLikeVisible() {
        return false;
    }
}
