package com.worldventures.dreamtrips.modules.dtl.model;

public class EstimationPointsHolder {

    private double points;

    public double getPoints() {
        return points;
    }

    public int getPointsInteger() {
        return ((Double) points).intValue();
    }

    public void setPoints(double points) {
        this.points = points;
    }
}
