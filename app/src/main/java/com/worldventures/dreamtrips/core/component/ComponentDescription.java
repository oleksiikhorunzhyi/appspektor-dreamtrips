package com.worldventures.dreamtrips.core.component;

import android.support.v4.app.Fragment;

public class ComponentDescription {
    private final String key;
    private final int title;
    private final int icon;
    private final Class<? extends Fragment> fragmentClass;

    public ComponentDescription(String key, int titleRes, int iconRes, Class<? extends Fragment> fragmentClass) {
        this.key = key;
        this.title = titleRes;
        this.icon = iconRes;
        this.fragmentClass = fragmentClass;
    }

    public String getKey() {
        return key;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentDescription that = (ComponentDescription) o;

        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        return key != null ? key.hashCode() : 0;
    }
}
