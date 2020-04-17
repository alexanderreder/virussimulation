package at.reder.virussim.model;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 *
 * @author Alexander Reer
 */
@Configuration
@ComponentScan(basePackages = "at.reder.virussim")
@PropertySource("classpath:application.properties")
public class Context {

}
