package at.reder.virussim;

import at.reder.virussim.model.Playground;
import at.reder.virussim.view.VirusSimFrame;

/**
 *
 * @author alex
 */
//@SpringBootApplication(scanBasePackages = {"at.reder.virussim"})
public class Main {//implements CommandLineRunner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        Playground playground = new Playground(1000, 1000, 0.2f);
        VirusSimFrame vsf = new VirusSimFrame();
        playground.addPlaygroundChangeListener(vsf);
        java.awt.EventQueue.invokeLater(() -> vsf.setVisible(true));
        for (int i = 0; i < 10; i++) {
            playground.timeChanged(i);
            vsf.repaint();
            Thread.currentThread().sleep(1000);
        }
//        SpringApplication.run(Main.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
}
