package at.reder.virussim.model;

import at.reder.virussim.listener.TimeChangedListener;
import java.util.Random;
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

    public Playground(int maxX, int maxY, double density) {
        this(maxX, maxY, Math.round(maxX * maxY * density));
    }

    public Playground(int maxX, int maxY, long maxHosts) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.hosts = new Host[this.maxX][this.maxY];
        this.oldHosts = new Host[this.maxX][this.maxY];
        initHosts(maxHosts);
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
        // 3) calculate new host positions
        // 4) save new host grid
        // 5) clear old host grid
    }

    private void initHosts(long maxHosts) {
        long start = System.currentTimeMillis();
        if (maxHosts > (this.maxX * this.maxY * 0.75)) {
            maxHosts = Math.round(this.maxX * this.maxY * 0.75);
            LOGGER.warn("Too much hosts defined. Using {} hosts", maxHosts);
        }
        for (long host = 0; host < maxHosts; host++) {
            int x;
            int y;
            do {
                x = POS_RANDOM.nextInt(this.maxX);
                y = POS_RANDOM.nextInt(this.maxY);
            } while (this.hosts[x][y] != null);
            this.hosts[x][y] = new Host();
            this.oldHosts[x][y] = null;
        }
        LOGGER.info("Initializing playground ({}/{}) with {} hosts finished in {}ms", this.maxX, this.maxY, maxHosts, System.currentTimeMillis() - start);
    }

    private void move(Host host, int x, int y) {
        int[] move = host.getMove();
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
        };
    }

}
