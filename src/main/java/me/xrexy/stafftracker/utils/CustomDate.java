package me.xrexy.stafftracker.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class CustomDate {
    private final LocalDate localDate;

    public CustomDate() {
        this.localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public CustomDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public int getYear() {
        return localDate.getYear();
    }

    public int getDay() {
        return localDate.getDayOfMonth();
    }

    public int getMonth() {
        return localDate.getMonthValue();
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public String getFormatted() {
        return getDay() + "-" + getMonth() + "-" + getYear();
    }
}
