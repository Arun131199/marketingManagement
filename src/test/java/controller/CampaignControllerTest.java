package controller;

import dto.CampaignRequest;
import dto.CampaignResponse;
import enums.Objective;
import enums.Platform;
import enums.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import services.CampaignService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CampaignControllerTest {

    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController controller;

    @Test
    void getAllCampaignsDelegatesToService() {
        CampaignResponse response = response(1L, "Launch");
        when(campaignService.getAll("ACTIVE", "GOOGLE_ADS", "launch")).thenReturn(List.of(response));

        ResponseEntity<List<CampaignResponse>> result =
                controller.getAllCampaigns("ACTIVE", "GOOGLE_ADS", "launch");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).containsExactly(response);
    }

    @Test
    void getCampaignByIdDelegatesToService() {
        CampaignResponse response = response(2L, "Retarget");
        when(campaignService.getById(2L)).thenReturn(response);

        ResponseEntity<CampaignResponse> result = controller.getCampaignById(2L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void createCampaignReturnsCreatedResponse() {
        CampaignRequest request = new CampaignRequest();
        CampaignResponse response = response(3L, "Created");
        when(campaignService.create(request)).thenReturn(response);

        ResponseEntity<CampaignResponse> result = controller.createCampaign(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void updateDelegatesToServiceWithRequestAndId() {
        CampaignRequest request = new CampaignRequest();
        CampaignResponse response = response(4L, "Updated");
        when(campaignService.update(request, 4L)).thenReturn(response);

        ResponseEntity<CampaignResponse> result = controller.update(4L, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void deleteDelegatesToServiceAndReturnsNoContent() {
        ResponseEntity<Void> result = controller.delete(5L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(campaignService).delete(5L);
    }

    private CampaignResponse response(Long id, String name) {
        return CampaignResponse.builder()
                .id(id)
                .campaignName(name)
                .platform(Platform.GOOGLE_ADS)
                .status(Status.ACTIVE)
                .objective(Objective.LEADS)
                .build();
    }
}
