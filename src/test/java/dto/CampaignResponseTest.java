package dto;

import enums.Objective;
import enums.Platform;
import enums.Status;
import model.Campaign;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CampaignResponseTest {

    @Test
    void fromMapsCampaignAndCalculatesTotalBudget() {
        LocalDate startDate = LocalDate.of(2026, 6, 1);
        LocalDate endDate = LocalDate.of(2026, 6, 30);
        LocalDateTime createdAt = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 6, 2, 10, 0);
        Campaign campaign = Campaign.builder()
                .id(10L)
                .campaignName("Launch")
                .platform(Platform.GOOGLE_AND_META)
                .status(Status.ACTIVE)
                .objective(Objective.SALES)
                .owner("Arun")
                .startDate(startDate)
                .endDate(endDate)
                .googleBudget(new BigDecimal("100.50"))
                .metaBudget(new BigDecimal("200.25"))
                .targetAudience("Founders")
                .kpi("ROAS")
                .landingPage("https://example.com")
                .strategy("Paid search and social")
                .manualData("manual")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        CampaignResponse response = CampaignResponse.from(campaign);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCampaignName()).isEqualTo("Launch");
        assertThat(response.getPlatform()).isEqualTo(Platform.GOOGLE_AND_META);
        assertThat(response.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(response.getObjective()).isEqualTo(Objective.SALES);
        assertThat(response.getOwner()).isEqualTo("Arun");
        assertThat(response.getStartDate()).isEqualTo(startDate);
        assertThat(response.getEndDate()).isEqualTo(endDate);
        assertThat(response.getGoogleBudget()).isEqualByComparingTo("100.50");
        assertThat(response.getMetaBudget()).isEqualByComparingTo("200.25");
        assertThat(response.getTotalBudget()).isEqualByComparingTo("300.75");
        assertThat(response.getTargetAudience()).isEqualTo("Founders");
        assertThat(response.getKpi()).isEqualTo("ROAS");
        assertThat(response.getLandingPage()).isEqualTo("https://example.com");
        assertThat(response.getStrategy()).isEqualTo("Paid search and social");
        assertThat(response.getManualData()).isEqualTo("manual");
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void fromTreatsNullBudgetsAsZero() {
        Campaign campaign = Campaign.builder()
                .campaignName("Launch")
                .platform(Platform.GOOGLE_ADS)
                .status(Status.PLANNING)
                .objective(Objective.LEADS)
                .build();

        CampaignResponse response = CampaignResponse.from(campaign);

        assertThat(response.getGoogleBudget()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getMetaBudget()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getTotalBudget()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
