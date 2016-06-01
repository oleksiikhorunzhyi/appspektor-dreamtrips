package com.worldventures.dreamtrips.modules.dtl_flow.view.toolbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.ActionView;
import at.markushi.ui.action.CloseAction;
import at.markushi.ui.action.DrawerAction;
import butterknife.InjectView;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

/**
 * Custom implementation to wrap up look and behaviour
 */
public class DtlToolbar extends BaseDtlToolbar {

    private static final boolean DEF_COLLAPSED = true;
    private static final boolean DEF_NAVIGATION_ICON_VISIBLE = true;
    private static final boolean DEF_SHOW_FILTER_BAR = true;
    @DrawableRes
    private static final int DEF_NAVIGATION_ICON = R.drawable.ic_menu_trip_map;

    @InjectView(R.id.dtlToolbarLayout)
    ViewGroup dtlToolbarLayout;
    @InjectView(R.id.dtlToolbarActionViewLayout)
    ViewGroup actionViewLayout;
    @InjectView(R.id.dtlToolbarActionView)
    ActionView actionView;
    @InjectView(R.id.dtlToolbarLocationSearchLayout)
    ViewGroup locationSearchLayout;
    @InjectView(R.id.dtlToolbarNavigationLayout)
    ViewGroup dtlNavigationControl;
    @InjectView(R.id.dtlToolbarNavigationIcon)
    ImageView dtlToolbarNavigationIcon;
    @InjectView(R.id.dtlToolbarFilterBarRoot)
    ViewGroup filterBarRoot;
    //
    protected List<CollapseListener> collapseListeners = new ArrayList<>();
    protected List<ExpandListener> expandListeners = new ArrayList<>();
    protected List<NavigationClickListener> navigationClickListeners = new ArrayList<>();
    protected List<NavigationControlListener> navigationControlListeners = new ArrayList<>();
    //
    @State
    boolean collapsed;
    //
    boolean navigationControlVisible;
    @DrawableRes
    int navigationIconResource;
    private boolean showNavigation;
    private boolean showFilterBar;

    public DtlToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Essential private and package-private stuff
    ///////////////////////////////////////////////////////////////////////////

    private void collapse() {
        collapsed = true;
        updateToolbarCaptions();
        animateCollapsing();
        patchInputFields(true);
        Queryable.from(collapseListeners).forEachR(listener -> listener.onCollapsed());
    }

    private void expand() {
        collapsed = false;
        updateToolbarCaptions();
        animateExpanding();
        patchInputFields(false);
        Queryable.from(expandListeners).forEachR(listener -> listener.onExpanded());
    }

    @Override
    protected void initAttributes(AttributeSet attrs) {
        super.initAttributes(attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DtlToolbar);
        collapsed = a.getBoolean(R.styleable.DtlToolbar_dtlt_collapsed, DEF_COLLAPSED);
        navigationControlVisible = a.getBoolean(R.styleable.DtlToolbar_dtlt_navigation_icon_visible,
                DEF_NAVIGATION_ICON_VISIBLE);
        showFilterBar = a.getBoolean(R.styleable.DtlToolbar_dtlt_show_filter_bar_portrait,
                DEF_SHOW_FILTER_BAR);
        navigationIconResource = a.getResourceId(R.styleable.DtlToolbar_dtlt_navigation_icon_src,
                DEF_NAVIGATION_ICON);
        focusedMode = FocusedMode.fromAttribute(a.getInt(R.styleable.DtlToolbar_dtlt_focused_mode,
                DEF_FOCUSED_MODE.id));
        a.recycle();
        if (focusedMode != FocusedMode.UNDEFINED) collapsed = false;
        showNavigation = !ViewUtils.isLandscapeOrientation(getContext());
    }

    @Override
    protected void initState() {
        patchInputFields(collapsed);
        if (collapsed) {
            locationSearchLayout.setVisibility(GONE);
            actionView.setAction(new DrawerAction(), false);
            if (!showNavigation) actionViewLayout.setVisibility(INVISIBLE);
        } else {
            locationSearchLayout.setVisibility(VISIBLE);
            actionViewLayout.setVisibility(VISIBLE);
            actionView.setAction(new CloseAction(), false);
        }
        dtlToolbarNavigationIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),
                navigationIconResource));
        dtlNavigationControl.setVisibility(navigationControlVisible ? VISIBLE : INVISIBLE);
        filterBarRoot.setVisibility(showFilterBar ? VISIBLE : GONE);
    }

    @Override
    protected void bindSearchQueryPersisting() {
        RxDtlToolbar.merchantSearchTextChanges(this)
                .skip(1)
                .filter(s -> !isCollapsed())
                .compose(RxLifecycle.bindView(this))
                .subscribe(searchQuery -> this.searchQuery = searchQuery);
    }

    /**
     * Deals with focus in our input fields during collapsing/expanding <br />
     * to mimic native Toolbar's SearchView behaviour.
     * @param collapsed boolean indicating whether new state is collapsed
     */
    private void patchInputFields(boolean collapsed) {
        if (collapsed) {
            SoftInputUtil.hideSoftInputMethod(this);
            merchantSearchInput.clearFocus();
            locationSearchInput.clearFocus();
            merchantSearchInput.setFocusable(false);
            merchantSearchInput.setFocusableInTouchMode(false);
            merchantSearchInput.setInputType(InputType.TYPE_NULL);
        } else {
            merchantSearchInput.setFocusable(true);
            merchantSearchInput.setFocusableInTouchMode(true);
            merchantSearchInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            //
            if (focusedMode != FocusedMode.UNDEFINED) {
                switch (focusedMode) {
                    case SEARCH:
                        merchantSearchInput.requestFocus();
                        SoftInputUtil.showSoftInputMethod(getContext());
                        break;
                    case LOCATION:
                        SoftInputUtil.showSoftInputMethod(getContext());
                        locationSearchInput.requestFocus();
                        break;
                }
            }
            // due to possible bug in EditText - hint not saved after rotation. Fix below:
            if (TextUtils.isEmpty(searchQuery) || TextUtils.isEmpty(merchantSearchInput.getHint())) {
                merchantSearchInput.setHint(defaultEmptySearchCaption);
            }
        }
    }

    @Override
    protected void updateToolbarCaptions() {
        if (collapsed) {
            final String searchQueryTitle =
                    TextUtils.isEmpty(searchQuery) ? defaultEmptySearchCaption : searchQuery;
            if (TextUtils.isEmpty(searchQuery)) {
                merchantSearchInput.setHint(searchQueryTitle + " " + locationTitle);
                merchantSearchInput.setText("");
            } else {
                merchantSearchInput.setText(prepareSpannedTopCaption(searchQueryTitle, locationTitle));
            }
        } else {
            if (TextUtils.isEmpty(searchQuery)) {
                merchantSearchInput.setHint(defaultEmptySearchCaption);
            }
            merchantSearchInput.setText(TextUtils.isEmpty(searchQuery) ? "" : searchQuery);
            locationSearchInput.setText(locationTitle);
            locationSearchInput.selectAll();
        }
    }

    private SpannableStringBuilder prepareSpannedTopCaption(String searchQuery,
                                                            String locationTitle) {
        final SpannableStringBuilder stringBuilder =
                new SpannableStringBuilder(searchQuery + " " + locationTitle);
        final ForegroundColorSpan colorSpan = new ForegroundColorSpan(getContext().getResources()
                .getColor(R.color.dtlt_input_hint_color));
        stringBuilder.setSpan(colorSpan, searchQuery.length(), stringBuilder.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return stringBuilder;
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
        locationSearchLayout.setVisibility(VISIBLE);
        ValueAnimator heightAnimator = ValueAnimator.ofInt(dtlToolbarLayout.getHeight(),
                dtlToolbarLayout.getHeight() * 2);
        heightAnimator.addUpdateListener(animation -> {
            dtlToolbarLayout.getLayoutParams().height = (int) animation.getAnimatedValue();
            dtlToolbarLayout.requestLayout();
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
        ValueAnimator heightAnimator = ValueAnimator.ofInt(dtlToolbarLayout.getHeight(),
                dtlToolbarLayout.getHeight() / 2);
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        heightAnimator.setStartDelay(75L);
        heightAnimator.addUpdateListener(animation -> {
            dtlToolbarLayout.getLayoutParams().height = (int) animation.getAnimatedValue();
            dtlToolbarLayout.requestLayout();
        });
        //
        Animator secondRowAnimator = ObjectAnimator.ofFloat(locationSearchLayout, ALPHA, 1F, 0F);
        AnimatorSet collapseSet = new AnimatorSet();
        collapseSet.playTogether(secondRowAnimator, heightAnimator);
        collapseSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                locationSearchLayout.setVisibility(GONE);
                locationSearchLayout.setAlpha(1F);
            }
        });
        collapseSet.start();
    }

    @Override
    protected void onMerchantSearchInputClicked() {
        if (collapsed) {
            expand();
        }
    }

    @OnClick(R.id.dtlToolbarActionViewLayout)
    void actionViewClicked(View view) {
        if (collapsed) {
            Queryable.from(navigationControlListeners).forEachR(listener ->
                    listener.onNavigationControlClicked());
        } else {
            collapse();
        }
    }

    @OnClick(R.id.dtlToolbarNavigationLayout)
    void navigationClicked(View view) {
        Queryable.from(navigationClickListeners)
                .forEachR(listener -> listener.onNavigationClicked());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Listeners
    ///////////////////////////////////////////////////////////////////////////

    public interface CollapseListener {

        void onCollapsed();
    }

    public interface ExpandListener {

        void onExpanded();
    }

    public interface NavigationClickListener {

        void onNavigationClicked();
    }

    public interface NavigationControlListener {

        void onNavigationControlClicked();
    }

    public void addNavigationClickListener(@NonNull NavigationClickListener listener) {
        if (checkListenerNull(listener)) return;
        navigationClickListeners.add(listener);
    }

    public void removeNavigationClickListener(NavigationClickListener listener) {
        navigationClickListeners.remove(listener);
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
