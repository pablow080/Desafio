package org.desafioestagio.wicket.service;

import org.apache.wicket.util.convert.IConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateConverter implements IConverter<LocalDate> {

    @Override
    public LocalDate convertToObject(String value, Locale locale) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    @Override
    public String convertToString(LocalDate value, Locale locale) {
        return value != null ? value.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "";
    }
}
