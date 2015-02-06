package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.ThemeHeaderModel;
import com.worldventures.dreamtrips.utils.busevents.CheckBoxAllThemePressedEvent;
import com.worldventures.dreamtrips.utils.busevents.ToggleThemeVisibilityEvent;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by 1 on 23.01.15.
 */
@Layout(R.layout.adapter_item_activity_header)
public class ThemeHeaderCell extends AbstractCell<ThemeHeaderModel> {

    @InjectView(R.id.checkBoxSelectAllTheme)
    CheckBox checkBoxSelectAll;

    public ThemeHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        checkBoxSelectAll.setChecked(getModelObject().isChecked());
    }

    @OnClick(R.id.checkBoxSelectAllTheme)
    void checkBoxClicked() {
        getModelObject().setChecked(checkBoxSelectAll.isChecked());
        getEventBus().post(new CheckBoxAllThemePressedEvent(checkBoxSelectAll.isChecked()));
    }

    @OnClick(R.id.textViewSelectAllTheme)
    void checkBoxTextViewClicked() {
        checkBoxSelectAll.setChecked(!checkBoxSelectAll.isChecked());
        getModelObject().setChecked(checkBoxSelectAll.isChecked());
        getEventBus().post(new CheckBoxAllThemePressedEvent(checkBoxSelectAll.isChecked()));
    }

    @OnClick(R.id.listHeader)
    void toggleVisibility() {
        getEventBus().post(new ToggleThemeVisibilityEvent());
    }

    @Override
    public void prepareForReuse() {

    }
}

