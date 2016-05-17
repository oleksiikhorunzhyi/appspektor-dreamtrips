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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

    private static final boolean DEF_COLLAPSED = true;
    private static final boolean DEF_NAVIGATION_ICON_VISIBLE = true;
    @DrawableRes
    private static final int DEF_NAVIGATION_ICON = R.drawable.ic_menu_trip_map;
    private static final FocusedMode DEF_FOCUSED_MODE = FocusedMode.UNDEFINED;

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
    @InjectView(R.id.dtlToolbarNavigationLayout)
    ViewGroup dtlNavigationControl;
    @InjectView(R.id.dtlToolbarNavigationIcon)
    ImageView dtlToolbarNavigationIcon;
    @InjectView(R.id.dtlToolbarBottomCaption)
    AppCompatEditText bottomCaption;
    //
    @State
    boolean collapsed;
    //
    private FocusedMode focusedMode;
    boolean navigationControlVisible;
    @DrawableRes
    int navigationIconResource;
    private String searchQuery;
    private String locationTitle;
    private String defaultEmptySearchCaption;
    private boolean showNavigation;
    //
    private List<CollapseListener> collapseListeners = new ArrayList<>();
    private List<ExpandListener> expandListeners = new ArrayList<>();
    private List<NavigationClickListener> navigationClickListeners = new ArrayList<>();
    private List<NavigationControlListener> navigationControlListeners = new ArrayList<>();

    public DtlToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.TOP);
        setFocusableInTouchMode(true);
        setBackgroundColor(ContextCompat.getColor(context, R.color.theme_main));
        initAttributes(attrs);
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
        bindSearchQueryPersisting();
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

    ///////////////////////////////////////////////////////////////////////////
    // Essential private and package-private stuff
    ///////////////////////////////////////////////////////////////////////////

    private void bindSearchQueryPersisting() {
        RxDtlToolbar.merchantSearchTextChanges(this)
                .skip(1)
                .filter(s -> !isCollapsed())
                .compose(RxLifecycle.bindView(this))
                .subscribe(searchQuery -> this.searchQuery = searchQuery);
    }

    AppCompatEditText getMerchantSearchView() {
        return topCaption;
    }

    AppCompatEditText getLocationSearchView() {
        return bottomCaption;
    }

    private void initAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DtlToolbar);
        collapsed = a.getBoolean(R.styleable.DtlToolbar_dtlt_collapsed, DEF_COLLAPSED);
        navigationControlVisible = a.getBoolean(R.styleable.DtlToolbar_dtlt_navigation_icon_visible,
                DEF_NAVIGATION_ICON_VISIBLE);
        navigationIconResource = a.getResourceId(R.styleable.DtlToolbar_dtlt_navigation_icon_src,
                DEF_NAVIGATION_ICON);
        focusedMode = FocusedMode.fromAttribute(a.getInt(R.styleable.DtlToolbar_dtlt_focused_mode,
                DEF_FOCUSED_MODE.id));
        a.recycle();
        if (focusedMode != FocusedMode.UNDEFINED) collapsed = false;
        showNavigation = !ViewUtils.isLandscapeOrientation(getContext());
        defaultEmptySearchCaption = getResources().getString(R.string.dtlt_search_hint);
    }

    private void initState() {
        patchInputFields(collapsed);
        if (collapsed) {
            secondRow.setVisibility(GONE);
            actionView.setAction(new DrawerAction(), false);
            if (!showNavigation) actionViewLayout.setVisibility(INVISIBLE);
        } else {
            secondRow.setVisibility(VISIBLE);
            actionViewLayout.setVisibility(VISIBLE);
            actionView.setAction(new CloseAction(), false);
        }
        dtlToolbarNavigationIcon.setImageDrawable(ContextCompat.getDrawable(getContext(),
                navigationIconResource));
        dtlNavigationControl.setVisibility(navigationControlVisible ? VISIBLE : INVISIBLE);
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
            //
            if (focusedMode != FocusedMode.UNDEFINED) {
                switch (focusedMode) {
                    case SEARCH:
                        topCaption.requestFocus();
                        SoftInputUtil.showSoftInputMethod(getContext());
                        break;
                    case LOCATION:
                        SoftInputUtil.showSoftInputMethod(getContext());
                        bottomCaption.requestFocus();
                        break;
                }
            }
            // due to possible bug in EditText - hint not saved after rotation. Fix below:
            if (TextUtils.isEmpty(searchQuery) || TextUtils.isEmpty(topCaption.getHint())) {
                topCaption.setHint(defaultEmptySearchCaption);
            }
        }
    }

    private void updateToolbarCaptions() {
        if (collapsed) {
            final String searchQueryTitle =
                    TextUtils.isEmpty(searchQuery) ? defaultEmptySearchCaption : searchQuery;
            if (TextUtils.isEmpty(searchQuery)) {
                topCaption.setHint(searchQueryTitle + " " + locationTitle);
                topCaption.setText("");
            } else {
                topCaption.setText(prepareSpannedTopCaption(searchQueryTitle, locationTitle));
            }
        } else {
            if (TextUtils.isEmpty(searchQuery)) {
                topCaption.setHint(defaultEmptySearchCaption);
            }
            topCaption.setText(TextUtils.isEmpty(searchQuery) ? "" : searchQuery);
            bottomCaption.setText(locationTitle);
            bottomCaption.selectAll();
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
            focusedMode = FocusedMode.SEARCH;
            expand();
            Queryable.from(expandListeners).forEachR(listener -> listener.onExpanded());
        }
    }

    @OnClick(R.id.dtlToolbarNavigationLayout)
    void navigationClicked(View view) {
        Queryable.from(navigationClickListeners).forEachR(listener -> listener.onNavigationClicked());
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

    ///////////////////////////////////////////////////////////////////////////
    // State saving
    ///////////////////////////////////////////////////////////////////////////

    @Override public Parcelable onSaveInstanceState() {
        return Icepick.saveInstanceState(this, super.onSaveInstanceState());
    }

    @Override public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(Icepick.restoreInstanceState(this, state));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Attributes enum
    ///////////////////////////////////////////////////////////////////////////

    public enum FocusedMode {
        UNDEFINED(0), SEARCH(1), LOCATION(2);

        private int id;

        FocusedMode(int attributeId) {
            this.id = attributeId;
        }

        public static FocusedMode fromAttribute(int attributeId) {
            for (FocusedMode value : values()) {
                if (value.id == attributeId) return value;
            }
            throw new IllegalArgumentException("DtlToolbar: wrong argument provided for focused" +
                    " mode attribute: must be one of " +
                    Queryable.from(values()).joinStrings(" ", element -> element.name()));
        }
    }
}
