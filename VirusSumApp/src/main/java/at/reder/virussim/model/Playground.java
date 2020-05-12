package at.reder.virussim.model;

import at.reder.virussim.listener.PlaygroundChangedListener;
import at.reder.virussim.listener.TimeChangedListener;
import static at.reder.virussim.model.Host.HealthStatus.HEALTHY;
import static at.reder.virussim.model.Host.HealthStatus.INFECTED;
import static at.reder.virussim.model.Host.HealthStatus.TRIGGERED;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alex
 */
public class Playground implements TimeChangedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Playground.class);
    private static final Random VARIATION_RANDOM = new Random();
    private static final int RESOLUTION = 1000;
    private static final float INFACTION_RATE = 0.05f;
    private final int maxX;
    private final int maxY;
    private final Host[][] hosts;
    private final Host[][] oldHosts;
    private final Map<Host, int[]> hostVector;
    private final Set<PlaygroundChangedListener> playgroundChangedListener;
    private final int[] probabilityArray;
    private final Virus virus;
    private boolean allowSikToMove;

    public Playground(int maxX, int maxY, float density, int initialInfected, Virus virus) {
        this(maxX, maxY, Math.round(maxX * maxY * density), initialInfected, virus);
    }

    public Playground(int maxX, int maxY, int maxHosts, int initialInfected, Virus virus) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.hosts = new Host[this.maxX][this.maxY];
        this.oldHosts = new Host[this.maxX][this.maxY];
        this.hostVector = new HashMap<>(maxHosts);
        this.playgroundChangedListener = new HashSet<>();
        this.probabilityArray = new int[RESOLUTION];
        this.virus = virus;
        this.allowSikToMove = false;
        initHosts(maxHosts, initialInfected);
    }

    public void addPlaygroundChangeListener(PlaygroundChangedListener pcl) {
        this.playgroundChangedListener.add(pcl);
    }

    public void removePlaygroundChangedListener(PlaygroundChangedListener pcl) {
        this.playgroundChangedListener.remove(pcl);
    }

    public Virus getVirus() {
        return this.virus;
    }

    public boolean isAllowSikToMove() {
        return this.allowSikToMove;
    }

    public void setAllowSikToMove(boolean allowSikToMove) {
        this.allowSikToMove = allowSikToMove;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxY() {
        return this.maxY;
    }

    public Host getHost(int x, int y) {
        return getHost(this.hosts, x, y);
    }

    public Host getOldHost(int x, int y) {
        return getHost(this.oldHosts, x, y);
    }

    private Host getHost(Host[][] hostGrid, int x, int y) {
        Host host;
        if (x >= 0 && y >= 0 && hostGrid.length > x && hostGrid[0].length > y) {
            host = hostGrid[x][y];
        } else {
            host = null;
        }
        return host;
    }

    @Override
    public void timeChanged(int timestamp) {
        // 1) copy host grid
        copyHostGrid();
        // 2) calculate new host infections
        calculateHostInfections(timestamp);
        // 3) clear host grid
        clearHostGrid();
        // 4) Calculate new host positions
        moveHosts();
        // 5) save new host grid
        Statistics statistics = logStatistics(timestamp);
        // 6) clear old host grid
        clearOldHostGrid();
        playgroundChanged(statistics);
    }

    private Statistics logStatistics(int timestamp) {
        Statistics statistics = new Statistics(timestamp);
        Map<Host.HealthStatus, Integer> result = new EnumMap<>(Host.HealthStatus.class);
        gridIterator((int x, int y) -> {
            if (this.hosts[x][y] != null) {
                Host host = this.hosts[x][y];
                if (!result.containsKey(host.getHealthStatus())) {
                    result.put(host.getHealthStatus(), 1);
                } else {
                    result.put(host.getHealthStatus(), result.get(host.getHealthStatus()) + 1);
                }
            }
        });
        statistics.setHostStatus(result);
        StringBuilder logString = new StringBuilder(64);
        logString.append(timestamp).append(": ");
        for (Host.HealthStatus healthStatus : Host.HealthStatus.values()) {
            int count = 0;
            if (result.containsKey(healthStatus) && result.get(healthStatus) != null) {
                count = result.get(healthStatus);
            }
            logString.append(count).append(" Hosts with status ").append(healthStatus).append(", ");
        }
        LOGGER.info(logString.substring(0, logString.length() - 3));
        return statistics;
    }

    private void initHosts(int maxHosts, int initialInfected) {
        long start = System.currentTimeMillis();
        if (maxHosts > (this.maxX * this.maxY * 0.75)) {
            maxHosts = Math.round(this.maxX * this.maxY * 0.75f);
            LOGGER.warn("Too much hosts defined. Using {} hosts", maxHosts);
        }
        setProbabilityArray(INFACTION_RATE);
        int infectedCount = 0;
        for (int hostId = 0; hostId < maxHosts; hostId++) {
            int x;
            int y;
            do {
                x = VARIATION_RANDOM.nextInt(this.maxX);
                y = VARIATION_RANDOM.nextInt(this.maxY);
            } while (this.hosts[x][y] != null);
            Host host = generateHost(hostId);
            if (initialInfected > 0) {
                initialInfected--;
                infectedCount++;
                LOGGER.info("Host {} initially virus infected", host);
                host.setVirus(this.virus, 0);
            }
            this.hosts[x][y] = host;
            this.oldHosts[x][y] = null;
            this.hostVector.put(host, new int[]{x, y});
        }
        playgroundChanged(new Statistics(0));
        LOGGER.info("Initializing playground ({}/{}) with {} hosts, {} infected,  finished in {}ms", this.maxX, this.maxY, maxHosts, infectedCount, System.currentTimeMillis() - start);
    }

    private Host generateHost(int id) {
        Host host = new Host(id);
        host.setMobilityRadius((this.maxX + this.maxY) / 6);
        host.setMobilityRadiusDeviation(0.3f);
        return host;
    }

    private void clearHostGrid() {
        gridIterator((int x, int y) -> {
            if (this.allowSikToMove || (this.hosts[x][y] != null && this.hosts[x][y].getHealthStatus() != Host.HealthStatus.TRIGGERED)) {
                this.hosts[x][y] = null;
            }
        });
    }

    private void clearOldHostGrid() {
        gridIterator((int x, int y) -> this.oldHosts[x][y] = null);
    }

    private void moveHosts() {
        long start = System.currentTimeMillis();
        this.hostVector.keySet().forEach(this::move);
        LOGGER.info("Calculating new host infection/position takes {}ms", System.currentTimeMillis() - start);
    }

    private void move(Host host) {
        int[] pos = this.hostVector.get(host);
        int[] newPos = new int[2];
        int[] move;
        if (this.allowSikToMove || host.getHealthStatus() != TRIGGERED) {
            do {
                move = host.getMove();
                LOGGER.debug("Pos: {}/{}, Vector: {}/{}", pos[0], pos[1], move[0], move[1]);
                if (pos[0] + move[0] >= this.maxX) {
                    newPos[0] = Math.min(this.maxX - 1, pos[0] + move[0] - this.maxX);
                } else if (pos[0] + move[0] < 0) {
                    newPos[0] = Math.max(0, this.maxX + pos[0] + move[0]);
                } else {
                    newPos[0] = pos[0] + move[0];
                }
                if (pos[1] + move[1] >= this.maxY) {
                    newPos[1] = Math.min(this.maxY - 1, pos[1] + move[1] - this.maxY);
                } else if (pos[1] + move[1] < 0) {
                    newPos[1] = Math.max(0, this.maxY + pos[1] + move[1]);
                } else {
                    newPos[1] = pos[1] + move[1];
                }
            } while (this.hosts[newPos[0]][newPos[1]] != null);
            this.hosts[newPos[0]][newPos[1]] = host;
            this.hostVector.put(host, newPos);
        }
    }

    private void calculateHostInfections(int timestamp) {
        long start = System.currentTimeMillis();
        gridIterator((int x, int y) -> {
            Host host = this.hosts[x][y];
            if (host != null && host.getVirus() != null) {
                switch (host.getHealthStatus()) {
                    case INFECTED:
                        // host infected after getting a virus
                        // timestamp >= virus timestamp + incubation time + incubation variation = infection timestamp
                        int incubationTime = this.virus.getTriggerPeriod().getValue();
                        if (timestamp >= incubationTime + host.getInfectionTimestamp()) {
                            host.setTriggeredTimestamp(timestamp);
                            LOGGER.info("Host {} became triggerd at {} with virus {}", host, timestamp, virus);
                        }
                        break;
                    case TRIGGERED:
                        // timestamp >= infection timestamp + healing time + healing variation = healed timestamp
                        int healingTime = this.virus.getHealingPeriod().getValue();
                        if (timestamp >= healingTime + host.getInfectionTimestamp()) {
                            host.setImmuneTimestamp(timestamp);
                            LOGGER.info("Host {} became immune at {} with virus {}", host, timestamp, this.virus);
                        }
                        break;
                    case IMMUNE:
                        // check immunity status
                        // timestamp >= healing timestamp + immuinity period + immunity variation
                        if (virus.getImmunityPeriod().getBase() >= 0) {
                            int immunityTime = this.virus.getImmunityPeriod().getValue();
                            if (timestamp >= immunityTime + host.getImmuneTimestamp()) {
                                host.setVirus(null, -1);
                                host.setTriggeredTimestamp(-1);
                                host.setImmuneTimestamp(-1);
                                LOGGER.info("Host ({}) immunity for virus {} ended at {}", host, this.virus, timestamp);
                            }
                        }
                        break;
                }
            }
            Host oldHost = this.oldHosts[x][y];
            if (oldHost != null
                && (oldHost.getHealthStatus() == INFECTED || oldHost.getHealthStatus() == TRIGGERED)) {
                // search neigbours in infectionRadius
                Set<Host> neighbours = getNeighbours(x, y, virus.getInfectionRadius());
                // infection = infectionProbability / distace
                neighbours.forEach(neighbour -> {
                    int[] pos = this.hostVector.get(neighbour);
                    float distance = (float) Math.sqrt(Math.pow(pos[0] - x, 2) + Math.pow(pos[1] - y, 2));
                    float infection = virus.getInfectionProbability() / distance;
                    LOGGER.debug("Infection probability for host {}={}", neighbour.getId(), infection);
                    setProbabilityArray(infection);
                    boolean infected = this.probabilityArray[VARIATION_RANDOM.nextInt(RESOLUTION - 1)] == 1;
                    if (infected) {
                        LOGGER.info("Host {} get virus at {}", neighbour, timestamp);
                        neighbour.setVirus(this.virus, timestamp);
                    }
                });
            }
        });
        LOGGER.info("Calculating host infections finishes after {}ms", System.currentTimeMillis() - start);
    }

    private void setProbabilityArray(float probability) {
        int maxProb = Math.round(RESOLUTION * probability);
        for (int i = 0; i < maxProb; i++) {
            this.probabilityArray[i] = 1;
        }
        for (int i = maxProb; i < RESOLUTION; i++) {
            this.probabilityArray[i] = 0;
        }
    }

    private Set<Host> getNeighbours(int x, int y, int infectionRadius) {
        Set<Host> neighbours = new HashSet<>();
        int xMin = Math.max(0, x - infectionRadius);
        int xMax = Math.min(this.maxX - 1, x + infectionRadius);
        int yMin = Math.max(0, y - infectionRadius);
        int yMax = Math.min(this.maxY - 1, y + infectionRadius);
        for (int px = xMin; px <= xMax; px++) {
            for (int py = yMin; py <= yMax; py++) {
                if ((x != px || y != py)
                    && this.hosts[px][py] != null
                    && this.hosts[px][py].getHealthStatus() == HEALTHY) {
                    neighbours.add(this.hosts[px][py]);
                }
            }
        }
        return neighbours;
    }

//    private int getVariation(int base, float variation) {
//        int absVariation = Math.round(base * variation);
//        return VARIATION_RANDOM.nextInt(absVariation * 2) - absVariation + base;
//
//    }
    private void copyHostGrid() {
        long start = System.currentTimeMillis();
        gridIterator((int x, int y) -> {
            try {
                if (hosts[x][y] != null) {
                    oldHosts[x][y] = hosts[x][y].clone();
                }
            } catch (CloneNotSupportedException ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });
        LOGGER.info("Copying grid finishes after {}ms", System.currentTimeMillis() - start);
    }

    private void gridIterator(GridProcessor processor) {
        for (int x = 0; x < this.maxX; x++) {
            for (int y = 0; y < this.maxY; y++) {
                processor.process(x, y);
            }
        }
    }

    private void playgroundChanged(Statistics statistics) {
        this.playgroundChangedListener.forEach(pcl -> pcl.playgroundChanged(this, statistics));

    }

    private interface GridProcessor {

        public void process(int x, int y);
    }

}
