package at.reder.virussim.model;

import at.reder.virussim.helper.VariationElement;

/**
 *
 * @author alex
 */
public class Virus {

    private final int infectionRadius;
    private final float infectionProbability;
    private final VariationElement triggerPeriod;
    private final VariationElement healingPeriod;
    private final VariationElement immunityPeriod;

    public Virus(int infectionRadius,
            float infectionProbability,
            VariationElement triggerPeriod,
            VariationElement healingPeriod,
            VariationElement immunityPeriod) {
        this.infectionRadius = infectionRadius;
        this.infectionProbability = infectionProbability;
        this.triggerPeriod = triggerPeriod;
        this.healingPeriod = healingPeriod;
        this.immunityPeriod = immunityPeriod;
    }

    public int getInfectionRadius() {
        return infectionRadius;
    }

    public float getInfectionProbability() {
        return this.infectionProbability;
    }

    public VariationElement getTriggerPeriod() {
        return triggerPeriod;
    }

    public VariationElement getHealingPeriod() {
        return healingPeriod;
    }

    public VariationElement getImmunityPeriod() {
        return immunityPeriod;
    }

}
