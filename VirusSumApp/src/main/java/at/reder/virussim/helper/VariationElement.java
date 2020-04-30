package at.reder.virussim.helper;

import java.util.Random;

/**
 *
 * @author alex
 */
public class VariationElement {

    private static final Random VARIATION_RANDOM = new Random();
    private final int base;
    private final float variation;

    public VariationElement(int base, float variation) {
        this.base = base;
        this.variation = variation;
    }

    public int getBase() {
        return this.base;
    }

    public float getVariation() {
        return this.variation;
    }

    public int getValue() {
        int absVariation = Math.round(this.base * this.variation);
        return absVariation > 0 ? (VARIATION_RANDOM.nextInt(absVariation * 2) - absVariation + this.base) : this.base;
    }
}
