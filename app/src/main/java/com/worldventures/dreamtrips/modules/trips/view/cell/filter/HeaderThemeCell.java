package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllThemePressedEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleThemeVisibilityEvent;
import com.worldventures.dreamtrips.modules.trips.model.ThemeHeaderModel;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_activity_header)
public class HeaderThemeCell extends AbstractCell<ThemeHeaderModel> {

    @InjectView(R.id.checkBoxSelectAllTheme)
    protected CheckBox checkBoxSelectAll;

    public HeaderThemeCell(View view) {
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
        //nothing to do here
    }
}

