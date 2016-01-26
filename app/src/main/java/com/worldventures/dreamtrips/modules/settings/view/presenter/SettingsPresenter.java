package com.worldventures.dreamtrips.modules.settings.view.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.settings.api.UpdateSettingsCommand;
import com.worldventures.dreamtrips.modules.settings.model.Settings;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsPresenter extends Presenter<SettingsPresenter.View> {

    private List<Settings> settingsList;
    private List<Settings> immutableSettingsList;

    public SettingsPresenter(SettingsGroup group, List<Settings> settingsList) {
        SettingsManager settingsManager = new SettingsManager();
        SettingsFactory settingsFactory = new SettingsFactory();
        this.settingsList = settingsManager.merge(settingsList, settingsFactory.createSettings(group));
        immutableSettingsList = cloneList(settingsList);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.setSettings(settingsList);
    }

    private List<Settings> cloneList(List<Settings> settingsList) {
        List<Settings> cloneList = new ArrayList<>();
        Queryable.from(settingsList).forEachR(setting -> cloneList.add(SerializationUtils.clone(setting)));
        return cloneList;
    }

    public void applyChanges() {
        List<Settings> changes = Queryable.from(settingsList).filter(setting ->
                Queryable.from(immutableSettingsList)
                        .filter(changeSetting -> changeSetting.equals(setting) && setting.getValue() != changeSetting.getValue())
                        .firstOrDefault() != null).toList();
        //
        if (changes.size() > 0) doRequest(new UpdateSettingsCommand(changes));
    }

    public interface View extends Presenter.View {

        void setSettings(List<Settings> settingsList);
    }
}
