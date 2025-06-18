package org.example.hansabal.domain.admin.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProductOrderStatRequest(

        @NotNull
        LocalDate from,

        @NotNull
        LocalDate to,

        @NotNull
        ProductOrderStatPeriodType period
) {
}
