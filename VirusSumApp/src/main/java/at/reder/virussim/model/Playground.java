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

    public Playground(int maxX, int maxY, double density) {
        this(maxX, maxY, Math.round(maxX * maxY * density));
    }

    public Playground(int maxX, int maxY, long maxHosts) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.hosts = new Host[this.maxX][this.maxY];
        initHosts(maxHosts);
    }

    @Override
    public void timeChanged(int timestamp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void initHosts(long maxHosts) {
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
            } while (this.hosts[x][y] == null);
            this.hosts[x][y] = new Host();
        }
    }

    private void move(Host host) {

    }

}
