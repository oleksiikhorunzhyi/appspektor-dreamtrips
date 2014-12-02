package com.worldventures.dreamtrips.pm;

import android.test.InstrumentationTestCase;

import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.DataManager;

public abstract class BasePMTest extends InstrumentationTestCase {
    private DataManager dataManager;

    public void setUp() throws Exception {
        super.setUp();
        DTApplication app = (DTApplication) this.getInstrumentation().getTargetContext().getApplicationContext();
        dataManager = new DataManager(app);
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}
