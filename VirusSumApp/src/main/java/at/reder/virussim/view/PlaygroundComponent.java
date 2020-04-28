package at.reder.virussim.view;

import at.reder.virussim.model.Host;
import at.reder.virussim.model.Playground;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alexander Reer
 */
public class PlaygroundComponent extends JPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaygroundComponent.class);
    private final Playground playground;

    public PlaygroundComponent(Playground playground) {
        this.playground = playground;
    }

    @Override
    public void paintComponent(Graphics g) {
        int width = this.playground.getMaxX();
        int height = this.playground.getMaxY();
        setPreferredSize(new Dimension(width, height));
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int cc = 0; cc < width; cc++) {
            for (int rc = 0; rc < height; rc++) {
                Host host = this.playground.getHost(cc, rc);
                if (host == null) {
                    img.setRGB(cc, rc, Color.GRAY.getRGB());
                } else if (host.isInfected()) {
                    img.setRGB(cc, rc, Color.RED.getRGB());
                } else {
                    img.setRGB(cc, rc, Color.GREEN.getRGB());
                }
            }
        }
        g.drawImage(img, 0, 0, null);
    }
}
