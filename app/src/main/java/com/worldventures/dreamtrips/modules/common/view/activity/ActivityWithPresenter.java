package com.worldventures.dreamtrips.modules.common.view.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.badoo.mobile.util.WeakHandler;
import com.facebook.Session;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.core.utils.events.ImagePickRequestEvent;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.TermsConditionsDialog;
import com.worldventures.dreamtrips.modules.tripsimages.view.custom.PickImageDelegate;

import icepick.Icepick;
import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class ActivityWithPresenter<PM extends ActivityPresenter> extends BaseActivity
        implements ActivityPresenter.View {

    private PM presenter;
    private PickImageDelegate pickImageDelegate;
    private WeakHandler handler = new WeakHandler();
    private final PublishSubject<ActivityEvent> lifecycleSubject = PublishSubject.create();

    public PM getPresentationModel() {
        return presenter;
    }

    abstract protected PM createPresentationModel(Bundle savedInstanceState);

    @Override
    protected void beforeCreateView(Bundle savedInstanceState) {
        this.presenter = createPresentationModel(savedInstanceState);
        inject(this.presenter);
        this.presenter.onInjected();
        Icepick.restoreInstanceState(this, savedInstanceState);
        this.presenter.restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
        if (presenter != null) this.presenter.saveInstanceState(outState);
        pickImageDelegate.saveInstanceState(outState);
    }


    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        pickImageDelegate = new PickImageDelegate(this);
        pickImageDelegate.restoreInstanceState(savedInstanceState);
        this.presenter.takeView(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.presenter.onMenuPrepared();
        return super.onPrepareOptionsMenu(menu);
    }

    public void informUser(String st) {
        Toast.makeText(getApplicationContext(), st, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void informUser(int stringId) {
        Toast.makeText(getApplicationContext(), stringId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(this) && ViewUtils.isLandscapeOrientation(this);
    }

    @Override
    public boolean isVisibleOnScreen() {
        return true;
    }

    @Override
    public void alert(String s) {
        runOnUiThread(() -> {
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(R.string.alert).content(s).positiveText(R.string.OK).show();
        });
    }

    @Override
    public void showTermsDialog() {
        TermsConditionsDialog.create().show(getSupportFragmentManager());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresentationModel().onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresentationModel().onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.presenter.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
        getPresentationModel().onStop();
    }

    @Override
    public void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        if (getPresentationModel() != null) {
            getPresentationModel().dropView();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (pickImageDelegate != null) {
            pickImageDelegate.setImageCallback(this::imagePicked);
            pickImageDelegate.onActivityResult(requestCode, resultCode, data);
        }

        if (Session.getActiveSession() != null && requestCode == Session.DEFAULT_AUTHORIZE_ACTIVITY_CODE)
            Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        presenter.onConfigurationChanged(newConfig);
    }

    public void onEvent(ImagePickRequestEvent event) {
        pickImage(event.getRequestType(), event.getRequesterID());
    }

    private void pickImage(int requestType, int requesterId) {
        pickImageDelegate.setRequesterId(requesterId);
        pickImageDelegate.setRequestType(requestType);
        pickImageDelegate.show();
    }

    private void imagePicked(ChosenImage... chosenImages) {
        handler.postDelayed(() -> {
            eventBus.removeStickyEvent(ImagePickedEvent.class);
            eventBus.postSticky(new ImagePickedEvent(pickImageDelegate.getRequestType(),
                    pickImageDelegate.getRequesterId(),
                    chosenImages));
        }, 400);
    }

    @Override
    public <T> Observable<T> bind(Observable<T> observable) {
        return bindUntilDropView(observable);
    }

    @Override
    public <T> Observable<T> bindUntilStop(Observable<T> observable) {
        return observable.compose(RxLifecycle.bindUntilActivityEvent(lifecycleSubject, ActivityEvent.STOP));
    }

    @Override
    public <T> Observable<T> bindUntilDropView(Observable<T> observable) {
        return observable.compose(RxLifecycle.bindUntilActivityEvent(lifecycleSubject, ActivityEvent.DESTROY));
    }
}
