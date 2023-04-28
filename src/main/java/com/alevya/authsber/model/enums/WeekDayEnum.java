package com.alevya.authsber.model.enums;

public enum WeekDayEnum {

    MON("Пн"),
    TUE("Вт"),
    WED("Ср"),
    THU("Чт"),
    FRI("Пт"),
    SAT("Сб"),
    SUN("Вс");

    private final String weekDayRu;

    WeekDayEnum(String weekDayRu) {
        this.weekDayRu = weekDayRu;
    }

    public String getWeekDayName() {
        return weekDayRu;
    }
}