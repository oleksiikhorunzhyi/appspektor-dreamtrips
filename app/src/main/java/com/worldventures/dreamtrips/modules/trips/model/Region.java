package com.worldventures.dreamtrips.modules.trips.model;

import com.worldventures.dreamtrips.modules.common.model.BaseEntity;

/**
 * Created by Edward on 22.01.15.
 */
public class Region extends BaseEntity {

    private String name;
    private transient boolean checked = true;
    private transient boolean shouldBeGone = true;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShouldBeGone() {
        return shouldBeGone;
    }

    public void setShouldBeGone(boolean shouldBeGone) {
        this.shouldBeGone = shouldBeGone;
    }
}
