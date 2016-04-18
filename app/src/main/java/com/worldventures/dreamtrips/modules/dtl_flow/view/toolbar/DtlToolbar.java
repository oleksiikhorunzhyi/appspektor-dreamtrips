package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.ActionView;
import at.markushi.ui.action.CloseAction;
import at.markushi.ui.action.DrawerAction;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import timber.log.Timber;

/**
 * Custom implementation to wrap up look and behaviour. <br />
 * Actual layout is included from
 * {@link com.worldventures.dreamtrips.R.layout#view_dtl_toolbar_content this layout}
 */
public class DtlToolbar extends LinearLayout {

    @InjectView(R.id.dtlToolbarFirstRow)
    View firstRowLayout;
    @InjectView(R.id.actionViewLayout)
    ViewGroup actionViewLayout;
    @InjectView(R.id.actionView)
    ActionView actionView;
    @InjectView(R.id.dtlToolbarTopCaption)
    AppCompatEditText topCaption;
    @InjectView(R.id.dtlToolbarSecondRow)
    ViewGroup secondRow;
    @InjectView(R.id.dtlToolbarBottomCaption)
    AppCompatEditText bottomCaption;
    //
    @State
    boolean collapsed;
    //
    private String searchQuery;
    private String locationTitle;
    private String defaultEmptySearchCaption = "Food and Drinks"; // TODO :: 4/11/16 move to resources
    private boolean showNavigation;
    //
    private List<CollapseListener> collapseListeners = new ArrayList<>();
    private List<ExpandListener> expandListeners = new ArrayList<>();
    private List<MapClickListener> mapClickListeners = new ArrayList<>();
    private List<NavigationControlListener> navigationControlListeners = new ArrayList<>();

    public DtlToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        initAttributes();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initState();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Essential public methods
    ///////////////////////////////////////////////////////////////////////////

    public void collapse() {
        collapsed = true;
        updateToolbarCaptions();
        animateCollapsing();
        patchInputFields(true);
    }

    public void expand() {
        collapsed = false;
        updateToolbarCaptions();
        animateExpanding();
        patchInputFields(false);
    }

    public void setToolbarCaptions(@Nullable String searchQuery, String locationTitle) {
        this.searchQuery = searchQuery;
        this.locationTitle = locationTitle;
        updateToolbarCaptions();
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void updateAppliedSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Essential private and package-private stuff
    ///////////////////////////////////////////////////////////////////////////

    AppCompatEditText getMerchantSearchView() {
        return topCaption;
    }

    AppCompatEditText getLocationSearchView() {
        return bottomCaption;
    }

    private void initAttributes() {
        // TODO :: 4/12/16 add ability to customize some attrs from xml?
        collapsed = true;
        showNavigation = !ViewUtils.isLandscapeOrientation(getContext());
    }

    private void initState() {
        patchInputFields(collapsed);
        if (collapsed) {
            secondRow.setVisibility(GONE);
            if (!showNavigation) actionViewLayout.setVisibility(INVISIBLE);
            // TODO :: 4/18/16 show proper actionView state?
        } else {
            secondRow.setVisibility(VISIBLE);
            actionViewLayout.setVisibility(VISIBLE);
        }
    }

    /**
     * Deals with focus in our input fields during collapsing/expanding <br />
     * to mimic native Toolbar's SearchView behaviour.
     * @param collapsed boolean indicating whether new state is collapsed
     */
    private void patchInputFields(boolean collapsed) {
        if (collapsed) {
            SoftInputUtil.hideSoftInputMethod(this);
            topCaption.clearFocus();
            bottomCaption.clearFocus();
            topCaption.setFocusable(false);
            topCaption.setFocusableInTouchMode(false);
            topCaption.setInputType(InputType.TYPE_NULL);
        } else {
            topCaption.setFocusable(true);
            topCaption.setFocusableInTouchMode(true);
            topCaption.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            topCaption.requestFocus();
            SoftInputUtil.showSoftInputMethod(topCaption);
        }
    }

    private void updateToolbarCaptions() {
        if (collapsed) {
            String searchQueryTitle =
                    TextUtils.isEmpty(searchQuery) ? defaultEmptySearchCaption : searchQuery;
            if (TextUtils.isEmpty(searchQuery)) {
                topCaption.setHint(searchQueryTitle + " " + locationTitle);
                topCaption.setText("");
            } else topCaption.setText(searchQueryTitle + " " + locationTitle);
        } else {
            if (TextUtils.isEmpty(searchQuery)) topCaption.setHint(defaultEmptySearchCaption);
            topCaption.setText(TextUtils.isEmpty(searchQuery) ? "" : searchQuery);
            bottomCaption.setText(locationTitle);
        }
    }

    private void animateExpanding() {
        if (showNavigation) actionView.setAction(new CloseAction());
        else {
            actionViewLayout.setVisibility(VISIBLE);
            Animator revealNavigationAnimator =
                    ObjectAnimator.ofFloat(actionViewLayout, ALPHA, 0F, 1F);
            revealNavigationAnimator.start();
        }
        //
        secondRow.setVisibility(VISIBLE);
        ValueAnimator heightAnimator = ValueAnimator.ofInt(getHeight(), getHeight() * 2);
        heightAnimator.addUpdateListener(animation -> {
            getLayoutParams().height = (int) animation.getAnimatedValue();
            requestLayout();
        });
        heightAnimator.setInterpolator(new DecelerateInterpolator());
        heightAnimator.start();
    }

    private void animateCollapsing() {
        if (showNavigation) actionView.setAction(new DrawerAction());
        else {
            Animator hideNavigationAnimator =
                    ObjectAnimator.ofFloat(actionViewLayout, ALPHA, 1F, 0F);
            hideNavigationAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    actionViewLayout.setVisibility(INVISIBLE);
                }
            });
            hideNavigationAnimator.start();
        }
        //
        ValueAnimator heightAnimator = ValueAnimator.ofInt(getHeight(), getHeight() / 2);
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        heightAnimator.setStartDelay(75L);
        heightAnimator.addUpdateListener(animation -> {
            getLayoutParams().height = (int) animation.getAnimatedValue();
            requestLayout();
        });
        //
        Animator secondRowAnimator = ObjectAnimator.ofFloat(secondRow, ALPHA, 1F, 0F);
        AnimatorSet collapseSet = new AnimatorSet();
        collapseSet.playTogether(secondRowAnimator, heightAnimator);
        collapseSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                secondRow.setVisibility(GONE);
                secondRow.setAlpha(1F);
            }
        });
        collapseSet.start();
    }

    @OnClick(R.id.actionViewLayout)
    void actionViewClicked(View view) {
        if (collapsed) {
            Queryable.from(navigationControlListeners).forEachR(listener ->
                    listener.onNavigationControlClicked());
        } else {
            collapse();
            Queryable.from(collapseListeners).forEachR(listener -> listener.onCollapsed());
        }
    }

    @OnClick(R.id.dtlToolbarTopCaption)
    void firstRowClicked(View view) {
        if (collapsed) {
            expand();
            Queryable.from(expandListeners).forEachR(listener -> listener.onExpanded());
        }
    }

    @OnClick(R.id.dtlToolbarMapLayout)
    void mapClicked(View view) {
        Queryable.from(mapClickListeners).forEachR(listener -> listener.onMapClicked());
    }

    /**
     * Sugar listener nullability checker-method.
     * @param listener object to check
     * @return true if listener is null
     */
    private boolean checkListenerNull(Object listener) {
        if (listener == null) {
            Timber.e(new IllegalArgumentException(
                            "Listener should not be null! Skipping listener adding"),
                    "Skipping listener adding.");
            return true;
        }
        return false;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////

    public void addMapClickListener(@NonNull MapClickListener listener) {
        if (checkListenerNull(listener)) return;
        mapClickListeners.add(listener);
    }

    public void removeMapClickListener(MapClickListener listener) {
        mapClickListeners.remove(listener);
    }

    public void addNavigationControlClickListener(@NonNull NavigationControlListener listener) {
        if (checkListenerNull(listener)) return;
        navigationControlListeners.add(listener);
    }

    public void removeNavigationControlClickListener(NavigationControlListener listener) {
        navigationControlListeners.remove(listener);
    }

    public void addCollapseListener(@NonNull CollapseListener listener) {
        if (checkListenerNull(listener)) return;
        collapseListeners.add(listener);
    }

    public void removeCollapseListener(CollapseListener listener) {
        collapseListeners.remove(listener);
    }

    public void addExpandListener(@NonNull ExpandListener listener) {
        if (checkListenerNull(listener)) return;
        expandListeners.add(listener);
    }

    public void removeExpandListener(ExpandListener listener) {
        expandListeners.remove(listener);
    }

    public interface CollapseListener {

        void onCollapsed();
    }

    public interface ExpandListener {

        void onExpanded();
    }

    public interface MapClickListener {

        void onMapClicked();
    }

    public interface NavigationControlListener {

        void onNavigationControlClicked();
    }

    ///////////////////////////////////////////////////////////////////////////
    // State saving
    ///////////////////////////////////////////////////////////////////////////

    @Override public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }
}
