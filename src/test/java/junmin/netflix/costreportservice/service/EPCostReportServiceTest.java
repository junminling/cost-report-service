package junmin.netflix.costreportservice.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import junmin.netflix.costreportservice.exception.InvalidProductionException;
import junmin.netflix.costreportservice.exception.InvalidReportException;
import junmin.netflix.costreportservice.model.EPItemizedCostEntity;
import junmin.netflix.costreportservice.model.EPTotalCostEntity;
import junmin.netflix.costreportservice.pojo.EPCostRecord;
import junmin.netflix.costreportservice.repository.EPItemizedCostRepository;
import junmin.netflix.costreportservice.repository.EPTotalCostRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EPCostReportServiceTest {
	EPItemizedCostRepository mockEPItemCostRepo;
	EPTotalCostRepository mockEPTotalCostRepo;
	EPCostReportService service;
	Gson gson;

	@BeforeAll
	void setup(){
		gson = new Gson();
		mockEPItemCostRepo = mock(EPItemizedCostRepository.class);
		mockEPTotalCostRepo = mock(EPTotalCostRepository.class);
		service = new EPCostReportService(mockEPTotalCostRepo, mockEPItemCostRepo);
		when(mockEPTotalCostRepo.save(any(EPTotalCostEntity.class))).thenReturn(null);
		when(mockEPItemCostRepo.save(any(EPItemizedCostEntity.class))).thenReturn(null);
	}
	@Test
	void processEPCostReport() throws InvalidReportException {
		List<EPCostRecord> result = service.processEPCostReport("MaryAndMartin", sampleMMEPCostInput());
		assertTrue(gson.toJson(result).equalsIgnoreCase(expectMMEPTotalCostReport()));
	}

	@Test()
	void processEPCostReport_withMultipleEPCode_throwException() {
		Exception exception = assertThrows(InvalidReportException.class, () -> {
			service.processEPCostReport("MaryAndMartin", invalidSampleMMEPCostInput());
		});
		assertTrue(exception.getMessage().equalsIgnoreCase("102 is not valid EPCode"));
	}

	@Test()
	void processEPCostReport_withEmptyInput_throwException() {
		Exception exception = assertThrows(InvalidReportException.class, () -> {
			service.processEPCostReport("MaryAndMartin", Collections.emptyList());
		});
		assertTrue(exception.getMessage().equalsIgnoreCase("cost report is empty"));
	}

	@Test
	void getCostReportByProdNameAndEpCode() throws InvalidProductionException {
		when(mockEPTotalCostRepo.findByProdNameAndEpCode("MaryAndMartin", "101"))
				.thenReturn(getMMEPTotalCostEntities());
		List<EPCostRecord> epCostResult = service.getCostReportByProdNameAndEpCode("MaryAndMartin", "101");
		System.out.println(gson.toJson(epCostResult));
		System.out.println(expectMMEPTotalCostReport());
		assertTrue(gson.toJson(epCostResult).equalsIgnoreCase(expectGetMMEPTotalCostReport()));

		when(mockEPTotalCostRepo.findByProdName("BurntBuscuits"))
				.thenReturn(getBBTotalCostEntities());
		List<EPCostRecord> prodCostResult = service.getCostReportByProdNameAndEpCode("BurntBuscuits", null);
		assertTrue(gson.toJson(prodCostResult).equalsIgnoreCase(expectGetBBTotalCostReport()));

		when(mockEPTotalCostRepo.findByProdName("BurntBuscuitsAmortized"))
				.thenReturn(getBBAmortizedTotalCostEntities());
		List<EPCostRecord> amortizedCostResult = service.getCostReportByProdNameAndEpCode("BurntBuscuitsAmortized", null);
		assertTrue(gson.toJson(amortizedCostResult).equalsIgnoreCase(expectGetBBAmortizedTotalCostReport()));
	}

	@Test()
	void getCostReportByProdNameAndEpCode_with_invalid_prodName_EPCode_throwException() {
		when(mockEPTotalCostRepo.findByProdNameAndEpCode("MaryAndMartin", "101"))
				.thenReturn(Collections.EMPTY_LIST);
		Exception exception = assertThrows(InvalidProductionException.class, () -> {
			service.getCostReportByProdNameAndEpCode("MaryAndMartin", "101");
		});
		assertTrue(exception.getMessage().equalsIgnoreCase("MaryAndMartin:101 does not exist"));
	}

	@Test()
	void getCostReportByProdNameAndEpCode_with_invalid_prodName_throwException() {
		when(mockEPTotalCostRepo.findByProdName("BurntBuscuits"))
				.thenReturn(Collections.EMPTY_LIST);
		Exception exception = assertThrows(InvalidProductionException.class, () -> {
			service.getCostReportByProdNameAndEpCode("BurntBuscuits", null);
		});
		assertTrue(exception.getMessage().equalsIgnoreCase("BurntBuscuits does not exist"));
	}

	@Test
	void processProdCostReport() throws InvalidReportException {
		List<EPCostRecord> result = service.processProdCostReport("BurntBuicuits", sampleBBCostInput());
		assertTrue(gson.toJson(result).equalsIgnoreCase(expectBBTotalCostReport()));
	}

	@Test
	void processAmortizedProdCostReport() throws InvalidReportException {
		List<EPCostRecord> result = service.processAmortizedProdCostReport("BurntBuicuitsII", sampleBBAmortizedCostInput());
		assertTrue(gson.toJson(result).equalsIgnoreCase(expectBBAmortizedTotalCostReport()));
	}

	@Test()
	void processProdCostReport_with_empty_prodName_throwException() {
		Exception exception = assertThrows(InvalidReportException.class, () -> {
			service.processProdCostReport("", sampleBBCostInput());
		});
		assertTrue(exception.getMessage().equalsIgnoreCase("production name in request URL path cannot be empty"));
	}

	private List<EPCostRecord> sampleMMEPCostInput() {
		String epCostReport = "[{\"amount\":200.00,\"episodeCode\":\"101\"},{\"amount\":300.00,\"episodeCode\":\"101\"},{\"amount\":150.00,\"episodeCode\":\"101\"},{\"amount\":75.00,\"episodeCode\":\"101\"},{\"amount\":1325.00,\"episodeCode\":\"101\"}]";
		return gson.fromJson(epCostReport, new TypeToken<ArrayList<EPCostRecord>>(){}.getType());
	}

	private List<EPCostRecord> sampleBBCostInput() {
		String epCostReport = "[{\"amount\": 200.00, \"episodeCode\": \"101\"}, {\"amount\": 300.00, \"episodeCode\": \"101\"}, {\"amount\": 150.00, \"episodeCode\": \"102\"}, {\"amount\": 75.00, \"episodeCode\": \"102\"}, {\"amount\": 1325.00, \"episodeCode\": \"103\"}, {\"amount\": 613.00, \"episodeCode\": \"104\"}, {\"amount\": 88.00, \"episodeCode\": \"105\"}, {\"amount\": 916.00, \"episodeCode\": \"105\"}, {\"amount\": 250.00, \"episodeCode\": \"105\"}]";
		return gson.fromJson(epCostReport, new TypeToken<ArrayList<EPCostRecord>>(){}.getType());
	}

	private List<EPCostRecord> sampleBBAmortizedCostInput() {
		String epCostReport = "[{\"amount\": 200.00, \"episodeCode\": \"101\"}, {\"amount\": 300.00, \"episodeCode\": \"101\"}, {\"amount\": 150.00, \"episodeCode\": \"102\"}, {\"amount\": 800.00, \"episodeCode\": \"001\"}, {\"amount\": 75.00, \"episodeCode\": \"102\"}, {\"amount\": 1325.00, \"episodeCode\": \"103\"}, {\"amount\": 613.00, \"episodeCode\": \"104\"}, {\"amount\": 750.00, \"episodeCode\": \"001\"}, {\"amount\": 88.00, \"episodeCode\": \"105\"}, {\"amount\": 916.00, \"episodeCode\": \"105\"}, {\"amount\": 250.00, \"episodeCode\": \"105\"}]\n";
		return gson.fromJson(epCostReport, new TypeToken<ArrayList<EPCostRecord>>(){}.getType());
	}

	private List<EPCostRecord> invalidSampleMMEPCostInput() {
		String epCostReport = "[{\"amount\":200.00,\"episodeCode\":\"101\"},{\"amount\":300.00,\"episodeCode\":\"102\"},{\"amount\":150.00,\"episodeCode\":\"101\"},{\"amount\":75.00,\"episodeCode\":\"101\"},{\"amount\":1325.00,\"episodeCode\":\"101\"}]";
		return gson.fromJson(epCostReport, new TypeToken<ArrayList<EPCostRecord>>(){}.getType());
	}

	private String expectMMEPTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":2050.00}]";
	}

	private String expectBBTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":500.00},{\"episodeCode\":\"102\",\"amount\":225.00},{\"episodeCode\":\"103\",\"amount\":1325.00},{\"episodeCode\":\"104\",\"amount\":613.00},{\"episodeCode\":\"105\",\"amount\":1254.00}]";
	}

	private String expectBBAmortizedTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":810.00},{\"episodeCode\":\"102\",\"amount\":535.00},{\"episodeCode\":\"103\",\"amount\":1635.00},{\"episodeCode\":\"104\",\"amount\":923.00},{\"episodeCode\":\"105\",\"amount\":1564.00}]";
	}

	private String expectGetMMEPTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":2050}]";
	}

	private String expectGetBBTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":500},{\"episodeCode\":\"102\",\"amount\":225},{\"episodeCode\":\"103\",\"amount\":1325},{\"episodeCode\":\"104\",\"amount\":613},{\"episodeCode\":\"105\",\"amount\":1254}]";
	}

	private String expectGetBBAmortizedTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":810},{\"episodeCode\":\"102\",\"amount\":535},{\"episodeCode\":\"103\",\"amount\":1635},{\"episodeCode\":\"104\",\"amount\":923},{\"episodeCode\":\"105\",\"amount\":1564}]";
	}

	private List<EPTotalCostEntity> getMMEPTotalCostEntities(){
		long timestamp = System.currentTimeMillis();
		return Arrays.asList(
				new EPTotalCostEntity("MaryAndMartin", "101", new BigDecimal(2050.00), null, new BigDecimal(2050.00), timestamp)
		);
	}

	private List<EPTotalCostEntity> getBBTotalCostEntities(){
		long timestamp = System.currentTimeMillis();
		return Arrays.asList(
				new EPTotalCostEntity("BurntBiscuits","101", new BigDecimal(500.00), null, new BigDecimal(500.00), timestamp),
				new EPTotalCostEntity("BurntBiscuits","102", new BigDecimal(225.00), null, new BigDecimal(225.00), timestamp),
				new EPTotalCostEntity("BurntBiscuits","103", new BigDecimal(1325.00), null, new BigDecimal(1325.00), timestamp),
				new EPTotalCostEntity("BurntBiscuits","104", new BigDecimal(613.00), null, new BigDecimal(613.00), timestamp),
				new EPTotalCostEntity("BurntBiscuits","105", new BigDecimal(1254.00), null, new BigDecimal(1254.00), timestamp)
		);
	}

	private List<EPTotalCostEntity> getBBAmortizedTotalCostEntities(){
		long timestamp = System.currentTimeMillis();
		return Arrays.asList(
				new EPTotalCostEntity("BurntBiscuitsII","101", new BigDecimal(810.00), null, new BigDecimal(810.00), timestamp),
				new EPTotalCostEntity("BurntBiscuitsII","102", new BigDecimal(535.00), null, new BigDecimal(535.00), timestamp),
				new EPTotalCostEntity("BurntBiscuitsII","103", new BigDecimal(1635.00), null, new BigDecimal(1635.00), timestamp),
				new EPTotalCostEntity("BurntBiscuitsII","104", new BigDecimal(923.00), null, new BigDecimal(923.00), timestamp),
				new EPTotalCostEntity("BurntBiscuitsII","105", new BigDecimal(1564.00), null, new BigDecimal(1564.00), timestamp)
		);
	}

}