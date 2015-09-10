package com.worldventures.dreamtrips.modules.trips.model;


import java.io.Serializable;

public class FilterModel implements Serializable {

    private int indexLeftPrice = 0;
    private int indexRightPrice = 4;
    private int indexLeftDuration = 0;
    private int indexRightDuration = 9;

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

    public void reset() {
        indexLeftPrice = 0;
        indexRightPrice = 4;
        indexLeftDuration = 0;
        indexRightDuration = 3;
    }
}
