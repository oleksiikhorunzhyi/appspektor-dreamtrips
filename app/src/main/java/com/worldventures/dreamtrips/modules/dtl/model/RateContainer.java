package com.worldventures.dreamtrips.modules.dtl.model;

public class RateContainer {

    private int food;
    private int service;
    private int cleanliness;
    private int uniqueness;

    public RateContainer(int food, int service, int cleanliness, int uniqueness) {
        this.food = food;
        this.service = service;
        this.cleanliness = cleanliness;
        this.uniqueness = uniqueness;
    }

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }

    public int getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(int cleanliness) {
        this.cleanliness = cleanliness;
    }

    public int getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(int uniqueness) {
        this.uniqueness = uniqueness;
    }
}
