package org.example.hansabal.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
@Embeddable
public class ProductOrderStatId {

	@Column(nullable = false)
	private Long ProdcutId;

	@Column(nullable = false)
	private LocalDate date;
}
