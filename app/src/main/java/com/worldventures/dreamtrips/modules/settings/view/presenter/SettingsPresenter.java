package com.worldventures.dreamtrips.modules.settings.view.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.settings.api.UpdateSettingsCommand;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.model.SettingsGroup;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;
import com.worldventures.dreamtrips.modules.settings.util.SettingsManager;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;

public class SettingsPresenter extends Presenter<SettingsPresenter.View> {

    @State
    ArrayList<Setting> settingsList;
    @State
    ArrayList<Setting> immutableSettingsList;

    @Inject
    SnappyRepository db;

    private SettingsGroup group;

    public SettingsPresenter(SettingsGroup group) {
        this.group = group;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        SettingsFactory settingsFactory = new SettingsFactory();
        if (this.settingsList == null)
            this.settingsList = (ArrayList<Setting>) SettingsManager.merge(db.getSettings(),
                    settingsFactory.createSettings(group));
        //
        if (immutableSettingsList == null)
            immutableSettingsList = cloneList(this.settingsList);
        //
        view.setSettings(settingsList);
    }

    private ArrayList<Setting> cloneList(List<Setting> settingsList) {
        ArrayList<Setting> cloneList = new ArrayList<>();
        Queryable.from(settingsList).forEachR(setting -> cloneList.add(SerializationUtils.clone(setting)));
        return cloneList;
    }

    private List<Setting> getChanges() {
        return Queryable.from(settingsList).filter(setting ->
                Queryable.from(immutableSettingsList)
                        .filter(changeSetting -> changeSetting.equals(setting) &&
                                !setting.getValue().equals(changeSetting.getValue()))
                        .firstOrDefault() != null).toList();
    }

    public boolean isSettingsChanged() {
        return getChanges().size() > 0;
    }

    public void applyChanges(boolean withClose) {
        if (isSettingsChanged()) {
            view.showLoading();
            List<Setting> changes = getChanges();
            doRequest(new UpdateSettingsCommand(changes),
                    aVoid -> {
                        db.saveSettings(changes);
                        immutableSettingsList = cloneList(this.settingsList);
                        //
                        view.hideLoading();
                        if (withClose)
                            view.close();
                    });
        }
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        view.hideLoading();
    }

    public interface View extends Presenter.View {

        void setSettings(List<Setting> settingsList);

        void showLoading();

        void hideLoading();

        void close();
    }
}
