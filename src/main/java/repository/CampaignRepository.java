package repository;

import enums.Platform;
import enums.Status;
import model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign,Long> {
    List<Campaign> findByStatus(Status status);
    List<Campaign> findByPlatform(Platform platform);
    List<Campaign> findByStatusAndPlatform(Status status,Platform platform);
    List<Campaign> findAllByOrderByCreatedAtDesc();
    List<Campaign> findByStatusOrderByCreatedAtDesc(Status status);
    List<Campaign> findByPlatformOrderByCreatedAtDesc(Platform platform);
    List<Campaign> findByStatusAndPlatformOrderByCreatedAtDesc(Status status, Platform platform);

    @Query("""
        SELECT c FROM Campaign c
        WHERE (:status IS NULL OR c.status = :status)
          AND (:platform IS NULL OR c.platform = :platform)
          AND (LOWER(c.campaignName) LIKE :pattern OR
               LOWER(c.owner) LIKE :pattern OR
               LOWER(c.strategy) LIKE :pattern OR
               LOWER(c.targetAudience) LIKE :pattern)
        ORDER BY c.createdAt DESC
        """)
    List<Campaign> searchByText(
            @Param("status") Status status,
            @Param("platform") Platform platform,
            @Param("pattern") String pattern
    );

    default List<Campaign> search(Status status, Platform platform, String search) {
        if (search == null || search.isBlank()) {
            if (status != null && platform != null) {
                return findByStatusAndPlatformOrderByCreatedAtDesc(status, platform);
            }
            if (status != null) {
                return findByStatusOrderByCreatedAtDesc(status);
            }
            if (platform != null) {
                return findByPlatformOrderByCreatedAtDesc(platform);
            }
            return findAllByOrderByCreatedAtDesc();
        }

        return searchByText(status, platform, "%" + search.toLowerCase() + "%");
    }

}
