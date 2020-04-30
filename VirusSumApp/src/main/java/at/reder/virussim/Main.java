package at.reder.virussim;

import at.reder.virussim.model.Playground;
import at.reder.virussim.view.VirusSimFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author alex
 */
//@SpringBootApplication(scanBasePackages = {"at.reder.virussim"})
public class Main {//implements CommandLineRunner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Playground playground = new Playground(1000, 768, 0.1f);
            VirusSimFrame vsf = new VirusSimFrame();
            vsf.setPlayground(playground);
            vsf.setVisible(true);
        });
//        SpringApplication.run(Main.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
