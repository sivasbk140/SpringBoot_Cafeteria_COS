package net.breezeware.Spring_Boot_Cafeteria.food.enumeration;

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