package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import enums.Objective;
import enums.Platform;
import enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Campaign name is required")
	@Column(nullable = false)
	private String campaignName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Platform platform;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Objective objective;

	private String owner;

	private LocalDate startDate;

	private LocalDate endDate;

	@Column(precision = 15, scale = 2)
	private BigDecimal googleBudget;

	@Column(precision = 15, scale = 2)
	private BigDecimal metaBudget;

	private String targetAudience;

	private String kpi;

	private String landingPage;

	@Column(columnDefinition = "TEXT")
	private String strategy;

	@Column(columnDefinition = "TEXT")
	private String manualData;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

}
