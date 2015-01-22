package com.worldventures.dreamtrips.core.model;

/**
 * Created by Edward on 22.01.15.
 */
public class Region extends BaseEntity {

    private String name;
    private transient boolean checked = true;

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
}
