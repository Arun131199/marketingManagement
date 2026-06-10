package controller;

import dto.CampaignRequest;
import dto.CampaignResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import services.CampaignService;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String search
    ){
        return ResponseEntity.ok(campaignService.getAll(status,platform,search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable Long id){
        return ResponseEntity.ok(campaignService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@Valid @RequestBody CampaignRequest campaignRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(campaignService.create(campaignRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CampaignRequest request
    ) {
        return ResponseEntity.ok(campaignService.update(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import")
    public ResponseEntity<List<CampaignResponse>> importCampaign(@RequestParam("File")MultipartFile file){
        List<CampaignResponse> imported=campaignService.importFromFile(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(imported);
    }

}
