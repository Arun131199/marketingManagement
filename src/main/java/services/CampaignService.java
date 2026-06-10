package services;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import dto.CampaignRequest;
import dto.CampaignResponse;
import enums.Objective;
import enums.Platform;
import enums.Status;
import exception.ResourceNotFoundException;
import model.Campaign;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import repository.CampaignRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<CampaignResponse> importFromFile(MultipartFile file) {
        String filename = file.getOriginalFilename().toLowerCase();
        try {
            List<CampaignRequest> requests;
            if (filename.endsWith(".csv")) {
                requests = parseCsv(file);
            } else if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
                requests = parseExcel(file);
            } else {
                throw new RuntimeException("Unsupported file format");
            }
            return requests.stream()
                    .map(this::create)  // existing create method reuse
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Import failed: " + e.getMessage());
        }
    }

    private List<CampaignRequest> parseExcel(MultipartFile file) throws Exception {
        List<CampaignRequest> list = new ArrayList<>();
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        boolean firstRow = true;
        for (Row row : sheet) {
            if (firstRow) { firstRow = false; continue; }
            if (isRowEmpty(row)) continue;

            CampaignRequest req = new CampaignRequest();
            req.setCampaignName(getCellValue(row, 0));
            req.setPlatform(parseEnum(Platform.class, getCellValue(row, 1)));
            req.setStatus(parseEnum(Status.class, getCellValue(row, 2)));
            req.setObjective(parseEnum(Objective.class, getCellValue(row, 3)));
            req.setOwner(getCellValue(row, 4));
            req.setStartDate(parseDate(getCellValue(row, 5)));
            req.setEndDate(parseDate(getCellValue(row, 6)));
            req.setGoogleBudget(parseBigDecimal(getCellValue(row, 7)));
            req.setMetaBudget(parseBigDecimal(getCellValue(row, 8)));
            req.setTargetAudience(getCellValue(row, 9));
            req.setKpi(getCellValue(row, 10));
            req.setLandingPage(getCellValue(row, 11));
            req.setStrategy(getCellValue(row, 12));
            req.setManualData(getCellValue(row, 13));
            list.add(req);
        }
        workbook.close();
        return list;
    }

    private List<CampaignRequest> parseCsv(MultipartFile file) throws Exception {
        List<CampaignRequest> list = new ArrayList<>();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));

        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        List<String[]> rows = csvReader.readAll();

        for (String[] r : rows) {
            CampaignRequest req = new CampaignRequest();
            req.setCampaignName(safe(r, 0));
            req.setPlatform(parseEnum(Platform.class, safe(r, 1)));
            req.setStatus(parseEnum(Status.class, safe(r, 2)));
            req.setObjective(parseEnum(Objective.class, safe(r, 3)));
            req.setOwner(safe(r, 4));
            req.setStartDate(parseDate(safe(r, 5)));
            req.setEndDate(parseDate(safe(r, 6)));
            req.setGoogleBudget(parseBigDecimal(safe(r, 7)));
            req.setMetaBudget(parseBigDecimal(safe(r, 8)));
            req.setTargetAudience(safe(r, 9));
            req.setKpi(safe(r, 10));
            req.setLandingPage(safe(r, 11));
            req.setStrategy(safe(r, 12));
            req.setManualData(safe(r, 13));
            list.add(req);
        }
        return list;
    }


    private String safe(String[] arr, int i) {
        return (arr != null && arr.length > i && arr[i] != null) ? arr[i].trim() : "";
    }

    private String getCellValue(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell))
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            default -> "";
        };
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = 0; i < 14; i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) return BigDecimal.ZERO;
        try {
            String cleaned = value.replaceAll("[^0-9.]", "");
            return new BigDecimal(cleaned);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

}
