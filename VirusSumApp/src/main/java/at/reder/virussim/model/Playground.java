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
    private static final Random POS_RANDOM = new Random();
    private final int maxX;
    private final int maxY;
    private final Host[][] hosts;
    private final Host[][] oldHosts;
    private final Map<Host, int[]> hostVector;
    private final Set<PlaygroundChangedListener> playgroundChangedListener;

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
        initHosts(maxHosts);
    }

    public void addPlaygroundChangeListener(PlaygroundChangedListener pcl) {
        this.playgroundChangedListener.add(pcl);
    }

    public void removePlaygroundChangedListener(PlaygroundChangedListener pcl) {
        this.playgroundChangedListener.remove(pcl);
    }

    public Host[][] getHosts() {
        return this.hosts;
    }

    public Host[][] getOldHosts() {
        return this.oldHosts;
    }

    @Override
    public void timeChanged(int timestamp) {
        // 1) copy host grid
        copyHostGrid();
        // 2) calculate new host infections
        calculateInfection(null);
        // 3) clear host grid
        clearHostGrid();
        long start = System.currentTimeMillis();
        this.hostVector.keySet().forEach(this::move);
        LOGGER.info("Calculating new host infection/position takes {}ms", System.currentTimeMillis() - start);
        // 5) save new host grid
        // 6) clear old host grid
        playgroundChanged();
    }

    private void initHosts(int maxHosts) {
        long start = System.currentTimeMillis();
        if (maxHosts > (this.maxX * this.maxY * 0.75)) {
            maxHosts = Math.round(this.maxX * this.maxY * 0.75f);
            LOGGER.warn("Too much hosts defined. Using {} hosts", maxHosts);
        }
        for (int hostId = 0; hostId < maxHosts; hostId++) {
            int x;
            int y;
            do {
                x = POS_RANDOM.nextInt(this.maxX);
                y = POS_RANDOM.nextInt(this.maxY);
            } while (this.hosts[x][y] != null);
            Host host = generateHost(hostId);
            this.hosts[x][y] = host;
            this.oldHosts[x][y] = null;
            this.hostVector.put(host, new int[]{x, y});
        }
        playgroundChanged();
        LOGGER.info("Initializing playground ({}/{}) with {} hosts finished in {}ms", this.maxX, this.maxY, maxHosts, System.currentTimeMillis() - start);
    }

    private Host generateHost(int id) {
        Host host = new Host(id);
        host.setMobilityRadius((this.maxX + this.maxY) / 6);
        host.setMobilityRadiusDeviation(0.3f);
        return host;
    }

    private void clearHostGrid() {
        for (int x = 0; x < this.maxX; x++) {
            for (int y = 0; y < this.maxY; y++) {
                this.hosts[x][y] = null;
            }
        }
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

    private void calculateInfection(Host host) {

    }

    private void copyHostGrid() {
        for (int x = 0; x < this.maxX; x++) {
            for (int y = 0; y < this.maxY; y++) {
                try {
                    if (this.hosts[x][y] != null) {
                        this.oldHosts[x][y] = this.hosts[x][y].clone();
                    }
                } catch (CloneNotSupportedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private void playgroundChanged() {
        this.playgroundChangedListener.forEach(pcl -> pcl.playgroundChanged(this));
    }

}
