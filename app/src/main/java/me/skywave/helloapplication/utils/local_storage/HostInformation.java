package me.skywave.helloapplication.utils.local_storage;

public class HostInformation {
    private String hostId;
    private String name;
    private boolean allowed;

    public HostInformation(String hostId, String name, boolean allowed) {
        setHostId(hostId);
        setName(name);
        setAllowed(allowed);
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    public String getHostId() {
        return hostId;
    }

    public String getName() {
        return name;
    }

    public boolean isAllowed() {
        return allowed;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, %d}", hostId, name, allowed ? 1 : 0);
    }
}
