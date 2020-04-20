package at.reder.virussim.model;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alex
 */
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration
public class PlaygroundTest {

    private static final int MAX_X = 1000;
    private static final int MAX_Y = 1000;
    private static final float DENSITY = 0.25f;
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaygroundTest.class);

    private static Host[][] copyHostGrid(Host[][] hosts) {
        Host[][] copiedHosts = new Host[MAX_X][MAX_Y];
        for (int x = 0; x < MAX_X; x++) {
            for (int y = 0; y < MAX_Y; y++) {
                try {
                    if (hosts[x][y] != null) {
                        copiedHosts[x][y] = hosts[x][y].clone();
                    }
                } catch (CloneNotSupportedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }
        return copiedHosts;
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    private Playground playground;

    @BeforeEach
    public void setUp() {
        this.playground = new Playground(MAX_X, MAX_Y, DENSITY);
    }

    public void tearDown() {
    }

    @Test
    public void testInitHosts() {
        int nHosts = 0;
        Host[][] hosts = this.playground.getHosts();
        for (Host[] hostLine : hosts) {
            for (Host host : hostLine) {
                if (host != null) {
                    nHosts++;
                }
            }
        }
        int expectedHosts = 250000;
        LOGGER.info("Init playground hosts: {}/{}", expectedHosts, nHosts);
        assertEquals(expectedHosts, nHosts);
    }

    @Test
    public void testInitOldHosts() {
        int nHosts = 0;
        Host[][] hosts = this.playground.getOldHosts();
        for (Host[] hostLine : hosts) {
            for (Host host : hostLine) {
                if (host != null) {
                    nHosts++;
                }
            }
        }
        int expectedHosts = 0;
        LOGGER.info("Init playground oldHosts: {}/{}", expectedHosts, nHosts);
        assertEquals(expectedHosts, nHosts);
    }

    @Test
    public void testTimeChanged1() {
        Host[][] copiedHosts = copyHostGrid(this.playground.getHosts());
        this.playground.timeChanged(0);
        LOGGER.info("Time changed/copy: {}/{}", copiedHosts, this.playground.getOldHosts());
        assertArrayEquals(copiedHosts, this.playground.getOldHosts());
    }

//    @Configuration
//    @ComponentScan("basepackage")
//    private static class SpringConfig {
//
//    }
}
