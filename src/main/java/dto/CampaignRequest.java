package dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import enums.Objective;
import enums.Platform;
import enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CampaignRequest {
	@NotBlank(message = "Campaign name is required")
	private String campaignName;

	private Platform platform;
	private Status status;
	private Objective objective;

	private String owner;
	private LocalDate startDate;
	private LocalDate endDate;
	private BigDecimal googleBudget;
	private BigDecimal metaBudget;
	private String targetAudience;
	private String kpi;
	private String landingPage;
	private String strategy;
	private String manualData;
}
