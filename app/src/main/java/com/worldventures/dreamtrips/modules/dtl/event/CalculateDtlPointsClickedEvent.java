package com.worldventures.dreamtrips.modules.dtl.event;

public class CalculateDtlPointsClickedEvent {

    private String userInput;

    public CalculateDtlPointsClickedEvent(String userInput) {
        this.userInput = userInput;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
