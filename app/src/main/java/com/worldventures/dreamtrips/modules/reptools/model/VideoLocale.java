package com.worldventures.dreamtrips.modules.reptools.model;

import java.io.Serializable;

public class VideoLocale implements Serializable {
    String title;
    String country;

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoLocale that = (VideoLocale) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return !(country != null ? !country.equals(that.country) : that.country != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}
