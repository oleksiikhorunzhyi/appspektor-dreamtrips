package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.SuggestionHelpView;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.Position;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SuggestionTagView extends TagView<TagSuggestionActionListener> {

    static int i = 0;
    @InjectView(R.id.suggestion_frame_container)
    View suggestionFrameContainer;
    @InjectView(R.id.test)
    TextView textView;
    private SuggestionHelpView suggestionHelpView;

    int additionalSize;

    public SuggestionTagView(Context context) {
        super(context);
    }

    public SuggestionTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuggestionTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initialize() {
        LayoutInflater.from(getContext()).inflate(getLayout(), this, true);
        ButterKnife.inject(this);
        additionalSize = getResources().getDimensionPixelSize(R.dimen.photo_tag_frame_additional_size);
        textView.setText("" + i++);
    }

    protected int getLayout() {
        return R.layout.layout_tag_view_suggestion;
    }

    @OnClick(R.id.suggestion_frame_container)
    protected void onFrameClicked() {
        tagListener.onFrameClicked(photoTag);
    }

    @Override
    protected void setupPointers() {
        //nothing to do
    }

    @Override
    public void setPhotoTag(PhotoTag photoTag) {
        super.setPhotoTag(photoTag);
        Position bottomRight = getAbsoluteTagPosition().getBottomRight();
        Position topLeft = getAbsoluteTagPosition().getTopLeft();
        suggestionFrameContainer.getLayoutParams().width = (int) (bottomRight.getX() - topLeft.getX()) + additionalSize;
        suggestionFrameContainer.getLayoutParams().height = (int) (bottomRight.getY() - topLeft.getY()) + additionalSize;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        layoutParams.leftMargin = layoutParams.leftMargin - getSize().getWidth() / 2 + additionalSize;
        layoutParams.topMargin = layoutParams.topMargin - getSize().getHeight() + additionalSize;
    }

    public void setSuggestionHelpView(SuggestionHelpView suggestionHelpView) {
        this.suggestionHelpView = suggestionHelpView;
    }

    private void removeHelpView() {
        if (suggestionHelpView == null || suggestionHelpView.getParent() == null) return;
        //
        ((ViewGroup) suggestionHelpView.getParent()).removeView(suggestionHelpView);
    }

    @Override
    protected void onDetachedFromWindow() {
        // removeHelpView();
        super.onDetachedFromWindow();
    }
}
