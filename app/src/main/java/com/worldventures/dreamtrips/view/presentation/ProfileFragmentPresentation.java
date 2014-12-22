package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.view.activity.Injector;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

@PresentationModel
public class ProfileFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {

    public static final String FROM = "from";
    public static final String LIVE_IN = "livesIn";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String USER_ID = "userId";
    public static final String USER_NOTE = "userNote";
    public static final String USER_EMAIL = "userEmail";
    private final PresentationModelChangeSupport changeSupport;
    private final Session currentSession;
    String from;
    String livesIn;
    String dateOfBirth;
    String userId;
    String userNote;
    String userEmail;

    public ProfileFragmentPresentation(Injector objectGraph) {
        super(objectGraph);
        this.changeSupport = new PresentationModelChangeSupport(this);
        currentSession = sessionManager.getCurrentSession();
        User user = currentSession.getUser();
        setUserNote(user.getLastName());
        setUserEmail(user.getEmail());
        setUserId(user.getUsername());
        //  setDateOfBirth(user.getBirthDate().toString());
        setLivesIn(user.getLocation());
        setFrom(user.getLocation());
        changeSupport.firePropertyChange(FROM);
        changeSupport.firePropertyChange(LIVE_IN);
        changeSupport.firePropertyChange(DATE_OF_BIRTH);
        changeSupport.firePropertyChange(USER_ID);
        changeSupport.firePropertyChange(USER_NOTE);
        changeSupport.firePropertyChange(USER_EMAIL);
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNote() {
        return userNote;
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }

    @Override
    public PresentationModelChangeSupport getPresentationModelChangeSupport() {
        return changeSupport;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getLivesIn() {
        return livesIn;
    }

    public void setLivesIn(String livesIn) {
        this.livesIn = livesIn;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void logout() {
        sessionManager.logoutUser();
        activityCompass.openLogin();
        activityCompass.finish();
    }

    public void onDataSet(int year, int month, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar calendar = new GregorianCalendar(year, month, day);
        setDateOfBirth(sdf.format(calendar.getTime()));
        changeSupport.firePropertyChange(DATE_OF_BIRTH);

    }
}
