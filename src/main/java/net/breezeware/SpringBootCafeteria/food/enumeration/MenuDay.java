package net.breezeware.SpringBootCafeteria.food.enumeration;

public enum MenuDay {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static MenuDay fromString(String day) {
        try {
            return MenuDay.valueOf(day.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}