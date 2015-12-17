package com.worldventures.dreamtrips.modules.common.view.custom.tagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.worldventures.dreamtrips.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ExistsTagView extends TagView implements View.OnClickListener {

    private static final long HIDE_DELETE_BUTTON_DELAY = 1000;

    @InjectView(R.id.tagged_user_name)
    public TextView taggedUserName;
    @InjectView(R.id.tagged_user_delete_tag)
    public Button btnDeleteTag;

    private Runnable hideDeleteBtnRunnable = () -> btnDeleteTag.setVisibility(View.INVISIBLE);

    //region Constructors
    public ExistsTagView(Context context) {
        super(context);
    }

    public ExistsTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExistsTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExistsTagView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //endregion

    @Override
    protected void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view_exist, this, true);
        ButterKnife.inject(this);
        setClickable(true);
        setOnClickListener(this);
        //todo uncomment
//        taggedUserName.setText(user.getFullName());
        taggedUserName.setText("Tagged Userovich");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(hideDeleteBtnRunnable);
    }

    @Override
    public void onClick(View v) {
        showDeleteButton();
    }

    private void showDeleteButton(){
        btnDeleteTag.setVisibility(View.VISIBLE);
        postDelayed(hideDeleteBtnRunnable, HIDE_DELETE_BUTTON_DELAY);
    }

    @OnClick({R.id.tagged_user_delete_tag})
    public void onDeleteTag(View view) {
        deleteTag();
    }
}