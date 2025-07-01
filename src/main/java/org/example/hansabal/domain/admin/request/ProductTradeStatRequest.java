package org.example.hansabal.domain.admin.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ProductTradeStatRequest(

        @NotNull
        LocalDate from,

        @NotNull
        LocalDate to,

        @NotNull
        ProductTradeStatPeriodType period
) {
}
