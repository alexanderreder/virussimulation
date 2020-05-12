package at.reder.virussim.model;

import at.reder.virussim.helper.VariationElement;

/**
 *
 * @author alex
 */
public class Virus {

    protected int infectionRadius;
    protected float infectionProbability;
    protected VariationElement triggerPeriod;
    protected VariationElement healingPeriod;
    protected VariationElement immunityPeriod;

    public Virus() {

    }

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

    /**
     * @param infectionRadius the infectionRadius to set
     */
    public void setInfectionRadius(int infectionRadius) {
        this.infectionRadius = infectionRadius;
    }

    /**
     * @param infectionProbability the infectionProbability to set
     */
    public void setInfectionProbability(float infectionProbability) {
        this.infectionProbability = infectionProbability;
    }

    /**
     * @param triggerPeriod the triggerPeriod to set
     */
    public void setTriggerPeriod(VariationElement triggerPeriod) {
        this.triggerPeriod = triggerPeriod;
    }

    /**
     * @param healingPeriod the healingPeriod to set
     */
    public void setHealingPeriod(VariationElement healingPeriod) {
        this.healingPeriod = healingPeriod;
    }

    /**
     * @param immunityPeriod the immunityPeriod to set
     */
    public void setImmunityPeriod(VariationElement immunityPeriod) {
        this.immunityPeriod = immunityPeriod;
    }

}
