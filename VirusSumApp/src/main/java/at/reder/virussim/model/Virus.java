package at.reder.virussim.model;

/**
 *
 * @author alex
 */
public class Virus {

    private final int incubationPeriod;
    private final float incubationVariation;
    private final int infectionRadius;
    private final float infectionProbability;

    public Virus(int incubationPeriod, float incubationVariation, int infectionRadius, float infectionProbalitiy) {
        this.incubationPeriod = incubationPeriod;
        this.incubationVariation = incubationVariation;
        this.infectionRadius = infectionRadius;
        this.infectionProbability = infectionProbalitiy;
    }

    public Virus getInfectionVirus() {
        return this;
    }

    public int getIncubationPeriod() {
        return this.incubationPeriod;
    }

    public float getIncubationVariation() {
        return this.incubationVariation;
    }

    public int getInfectionRadius() {
        return this.infectionRadius;
    }

    public float getInfectionProbability() {
        return this.infectionProbability;
    }
}
