package at.reder.virussim.view;

import at.reder.virussim.model.Host;
import at.reder.virussim.model.Playground;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 *
 * @author Alexander Reer
 */
public class PlaygroundPanel extends JPanel {

    private final Playground playground;

    public PlaygroundPanel(Playground playground) {
        this.playground = playground;
    }

    @Override
    public void paintComponents(Graphics g) {
        int width = getParent().getWidth();
        int height = getParent().getHeight();
        Host[][] hosts = this.playground.getHosts();
        // Create the new image needed
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int cc = 0; cc < width; cc++) {
            int x = hosts.length / width * cc;
            for (int rc = 0; rc < height; rc++) {
                int y = hosts[x].length / height * rc;
                Host host = hosts[x][y];
                if (host == null) {
                    img.setRGB(cc, rc, Color.GRAY.getRGB());
                } else if (host.isInfected()) {
                    // Set the pixel colour of the image n.b. x = cc, y = rc
                    img.setRGB(cc, rc, Color.RED.getRGB());
                } else {
                    img.setRGB(cc, rc, Color.GREEN.getRGB());
                }
            }//for cols
        }//for rows
        g.drawImage(img, 0, 0, width, height, null);
    }
}
