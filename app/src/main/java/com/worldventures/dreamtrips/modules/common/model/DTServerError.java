package com.worldventures.dreamtrips.modules.common.model;

public class DTServerError {
    String[] base;
    String[] uploadId;

    public String[] getBase() {
        return base;
    }

    public void setBase(String[] base) {
        this.base = base;
    }

    public String[] getUploadId() {
        return uploadId;
    }

    public void setUploadId(String[] uploadId) {
        this.uploadId = uploadId;
    }

    public String getErrorString() {
        String result = null;
        if (base != null && base.length > 0) {
            result = base[0];
        } else if (uploadId != null && uploadId.length > 0) {
            result = uploadId[0];
        }
        return result;
    }
}
