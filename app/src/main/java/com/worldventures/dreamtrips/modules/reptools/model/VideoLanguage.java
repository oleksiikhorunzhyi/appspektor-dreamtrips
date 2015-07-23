package com.worldventures.dreamtrips.modules.reptools.model;


import com.worldventures.dreamtrips.modules.common.view.util.Filterable;

import java.io.Serializable;

public class VideoLanguage implements Serializable, Filterable {

    String title;
    String nativeTitle;
    String code;

    public String getTitle() {
        return title;
    }

    public String getNativeTitle() {
        return nativeTitle;
    }

    public String getCode() {
        return code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNativeTitle(String nativeTitle) {
        this.nativeTitle = nativeTitle;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean containsQuery(String query) {
        return title.toLowerCase().contains(query.toLowerCase())
                || nativeTitle.toLowerCase().contains(query.toLowerCase());
    }
}
