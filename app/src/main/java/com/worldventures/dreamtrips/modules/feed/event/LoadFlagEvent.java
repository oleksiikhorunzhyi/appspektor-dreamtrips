package com.worldventures.dreamtrips.modules.feed.event;

import com.worldventures.dreamtrips.modules.feed.view.cell.Flaggable;

public class LoadFlagEvent {
    private Flaggable cell;

    public LoadFlagEvent(Flaggable cell) {
        this.cell = cell;
    }

    public Flaggable getCell() {
        return cell;
    }
}
