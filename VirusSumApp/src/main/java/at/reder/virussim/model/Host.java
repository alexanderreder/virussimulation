package at.reder.virussim.model;

import java.util.Random;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alex
 */
public class Host implements Cloneable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Host.class);
    private static final Random MOVE_RANDOM = new Random();
    private int mobilityRadius;
    private float mobilityRadiusDeviation;
    private int immunityTime;
    private Virus virus;
    private int infectionTimestamp;
    private int healingPeroid;

    /**
     * @return the mobilityRadius
     */
    public int getMobilityRadius() {
        return mobilityRadius;
    }

    /**
     * @param mobilityRadius the mobilityRadius to set
     */
    public void setMobilityRadius(int mobilityRadius) {
        this.mobilityRadius = mobilityRadius;
    }

    /**
     * @return the mobilityRadiusDeviation
     */
    public float getMobilityRadiusDeviation() {
        return mobilityRadiusDeviation;
    }

    /**
     * @param mobilityRadiusDeviation the mobilityRadiusDeviation to set
     */
    public void setMobilityRadiusDeviation(float mobilityRadiusDeviation) {
        this.mobilityRadiusDeviation = mobilityRadiusDeviation;
    }

    /**
     * @return the immunityTime
     */
    public int getImmunityTime() {
        return immunityTime;
    }

    /**
     * @param immunityTime the immunityTime to set
     */
    public void setImmunityTime(int immunityTime) {
        this.immunityTime = immunityTime;
    }

    /**
     * @return the virus
     */
    public Virus getVirus() {
        return virus;
    }

    /**
     * @param virus the virus to set
     */
    public void setVirus(Virus virus) {
        this.virus = virus;
    }

    /**
     * @return the infectionTimestamp
     */
    public int getInfectionTimestamp() {
        return infectionTimestamp;
    }

    /**
     * @param infectionTimestamp the infectionTimestamp to set
     */
    public void setInfectionTimestamp(int infectionTimestamp) {
        this.infectionTimestamp = infectionTimestamp;
    }

    /**
     * @return the healingPeroid
     */
    public int getHealingPeroid() {
        return healingPeroid;
    }

    /**
     * @param healingPeroid the healingPeroid to set
     */
    public void setHealingPeroid(int healingPeroid) {
        this.healingPeroid = healingPeroid;
    }

    public boolean isInfected() {
        return this.getVirus() != null;
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
        hostClone.setVirus(this.virus);
        return hostClone;
    }

    @Override
    public boolean equals(Object other) {
        boolean equals;
        if (other instanceof Host) {
            Host otherHost = (Host) other;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.healingPeroid, otherHost.getHealingPeroid())
                    .append(this.immunityTime, otherHost.getImmunityTime())
                    .append(this.infectionTimestamp, otherHost.getInfectionTimestamp())
                    .append(this.mobilityRadius, otherHost.getMobilityRadius())
                    .append(this.mobilityRadiusDeviation, otherHost.getMobilityRadiusDeviation())
                    .append(this.virus, otherHost.getVirus());
            equals = equalsBuilder.isEquals();
        } else {
            equals = false;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.healingPeroid)
                .append(this.immunityTime)
                .append(this.infectionTimestamp)
                .append(this.mobilityRadius)
                .append(this.mobilityRadiusDeviation)
                .append(this.virus);
        return hashCodeBuilder.toHashCode();
    }
}
