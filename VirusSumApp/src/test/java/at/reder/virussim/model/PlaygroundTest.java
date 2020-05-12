package at.reder.virussim.model;

import at.reder.virussim.helper.VariationElement;
import org.junit.jupiter.api.AfterAll;
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

    private static Host[][] copyHostGrid(Playground playground) {
        Host[][] copiedHosts = new Host[playground.getMaxX()][playground.getMaxY()];
        for (int x = 0; x < copiedHosts.length; x++) {
            for (int y = 0; y < copiedHosts[x].length; y++) {
                try {
                    if (playground.getHost(x, y) != null) {
                        copiedHosts[x][y] = playground.getHost(x, y).clone();
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
        this.playground = new Playground(MAX_X, MAX_Y, DENSITY, 1, new Virus(10,
            0.8f,
            new VariationElement(3, 0.4f),
            new VariationElement(7, 0.3f),
            new VariationElement(14, 0.2f)
        ));
    }

    public void tearDown() {
    }

    @Test
    public void testInitHosts() {
        int nHosts = 0;
        for (int x = 0; x < this.playground.getMaxX(); x++) {
            for (int y = 0; y < this.playground.getMaxY(); y++) {
                if (this.playground.getHost(x, y) != null) {
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
        for (int x = 0; x < this.playground.getMaxX(); x++) {
            for (int y = 0; y < this.playground.getMaxY(); y++) {
                if (this.playground.getOldHost(x, y) != null) {
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
        Host[][] copiedHosts = copyHostGrid(this.playground);
        this.playground.timeChanged(0);
        LOGGER.info("Time changed/copy: {}/{}", copiedHosts.length, copiedHosts[0].length);
        for (int x = 0; x < copiedHosts.length; x++) {
            for (int y = 0; y < copiedHosts[x].length; y++) {
                assertEquals(copiedHosts[x][y], this.playground.getOldHost(x, y));
            }
        }
    }

//    @Configuration
//    @ComponentScan("basepackage")
//    private static class SpringConfig {
//
//    }
}
