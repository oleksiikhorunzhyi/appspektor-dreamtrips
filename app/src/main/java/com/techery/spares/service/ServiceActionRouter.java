package com.techery.spares.service;

import android.content.Intent;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ServiceActionRouter {

    private Map<String, ActionBody> actionsMap = new HashMap<>();
    private Gson gson = new Gson();

    public interface ActionBody {
        public void run(Intent intent);
    }

    public interface ActionBodyWithPayload<T> {
        public void run(T payload);
    }

    public void dispatchIntent(Intent intent) {
        checkNotNull(intent);

        final String action = intent.getAction();
        checkNotNull(action);

        ActionBody body = actionsMap.get(action);

        if (body != null) {
            body.run(intent);
        } else {
            throw new IllegalArgumentException("Unknown action:" + action + " for service:" + intent.getComponent().getClassName());
        }
    }

    public void on(String action, ActionBody body) {
        checkNotNull(action);
        checkNotNull(body);

        actionsMap.put(action, body);
    }

    public <T> void on(String action, Class<T> clazz, ActionBodyWithPayload<T> body) {
        checkNotNull(action);
        checkNotNull(body);

        actionsMap.put(action, (intent) -> {
            String jsonPayload = intent.getStringExtra(InjectingService.EXTRA_PAYLOAD);

            if (jsonPayload != null) {
                T payload = this.gson.fromJson(jsonPayload, clazz);

                body.run(payload);
            } else {
                body.run(null);
            }
        });
    }

    public <T> void on( Class<T> clazz, ActionBodyWithPayload<T> body) {
        on(clazz.getName(), clazz, body);
    }
}
