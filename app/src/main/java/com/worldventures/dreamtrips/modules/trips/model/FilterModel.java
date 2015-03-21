package com.worldventures.dreamtrips.modules.trips.model;

/**
 * Created by Edward on 22.01.15.
 */
public class FilterModel {

    private int indexLeftPrice = 0;
    private int indexRightPrice = 4;
    private int indexLeftDuration = 0;
    private int indexRightDuration = 3;
    private boolean checked = true;
    private boolean hide = true;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getIndexLeftPrice() {
        return indexLeftPrice;
    }

    public void setIndexLeftPrice(int indexLeftPrice) {
        this.indexLeftPrice = indexLeftPrice;
    }

    public int getIndexRightPrice() {
        return indexRightPrice;
    }

    public void setIndexRightPrice(int indexRightPrice) {
        this.indexRightPrice = indexRightPrice;
    }

    public int getIndexLeftDuration() {
        return indexLeftDuration;
    }

    public void setIndexLeftDuration(int indexLeftDuration) {
        this.indexLeftDuration = indexLeftDuration;
    }

    public int getIndexRightDuration() {
        return indexRightDuration;
    }

    public void setIndexRightDuration(int indexRightDuration) {
        this.indexRightDuration = indexRightDuration;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void reset() {
        checked = true;
        indexLeftPrice = 0;
        indexRightPrice = 4;
        indexLeftDuration = 0;
        indexRightDuration = 3;
    }
}
