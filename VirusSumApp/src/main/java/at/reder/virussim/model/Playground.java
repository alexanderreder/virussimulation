package at.reder.virussim.model;

import at.reder.virussim.listener.PlaygroundChangedListener;
import at.reder.virussim.listener.TimeChangedListener;
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
    private static final float INFACTION_RATE = 0.01f;
    private final int maxX;
    private final int maxY;
    private final Host[][] hosts;
    private final Host[][] oldHosts;
    private final Map<Host, int[]> hostVector;
    private final Set<PlaygroundChangedListener> playgroundChangedListener;
    private final int[] probabilityArray;

    public Playground(int maxX, int maxY, float density) {
        this(maxX, maxY, Math.round(maxX * maxY * density));
    }

    public Playground(int maxX, int maxY, int maxHosts) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.hosts = new Host[this.maxX][this.maxY];
        this.oldHosts = new Host[this.maxX][this.maxY];
        this.hostVector = new HashMap<>(maxHosts);
        this.playgroundChangedListener = new HashSet<>();
        this.probabilityArray = new int[RESOLUTION];
        initHosts(maxHosts);
    }

    public void addPlaygroundChangeListener(PlaygroundChangedListener pcl) {
        this.playgroundChangedListener.add(pcl);
    }

    public void removePlaygroundChangedListener(PlaygroundChangedListener pcl) {
        this.playgroundChangedListener.remove(pcl);
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
        // 6) clear old host grid
        logStatistics(timestamp);
        playgroundChanged(timestamp);
    }

    private void logStatistics(int timestamp) {
        int[] result = new int[]{0, 0, 0, 0};
        gridIterator((int x, int y) -> {
            if (this.hosts[x][y] != null) {
                Host host = this.hosts[x][y];
                if (host.isImmune()) {
                    result[3]++;
                } else if (host.getVirus() != null) {
                    if (host.isInfected()) {
                        result[1]++;
                    } else {
                        result[0]++;
                    }
                } else {
                    result[2]++;
                }
            }
        });
        LOGGER.info("{}: {} hosts with virus, {} hosts infected, {} host immune, {} hosts healthy", timestamp, result[0], result[1], result[3], result[2]);
    }

    private void initHosts(int maxHosts) {
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
            if (this.probabilityArray[VARIATION_RANDOM.nextInt(RESOLUTION - 1)] == 1) {
                infectedCount++;
                LOGGER.info("Host {} initially virus infected", host);
                host.setVirus(new Virus(2, 0.1f,
                        20, 0.25f,
                        7, 0.2f), 0);
            }
            this.hosts[x][y] = host;
            this.oldHosts[x][y] = null;
            this.hostVector.put(host, new int[]{x, y});
        }
        playgroundChanged(0);
        LOGGER.info("Initializing playground ({}/{}) with {} hosts, {} infected,  finished in {}ms", this.maxX, this.maxY, maxHosts, infectedCount, System.currentTimeMillis() - start);
    }

    private Host generateHost(int id) {
        Host host = new Host(id);
        host.setMobilityRadius((this.maxX + this.maxY) / 6);
        host.setMobilityRadiusDeviation(0.3f);
        host.setImmunityPeriod(7);
        host.setImmunityVariation(0.1f);
        return host;
    }

    private void clearHostGrid() {
        gridIterator((int x, int y) -> this.hosts[x][y] = null);
    }

    private void moveHosts() {
        long start = System.currentTimeMillis();
        this.hostVector.keySet().forEach(this::move);
        LOGGER.info("Calculating new host infection/position takes {}ms", System.currentTimeMillis() - start);
    }

    private void move(Host host) {
        int[] pos = this.hostVector.get(host);
        int[] newPos = new int[2];
        do {
            int[] move = host.getMove();
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

    private void calculateHostInfections(int timestamp) {
        long start = System.currentTimeMillis();
        gridIterator((int x, int y) -> {
            Host host = this.hosts[x][y];
            if (host != null && host.getVirus() != null) {
                Virus virus = host.getVirus();
                if (!host.isImmune() && !host.isInfected()) {
                    // host infected after getting a virus
                    // timestamp >= virus timestamp + incubation time + incubation variation = infection timestamp
                    int incubationTime = getVariation(virus.getIncubationPeriod(), virus.getInfectionProbability());
                    if (timestamp >= incubationTime + host.getVirusTimestamp()) {
                        host.setInfectionTimestamp(timestamp);
                        LOGGER.info("Host {} became infected at {} with virus {}", host, timestamp, virus);
                    }
                } else if (host.isInfected()) {
                    // timestamp >= infection timestamp + healing time + healing variation = healed timestamp
                    int healingTime = getVariation(virus.getHealingPeriod(), virus.getHealingVariation());
                    if (timestamp >= healingTime + host.getInfectionTimestamp()) {
                        host.setInfectionTimestamp(-1);
                        host.setHealedTimestamp(timestamp);
                        LOGGER.info("Host {} became healed at {} with virus {}", host, timestamp, virus);
                    }
                } else if (host.isHealed()) {
                    // check immunity status
                    // timestamp >= healing timestamp + immuinity period + immunity variation
                    if (host.getImmunityPeriod() >= 0) {
                        int immunityTime = getVariation(host.getImmunityPeriod(), host.getImmunityVariation());
                        if (timestamp >= immunityTime + host.getHealedTimestamp()) {
                            host.setVirus(null, -1);
                            host.setHealedTimestamp(-1);
                            LOGGER.info("Host ({}) immunity for virus {} ended at {}", host, virus, timestamp);
                        }
                    }
                }
            }
            Host oldHost = this.oldHosts[x][y];
            if (oldHost != null && oldHost.getVirus() != null && !oldHost.isHealed()) {
                Virus virus = oldHost.getVirus();
                // search neigbours in infectionRadius
                Set<Host> neighbours = getNeighbours(x, y, virus.getInfectionRadius());
                // infection = infectionProbability / distace
                neighbours.forEach(neighbour -> {
                    int[] pos = this.hostVector.get(neighbour);
                    float distance = (float) Math.sqrt(Math.pow(pos[0] - x, 2) + Math.pow(pos[1] - y, 2));
                    float infection = virus.getInfectionProbability() / distance;
                    LOGGER.info("Infection probability for host{}={}", neighbour.getId(), infection);
                    setProbabilityArray(infection);
                    boolean infected = this.probabilityArray[VARIATION_RANDOM.nextInt(RESOLUTION - 1)] == 1;
                    if (infected) {
                        LOGGER.info("Host {} get virus at {}", neighbour, timestamp);
                        neighbour.setVirus(virus, timestamp);
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
                if (x != px && y != py
                        && this.hosts[px][py] != null
                        && this.hosts[px][py].getVirus() != null
                        && !this.hosts[px][py].isImmune()) {
                    neighbours.add(this.hosts[px][py]);
                }
            }
        }
        return neighbours;
    }

    private int getVariation(int base, float variation) {
        int absVariation = Math.round(base * variation);
        return VARIATION_RANDOM.nextInt(absVariation * 2) - absVariation + base;

    }

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

    private void playgroundChanged(int timestamp) {
        this.playgroundChangedListener.forEach(pcl -> pcl.playgroundChanged(this, timestamp));
    }

    private interface GridProcessor {

        public void process(int x, int y);
    }

}
