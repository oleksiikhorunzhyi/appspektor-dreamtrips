package com.worldventures.dreamtrips.core.component;

import android.support.v4.app.FragmentManager;

import com.innahema.collections.query.queriables.Queryable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RootComponentsProvider {

    public static class ComponentKeyDuplicateException extends RuntimeException {
        public ComponentKeyDuplicateException(String key) {
            super("Duplicate key for key:" + key);
        }
    }

    private final List<ComponentDescription> activeComponents;

    public RootComponentsProvider(Set<ComponentDescription> descriptions, ComponentsConfig componentConfig) {
        this.activeComponents = buildActiveComponents(descriptions, componentConfig);
    }

    private List<ComponentDescription> buildActiveComponents(Set<ComponentDescription> descriptions,
                                                             ComponentsConfig componentConfig) {

        Map<String, ComponentDescription> componentsMap = new HashMap<>();

        Queryable.from(descriptions).forEachR(cd -> {
            if (componentsMap.containsKey(cd.getKey())) {
                throw new ComponentKeyDuplicateException(cd.getKey());
            }
            componentsMap.put(cd.getKey(), cd);
        });

        return Queryable.from(componentConfig.getActiveComponents()).map(componentsMap::get).toList();
    }

    public List<ComponentDescription> getActiveComponents() {
        return activeComponents;
    }

    public ComponentDescription getComponentByKey(String key) {
        return Queryable.from(activeComponents)
                .firstOrDefault(c -> c.getKey().equalsIgnoreCase(key));
    }

    public ComponentDescription getComponent(FragmentManager fm, int backstackOffset) {
        int size = fm.getBackStackEntryCount();
        int sizeWithOffset = size - backstackOffset;
        for (int i = sizeWithOffset - 1; i >= 0; i--) {
            ComponentDescription component = getComponentByKey(fm.getBackStackEntryAt(i).getName());
            if (component != null) {
                return component;
            }
        }
        return null;
    }
}
