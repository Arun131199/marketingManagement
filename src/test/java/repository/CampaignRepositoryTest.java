package repository;

import com.marketingManagement.marketin.MarketinApplication;
import enums.Objective;
import enums.Platform;
import enums.Status;
import model.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MarketinApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:repository-test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional
class CampaignRepositoryTest {

    @Autowired
    private CampaignRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void searchWithoutTextReturnsCampaignsWithoutCallingLowerOnNullParameter() {
        Campaign campaign = campaign("Launch", Status.ACTIVE, Platform.GOOGLE_ADS);
        repository.saveAndFlush(campaign);

        List<Campaign> result = repository.search(null, null, null);

        assertThat(result).extracting(Campaign::getCampaignName).containsExactly("Launch");
    }

    @Test
    void searchWithTextMatchesTextColumnsCaseInsensitively() {
        repository.saveAndFlush(campaign("Launch", Status.ACTIVE, Platform.GOOGLE_ADS));
        repository.saveAndFlush(campaign("Retention", Status.PAUSED, Platform.META_ADS));

        List<Campaign> result = repository.search(null, null, "launch");

        assertThat(result).extracting(Campaign::getCampaignName).containsExactly("Launch");
    }

    @Test
    void searchWithoutTextAppliesStatusAndPlatformFilters() {
        repository.saveAndFlush(campaign("Google Active", Status.ACTIVE, Platform.GOOGLE_ADS));
        repository.saveAndFlush(campaign("Meta Active", Status.ACTIVE, Platform.META_ADS));

        List<Campaign> result = repository.search(Status.ACTIVE, Platform.META_ADS, null);

        assertThat(result).extracting(Campaign::getCampaignName).containsExactly("Meta Active");
    }

    private Campaign campaign(String name, Status status, Platform platform) {
        return Campaign.builder()
                .campaignName(name)
                .status(status)
                .platform(platform)
                .objective(Objective.LEADS)
                .owner("Arun")
                .strategy("Paid search")
                .targetAudience("SMB")
                .build();
    }
}
