package com.example.zuora_api.util;

import org.springframework.stereotype.Component;

@Component
public class ThreeTenBpConverter {

    public org.threeten.bp.LocalDate convert(java.time.LocalDate source) {
        return org.threeten.bp.LocalDate.of(
                source.getYear(),
                source.getMonthValue(),
                source.getDayOfMonth()
        );
    }
}
