package at.reder.virussim.model;

/**
 *
 * @author alex
 */
public class Virus {

    private final int incubationPeriod;
    private final int infectionRadius;
    private final double infectionProbability;

    public Virus(int incubationPeriod, int infectionRadius, double infectionProbalitiy) {
        this.incubationPeriod = incubationPeriod;
        this.infectionRadius = infectionRadius;
        this.infectionProbability = infectionProbalitiy;
    }

    public Virus getInfectionVirus() {
        return this;
    }

    public int getIncubationPeriod() {
        return this.incubationPeriod;
    }

    public int getInfectionRadius() {
        return this.infectionRadius;
    }

    public double getInfectionProbability() {
        return this.infectionProbability;
    }
}
