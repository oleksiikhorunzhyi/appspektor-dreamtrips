package com.worldventures.dreamtrips.view.presentation;

import com.worldventures.dreamtrips.core.SessionManager;
import com.worldventures.dreamtrips.core.model.Session;
import com.worldventures.dreamtrips.core.model.User;
import com.worldventures.dreamtrips.view.activity.Injector;

import org.robobinding.annotation.PresentationModel;
import org.robobinding.presentationmodel.HasPresentationModelChangeSupport;
import org.robobinding.presentationmodel.PresentationModelChangeSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

@PresentationModel
public class ProfileFragmentPresentation extends BasePresentation implements HasPresentationModelChangeSupport {

    public static final String FROM = "from";
    public static final String LIVE_IN = "livesIn";
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String USER_ID = "userId";
    public static final String USER_NOTE = "userName";
    public static final String USER_EMAIL = "userEmail";

    private final PresentationModelChangeSupport changeSupport;
    private final Session currentSession;

    @Inject
    protected SessionManager sessionManager;

    String from;
    String livesIn;
    String dateOfBirth;
    String userId;
    String userName;
    String userEmail;

    public ProfileFragmentPresentation(Injector objectGraph) {
        super(objectGraph);
        this.changeSupport = new PresentationModelChangeSupport(this);
        currentSession = sessionManager.getCurrentSession();
        User user = currentSession.getUser();
        setUserName(user.getUsername());
        setUserEmail(user.getEmail());
        setUserId(user.getUsername());
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        activityRouter.openLogin();
        activityRouter.finish();
    }

    public void onDataSet(int year, int month, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        Calendar calendar = new GregorianCalendar(year, month, day);
        setDateOfBirth(sdf.format(calendar.getTime()));
        changeSupport.firePropertyChange(DATE_OF_BIRTH);

    }
}
