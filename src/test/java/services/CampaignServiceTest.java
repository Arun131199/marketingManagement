package services;

import dto.CampaignRequest;
import dto.CampaignResponse;
import enums.Objective;
import enums.Platform;
import enums.Status;
import exception.ResourceNotFoundException;
import model.Campaign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CampaignRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock
    private CampaignRepository repository;

    @InjectMocks
    private CampaignService service;

    @Test
    void getAllParsesFiltersAndTrimsSearch() {
        Campaign campaign = campaign(1L, "Launch");
        when(repository.search(Status.ACTIVE, Platform.META_ADS, "launch")).thenReturn(List.of(campaign));

        List<CampaignResponse> responses = service.getAll("active", "meta_ads", " launch ");

        assertThat(responses).hasSize(1);
        assertThat(responses.getFirst().getCampaignName()).isEqualTo("Launch");
        verify(repository).search(Status.ACTIVE, Platform.META_ADS, "launch");
    }

    @Test
    void getAllUsesNullFiltersForAllBlankOrInvalidValues() {
        when(repository.search(null, null, null)).thenReturn(List.of());

        List<CampaignResponse> responses = service.getAll("All", "unknown", " ");

        assertThat(responses).isEmpty();
        verify(repository).search(null, null, null);
    }

    @Test
    void getByIdReturnsCampaign() {
        when(repository.findById(7L)).thenReturn(Optional.of(campaign(7L, "Retarget")));

        CampaignResponse response = service.getById(7L);

        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getCampaignName()).isEqualTo("Retarget");
    }

    @Test
    void getByIdThrowsWhenCampaignDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Campaign not found with id: 99");
    }

    @Test
    void createMapsRequestDefaultsAndSavesCampaign() {
        CampaignRequest request = request("New Campaign");
        request.setPlatform(null);
        request.setStatus(null);
        when(repository.save(org.mockito.ArgumentMatchers.any(Campaign.class)))
                .thenAnswer(invocation -> {
                    Campaign saved = invocation.getArgument(0);
                    saved.setId(11L);
                    return saved;
                });

        CampaignResponse response = service.create(request);

        ArgumentCaptor<Campaign> captor = ArgumentCaptor.forClass(Campaign.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCampaignName()).isEqualTo("New Campaign");
        assertThat(captor.getValue().getPlatform()).isEqualTo(Platform.GOOGLE_ADS);
        assertThat(captor.getValue().getStatus()).isEqualTo(Status.PLANNING);
        assertThat(response.getId()).isEqualTo(11L);
    }

    @Test
    void updateMapsRequestOntoExistingCampaign() {
        Campaign existing = campaign(5L, "Old");
        CampaignRequest request = request("Updated");
        request.setPlatform(Platform.GOOGLE_AND_META);
        request.setStatus(Status.COMPLETED);
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        CampaignResponse response = service.update(request, 5L);

        assertThat(response.getCampaignName()).isEqualTo("Updated");
        assertThat(response.getPlatform()).isEqualTo(Platform.GOOGLE_AND_META);
        assertThat(response.getStatus()).isEqualTo(Status.COMPLETED);
        verify(repository).save(existing);
    }

    @Test
    void updateThrowsWhenCampaignDoesNotExist() {
        CampaignRequest request = request("Updated");
        when(repository.findById(44L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(request, 44L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Campaign not found with id: 44");
    }

    @Test
    void deleteRemovesExistingCampaign() {
        when(repository.existsById(3L)).thenReturn(true);

        service.delete(3L);

        verify(repository).deleteById(3L);
    }

    @Test
    void deleteThrowsWhenCampaignDoesNotExist() {
        when(repository.existsById(3L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Campaign not found with id: 3");
        verifyNoMoreInteractions(repository);
    }

    private Campaign campaign(Long id, String name) {
        return Campaign.builder()
                .id(id)
                .campaignName(name)
                .platform(Platform.GOOGLE_ADS)
                .status(Status.ACTIVE)
                .objective(Objective.LEADS)
                .owner("Owner")
                .googleBudget(new BigDecimal("10.00"))
                .metaBudget(new BigDecimal("20.00"))
                .build();
    }

    private CampaignRequest request(String name) {
        CampaignRequest request = new CampaignRequest();
        request.setCampaignName(name);
        request.setPlatform(Platform.META_ADS);
        request.setStatus(Status.ACTIVE);
        request.setObjective(Objective.SALES);
        request.setOwner("Owner");
        request.setStartDate(LocalDate.of(2026, 6, 1));
        request.setEndDate(LocalDate.of(2026, 6, 30));
        request.setGoogleBudget(new BigDecimal("100.00"));
        request.setMetaBudget(new BigDecimal("50.00"));
        request.setTargetAudience("SMB");
        request.setKpi("CPA");
        request.setLandingPage("https://example.com");
        request.setStrategy("Search");
        request.setManualData("manual");
        return request;
    }
}
