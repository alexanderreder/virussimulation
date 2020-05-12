package at.reder.virussim.listener;

import at.reder.virussim.model.Playground;
import at.reder.virussim.model.Statistics;

/**
 *
 * @author alex
 */
public interface PlaygroundChangedListener {

    public void playgroundChanged(Playground playground, Statistics statistics);
}
