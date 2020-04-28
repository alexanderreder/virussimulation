package at.reder.virussim.model;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alex
 */
public class Host implements Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Host.class);
    private static final Random MOVE_RANDOM = new Random();
    private final int id;
    private int mobilityRadius;
    private float mobilityRadiusDeviation;
    private Virus virus;
    private int virusTimestamp;
    private int infectionTimestamp;
    private int healingPeriod;
    private int healingVariation;
    private int healdTimestamp;
    private int immunityPeriod;
    private int immunityVariation;

    public Host(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public int getMobilityRadius() {
        return mobilityRadius;
    }

    public void setMobilityRadius(int mobilityRadius) {
        this.mobilityRadius = mobilityRadius;
    }

    public float getMobilityRadiusDeviation() {
        return mobilityRadiusDeviation;
    }

    public void setMobilityRadiusDeviation(float mobilityRadiusDeviation) {
        this.mobilityRadiusDeviation = mobilityRadiusDeviation;
    }

    public Virus getVirus() {
        return virus;
    }

    public void setVirus(Virus virus, int timestamp) {
        this.virus = virus;
        this.virusTimestamp = timestamp;
    }

    public int getInfectionTimestamp() {
        return infectionTimestamp;
    }

    public void setInfectionTimestamp(int infectionTimestamp) {
        this.infectionTimestamp = infectionTimestamp;
    }

    public boolean isInfected() {
        return this.infectionTimestamp == -1;
    }

    public boolean isHealed() {
        return this.healdTimestamp == -1;
    }

    public int[] getMove() {
        return getMove(getAngle(), getLength());
    }

    public int[] getMove(double angle, int length) {
        int[] vector = new int[]{
            (int) Math.round(length * Math.cos(angle)),
            (int) Math.round(length * Math.sin(angle))
        };
        LOGGER.debug("Move vector ({}, {}) -> ({}, {})", length, angle, vector[0], vector[1]);
        return vector;
    }

    private double getAngle() {
        return MOVE_RANDOM.nextInt(360) * Math.PI / 180.0;
    }

    private int getLength() {
        int absDeviation = Math.round(this.mobilityRadiusDeviation * this.mobilityRadius);
        return MOVE_RANDOM.nextInt(absDeviation * 2) - absDeviation + this.mobilityRadius;
    }

    @Override
    public Host clone() throws CloneNotSupportedException {
        Host hostClone = (Host) super.clone();
        hostClone.setVirus(this.virus, this.virusTimestamp);
        return hostClone;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Host) && ((Host) other).getId() == this.id;
//        boolean equals;
//        if (other instanceof Host) {
//            Host otherHost = (Host) other;
//            EqualsBuilder equalsBuilder = new EqualsBuilder();
//            equalsBuilder.append(this.id, otherHost.getId())
//                    .append(this.healingPeroid, otherHost.getHealingPeroid())
//                    .append(this.immunityTime, otherHost.getImmunityTime())
//                    .append(this.infectionTimestamp, otherHost.getInfectionTimestamp())
//                    .append(this.mobilityRadius, otherHost.getMobilityRadius())
//                    .append(this.mobilityRadiusDeviation, otherHost.getMobilityRadiusDeviation())
//                    .append(this.virus, otherHost.getVirus());
//            equals = equalsBuilder.isEquals();
//        } else {
//            equals = false;
//        }
//        return equals;
    }

    @Override
    public int hashCode() {
        return this.id;
//        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
//        hashCodeBuilder.append(this.id)
//            .append(this.healingPeroid)
//            .append(this.immunityTime)
//            .append(this.infectionTimestamp)
//            .append(this.mobilityRadius)
//            .append(this.mobilityRadiusDeviation)
//            .append(this.virus);
//        return hashCodeBuilder.toHashCode();
    }
}
