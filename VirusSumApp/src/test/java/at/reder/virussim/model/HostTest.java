package at.reder.virussim.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alex
 */
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
public class HostTest {

    private static final int MOBILITY_RADIUS = 100;
    private static final float MOBILITY_RADIUS_DEVIATION = 0.2f;
    private static final Logger LOGGER = LoggerFactory.getLogger(HostTest.class);

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    private Host host;

    @BeforeEach
    public void setUp() {
        this.host = new Host(0);
        this.host.setMobilityRadius(MOBILITY_RADIUS);
        this.host.setMobilityRadiusDeviation(MOBILITY_RADIUS_DEVIATION);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testMove_0() {
        int[] move = this.host.getMove(0, 100);
        int[] expectedVector = new int[]{100, 0};
        LOGGER.info("Vector 0: {}/{}", expectedVector, move);
        assertArrayEquals(expectedVector, move);
    }

    @Test
    public void testMove_90() {
        int[] move = this.host.getMove(Math.PI / 2, 100);
        int[] expectedVector = new int[]{0, 100};
        LOGGER.info("Vector 90: {}/{}", expectedVector, move);
        assertArrayEquals(expectedVector, move);
    }

    @Test
    public void testMove_180() {
        int[] move = this.host.getMove(Math.PI, 100);
        int[] expectedVector = new int[]{-100, 0};
        LOGGER.info("Vector 180: {}/{}", expectedVector, move);
        assertArrayEquals(expectedVector, move);
    }

    @Test
    public void testMove_270() {
        int[] move = this.host.getMove(3 * Math.PI / 2, 100);
        int[] expectedVector = new int[]{0, -100};
        LOGGER.info("Vector 270: {}/{}", expectedVector, move);
        assertArrayEquals(expectedVector, move);
    }

    @Test
    public void testMove_45() {
        int[] move = this.host.getMove(Math.PI / 4, 100);
        int[] expectedVector = new int[]{71, 71};
        LOGGER.info("Vector 45: {}/{}", expectedVector, move);
        assertArrayEquals(expectedVector, move);
    }

    @Test
    public void testMove_rand() {
        int[] move = this.host.getMove();
        double length = Math.sqrt(Math.pow(move[0], 2) + Math.pow(move[1], 2));
        LOGGER.info("Vector/Length: {}/{}", move, length);
        assertTrue((MOBILITY_RADIUS * (1 + MOBILITY_RADIUS_DEVIATION)) >= length
            && (MOBILITY_RADIUS * (1 - MOBILITY_RADIUS_DEVIATION) <= length));
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        Host cloneHost = this.host.clone();
        LOGGER.info("Host/Clone: {}/{}", this.host, cloneHost);
        assertFalse(this.host == cloneHost);
    }

    @Test
    public void textCloneEquals() throws CloneNotSupportedException {
        Host cloneHost = this.host.clone();
        LOGGER.info("Host/Equals: {}/{}", this.host, cloneHost);
        assertTrue(this.host.equals(cloneHost));
    }

    @Test
    public void textCloneHashCode() throws CloneNotSupportedException {
        Host cloneHost = this.host.clone();
        LOGGER.info("HashCode: {}/{}", this.host.hashCode(), cloneHost.hashCode());
        assertEquals(this.host.hashCode(), cloneHost.hashCode());
    }
}
