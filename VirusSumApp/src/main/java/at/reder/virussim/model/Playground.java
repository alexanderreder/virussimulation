package at.reder.virussim.model;

import at.reder.virussim.listener.TimeChangedListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author alex
 */
@Component
//@ConfigurationProperties(prefix = "playground")
public class Playground implements TimeChangedListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Playground.class);
    private static final Random POS_RANDOM = new Random();
    @Value("${playground.max.x}")
    private int maxX;
    @Value("${playground.max.y}")
    private int maxY;
    @Value("${playground.max.hosts}")
    private int maxHosts;
    @Value("${playground.density}")
    private float density;

    private Host[][] hosts;
    private Host[][] oldHosts;
    private Map<Host, int[]> hostVector;

//    public Playground(int maxX, int maxY, float density) {
//        this(maxX, maxY, Math.round(maxX * maxY * density));
//    }
//
//    public Playground(int maxX, int maxY, int maxHosts) {
//        this.maxX = maxX;
//        this.maxY = maxY;
//        this.hosts = new Host[this.maxX][this.maxY];
//        this.oldHosts = new Host[this.maxX][this.maxY];
//        this.hostVector = new HashMap<>(maxHosts);
//        initHosts(maxHosts);
//    }
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
    }

    @PostConstruct
    private void initHosts() {
        long start = System.currentTimeMillis();
        if (this.maxHosts == 0) {
            this.maxHosts = Math.round(this.maxX * this.maxY * this.density);
        }
        this.hosts = new Host[this.maxX][this.maxY];
        this.oldHosts = new Host[this.maxX][this.maxY];
        this.hostVector = new HashMap<>(maxHosts);
        if (this.maxHosts > (this.maxX * this.maxY * 0.75)) {
            this.maxHosts = Math.round(this.maxX * this.maxY * 0.75f);
            LOGGER.warn("Too much hosts defined. Using {} hosts", this.maxHosts);
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
        LOGGER.info("Initializing playground ({}/{}) with {} hosts finished in {}ms", this.maxX, this.maxY, maxHosts, System.currentTimeMillis() - start);
    }

    private Host generateHost(int id) {
        Host host = new Host(id);
        host.setMobilityRadius(100);
        host.setMobilityRadiusDeviation(0.4f);
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
                newPos[0] = pos[0] + move[0] - this.maxX;
            } else if (pos[0] + move[0] < 0) {
                newPos[0] = this.maxX + pos[0] + move[0];
            } else {
                newPos[0] = pos[0] + move[0];
            }
            if (pos[1] + move[1] >= this.maxY) {
                newPos[1] = pos[1] + move[1] - this.maxY;
            } else if (pos[1] + move[1] < 0) {
                newPos[1] = this.maxY + pos[1] + move[1];
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

}
