package at.reder.virussim.model;

import at.reder.virussim.model.Host.HealthStatus;
import java.util.Map;

/**
 *
 * @author Alexander Reer
 */
public class Statistics {

    protected final int timestamp;
    protected Map<HealthStatus, Integer> hostStatus;

    public Statistics(int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    public int getTotalHosts() {
        int totalHosts = 0;
        totalHosts = this.hostStatus.values().stream().map(v -> v).reduce(totalHosts, Integer::sum);
        return totalHosts;
    }

    /**
     * @return the hostStatus
     */
    public Map<HealthStatus, Integer> getHostStatus() {
        return hostStatus;
    }

    /**
     * @param hostStatus the hostStatus to set
     */
    public void setHostStatus(Map<HealthStatus, Integer> hostStatus) {
        this.hostStatus = hostStatus;
    }

}
