package services;

import dto.CampaignRequest;
import dto.CampaignResponse;
import enums.Platform;
import enums.Status;
import exception.ResourceNotFoundException;
import model.Campaign;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CampaignRepository;

import java.util.List;

@Service
public class CampaignService {
    private final CampaignRepository repository;

    public CampaignService(CampaignRepository repository) {
        this.repository = repository;
    }

    public List<CampaignResponse> getAll(String status, String platform, String search){
        Status statusObj = parseEnum(Status.class, status);
        Platform platformObj = parseEnum(Platform.class, platform);
        String q=(search!=null&&!search.isBlank())?search.trim():null;
        return repository.search(statusObj, platformObj, q)
                .stream().map(CampaignResponse::from)
                .toList();
    }

    public CampaignResponse getById(Long id){
        return dto.CampaignResponse.from(findOrThrow(id));
    }

    @Transactional
    public CampaignResponse create(CampaignRequest request){
        Campaign campaign=mapToEntity(new Campaign(),request);
        return CampaignResponse.from(repository.save(campaign));
    }

    @Transactional
    public CampaignResponse update(CampaignRequest request, Long id){
        Campaign existing = findOrThrow(id);
        mapToEntity(existing,request);
        return CampaignResponse.from(repository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Campaign not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private Campaign findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
    }

    private Campaign mapToEntity(Campaign c, CampaignRequest req) {
        c.setCampaignName(req.getCampaignName());
        c.setPlatform(req.getPlatform() != null ? req.getPlatform() : Platform.GOOGLE_ADS);
        c.setStatus(req.getStatus() != null ? req.getStatus() : Status.PLANNING);
        c.setObjective(req.getObjective());
        c.setOwner(req.getOwner());
        c.setStartDate(req.getStartDate());
        c.setEndDate(req.getEndDate());
        c.setGoogleBudget(req.getGoogleBudget());
        c.setMetaBudget(req.getMetaBudget());
        c.setTargetAudience(req.getTargetAudience());
        c.setKpi(req.getKpi());
        c.setLandingPage(req.getLandingPage());
        c.setStrategy(req.getStrategy());
        c.setManualData(req.getManualData());
        return c;
    }

    private <T extends Enum<T>> T parseEnum(Class<T> clazz, String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("All")) return null;
        try {
            return Enum.valueOf(clazz, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
