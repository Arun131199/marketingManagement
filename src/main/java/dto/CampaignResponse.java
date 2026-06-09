package dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import enums.Objective;
import enums.Platform;
import enums.Status;
import lombok.Builder;
import lombok.Data;
import model.Campaign;

@Data
@Builder
public class CampaignResponse {
	private Long id;
    private String campaignName;
    private Platform platform;
    private Status status;
    private Objective objective;
    private String owner;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal googleBudget;
    private BigDecimal metaBudget;
    private BigDecimal totalBudget;
    private String targetAudience;
    private String kpi;
    private String landingPage;
    private String strategy;
    private String manualData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
   public static CampaignResponse from(Campaign c){
       BigDecimal googleBudget=c.getGoogleBudget()!=null?c.getGoogleBudget():BigDecimal.ZERO;
       BigDecimal metaBudget=c.getMetaBudget()!=null?c.getMetaBudget():BigDecimal.ZERO;

       return CampaignResponse.builder()
               .id(c.getId())
               .campaignName(c.getCampaignName())
               .platform(c.getPlatform())
               .status(c.getStatus())
               .objective(c.getObjective())
               .owner(c.getOwner())
               .startDate(c.getStartDate())
               .endDate(c.getEndDate())
               .googleBudget(googleBudget)
               .metaBudget(metaBudget)
               .totalBudget(googleBudget.add(metaBudget))
               .targetAudience(c.getTargetAudience())
               .kpi(c.getKpi())
               .landingPage(c.getLandingPage())
               .strategy(c.getStrategy())
               .manualData(c.getManualData())
               .createdAt(c.getCreatedAt())
               .updatedAt(c.getUpdatedAt())
               .build();
   }

}
