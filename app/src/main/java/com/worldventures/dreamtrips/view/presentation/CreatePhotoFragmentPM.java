package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.view.activity.Injector;

import org.robobinding.annotation.PresentationModel;

@PresentationModel
public class CreatePhotoFragmentPM extends BasePresentation {

    String title;
    String location;
    String date;
    String time;
    String tags;

    public CreatePhotoFragmentPM(IInformView view, Injector injector) {
        super(view, injector);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void onDataSet(int year, int month, int day) {

    }

    public void onTimeSet(int h, int m) {

    }
}
