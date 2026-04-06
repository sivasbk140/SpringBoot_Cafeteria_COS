package net.breezeware.SpringBootCafeteria.food.enumeration;

/**
 * Represents the days of the week on which a food menu can be scheduled.
 * <p>
 * Used in {@link net.breezeware.SpringBootCafeteria.food.entity.FoodMenu} to specify
 * which day a given menu (BREAKFAST, LUNCH, or DINNER) is available.
 * </p>
 */
public enum MenuDay {

    /** Monday menu day. */
    MONDAY,

    /** Tuesday menu day. */
    TUESDAY,

    /** Wednesday menu day. */
    WEDNESDAY,

    /** Thursday menu day. */
    THURSDAY,

    /** Friday menu day. */
    FRIDAY,

    /** Saturday menu day. */
    SATURDAY,

    /** Sunday menu day. */
    SUNDAY;

    /**
     * Converts a string to the corresponding {@link MenuDay} enum constant,
     * ignoring case and trimming whitespace.
     *
     * @param day the string representation of the day (e.g. "monday", "TUESDAY")
     * @return the matching {@link MenuDay} constant, or {@code null} if no match is found
     */
    public static MenuDay fromString(String day) {
        try {
            return MenuDay.valueOf(day.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}