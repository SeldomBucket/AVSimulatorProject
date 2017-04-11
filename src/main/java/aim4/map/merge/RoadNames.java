package aim4.map.merge;

/**
 * Created by Callum on 10/04/2017.
 */
public enum RoadNames {
    TARGET_ROAD ("TARGET_ROAD"),
    MERGING_ROAD ("MERGING_ROAD");

    private final String name;

    private RoadNames(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
