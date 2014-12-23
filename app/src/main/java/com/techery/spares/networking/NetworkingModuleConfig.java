package com.techery.spares.networking;

public class NetworkingModuleConfig {
    private final String serverAddress;

    public NetworkingModuleConfig(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
