package br.com.fiap.study_manager.converters;

import br.com.fiap.study_manager.models.enums.Weekday;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WeekdayConverter implements AttributeConverter<Weekday, String> {
    @Override
    public String convertToDatabaseColumn(Weekday weekday) {
        return (weekday == null) ? null : weekday.name();
    }

    @Override
    public Weekday convertToEntityAttribute(String dbData) {
        return (dbData == null) ? null : Weekday.valueOf(dbData);
    }
}
