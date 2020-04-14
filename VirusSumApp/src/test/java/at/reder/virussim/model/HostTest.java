package at.reder.virussim.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author alex
 */
public class HostTest {

    private static final int MOBILITY_RADIUS = 100;
    private static final float MOBILITY_RADIUS_DEVIATION = 0.2f;

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    private Host host;

    @BeforeEach
    public void setUp() {
        this.host = new Host();
        this.host.setMobilityRadius(MOBILITY_RADIUS);
        this.host.setMobilityRadiusDeviation(MOBILITY_RADIUS_DEVIATION);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testMove_0() {
        int[] move = this.host.getMove(0, 100);
        assertArrayEquals(new int[]{100, 0}, move);
    }

    @Test
    public void testMove_90() {
        int[] move = this.host.getMove(Math.PI / 2, 100);
        assertArrayEquals(new int[]{0, 100}, move);
    }

    @Test
    public void testMove_180() {
        int[] move = this.host.getMove(Math.PI, 100);
        assertArrayEquals(new int[]{-100, 0}, move);
    }

    @Test
    public void testMove_270() {
        int[] move = this.host.getMove(3 * Math.PI / 2, 100);
        assertArrayEquals(new int[]{0, -100}, move);
    }

    @Test
    public void testMove_45() {
        int[] move = this.host.getMove(Math.PI / 4, 100);
        assertArrayEquals(new int[]{71, 71}, move);
    }

    @Test
    public void testMove_rand() {
        int[] move = this.host.getMove();
        double length = Math.sqrt(Math.pow(move[0], 2) + Math.pow(move[1], 2));
        assertTrue((MOBILITY_RADIUS * (1 + MOBILITY_RADIUS_DEVIATION)) >= length
                && (MOBILITY_RADIUS * (1 - MOBILITY_RADIUS_DEVIATION) <= length));
    }
}
