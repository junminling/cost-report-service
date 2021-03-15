package junmin.netflix.costreportservice.controller;

import com.google.gson.Gson;
import junmin.netflix.costreportservice.pojo.EPCostRecord;
import junmin.netflix.costreportservice.service.EPCostReportService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CostReportController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CostReportControllerTest {
	@Autowired
	MockMvc mvc;
	@MockBean
	EPCostReportService mockService;
	Gson gson;

	@BeforeAll
	public void setup(){
		gson = new Gson();
	}

	@Test
	void postEPCostReport() throws Exception {
		when(mockService.processEPCostReport(any(), anyList())).thenReturn(getEPCostRecord());
		RequestBuilder request = MockMvcRequestBuilders
				.post("/api/report/production/MaryAndMartin/ep")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(sampleMMEPCostInput());
		MockHttpServletResponse resp = mvc.perform(request).andReturn().getResponse();
		assertTrue(resp.getStatus()==200);
		assertTrue(resp.getContentAsString().equalsIgnoreCase(expectPostEPCostReport()));
	}

	@Test
	public void getProdCostReportByProdNameAndEP() throws Exception {
		String epCode = "101";
		String production = "MaryAndMartin";
		double amount = 2050.00;
		when(mockService.getCostReportByProdNameAndEpCode(production, epCode)).thenReturn(getEPCostRecord());
		RequestBuilder request = MockMvcRequestBuilders
				.get("/api/report/production/"+production)
				.param("epCode", epCode)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		MockHttpServletResponse resp = mvc.perform(request).andReturn().getResponse();
		assertTrue(resp.getStatus()==200);
		assertTrue(resp.getContentAsString().equalsIgnoreCase(expectPostEPCostReport()));
	}

	@Test
	void postProdCostReport() throws Exception {
		when(mockService.processProdCostReport(any(), anyList())).thenReturn(getBBTotalCostEntities());
		RequestBuilder request = MockMvcRequestBuilders
				.post("/api/report/production/BurntBiscuits")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(sampleBBCostInput());
		MockHttpServletResponse resp = mvc.perform(request).andReturn().getResponse();
		assertTrue(resp.getStatus()==200);
		System.out.println(resp.getContentAsString());
		System.out.println(expectPostProdCostReport());
		assertTrue(resp.getContentAsString().equalsIgnoreCase(expectPostProdCostReport()));
	}

	@Test
	void postAmortizedProdCostReport() throws Exception{
		when(mockService.processAmortizedProdCostReport(any(), anyList())).thenReturn(getBBAmortizedTotalCostEntities());
		RequestBuilder request = MockMvcRequestBuilders
				.post("/api/report/production/BurntBiscuitsII/amortized")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(sampleBBAmortizedCostInput());
		MockHttpServletResponse resp = mvc.perform(request).andReturn().getResponse();
		assertTrue(resp.getStatus()==200);
		System.out.println(resp.getContentAsString());
		System.out.println(expectAmortizedPostProdCostReport());
		assertTrue(resp.getContentAsString().equalsIgnoreCase(expectAmortizedPostProdCostReport()));
	}




	private String sampleMMEPCostInput() {
		return "[{\"amount\":200.00,\"episodeCode\":\"101\"},{\"amount\":300.00,\"episodeCode\":\"101\"},{\"amount\":150.00,\"episodeCode\":\"101\"},{\"amount\":75.00,\"episodeCode\":\"101\"},{\"amount\":1325.00,\"episodeCode\":\"101\"}]";
	}

	private String sampleBBCostInput() {
		return "[{\"amount\": 200.00, \"episodeCode\": \"101\"}, {\"amount\": 300.00, \"episodeCode\": \"101\"}, {\"amount\": 150.00, \"episodeCode\": \"102\"}, {\"amount\": 75.00, \"episodeCode\": \"102\"}, {\"amount\": 1325.00, \"episodeCode\": \"103\"}, {\"amount\": 613.00, \"episodeCode\": \"104\"}, {\"amount\": 88.00, \"episodeCode\": \"105\"}, {\"amount\": 916.00, \"episodeCode\": \"105\"}, {\"amount\": 250.00, \"episodeCode\": \"105\"}]";
	}

	private String sampleBBAmortizedCostInput() {
		return "[{\"amount\": 200.00, \"episodeCode\": \"101\"}, {\"amount\": 300.00, \"episodeCode\": \"101\"}, {\"amount\": 150.00, \"episodeCode\": \"102\"}, {\"amount\": 800.00, \"episodeCode\": \"001\"}, {\"amount\": 75.00, \"episodeCode\": \"102\"}, {\"amount\": 1325.00, \"episodeCode\": \"103\"}, {\"amount\": 613.00, \"episodeCode\": \"104\"}, {\"amount\": 750.00, \"episodeCode\": \"001\"}, {\"amount\": 88.00, \"episodeCode\": \"105\"}, {\"amount\": 916.00, \"episodeCode\": \"105\"}, {\"amount\": 250.00, \"episodeCode\": \"105\"}]\n";
	}

	private String expectMMEPTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":2050.0}]";
	}

	private String expectBBTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":500.0},{\"episodeCode\":\"102\",\"amount\":225.0},{\"episodeCode\":\"103\",\"amount\":1325.0},{\"episodeCode\":\"104\",\"amount\":613.0},{\"episodeCode\":\"105\",\"amount\":1254.0}]";
	}

	private String expectBBAmortizedTotalCostReport(){
		return "[{\"episodeCode\":\"101\",\"amount\":810.0},{\"episodeCode\":\"102\",\"amount\":535.0},{\"episodeCode\":\"103\",\"amount\":1635.0},{\"episodeCode\":\"104\",\"amount\":923.0},{\"episodeCode\":\"105\",\"amount\":1564.0}]";
	}

	private List<EPCostRecord> getEPCostRecord(){
		return Arrays.asList(new EPCostRecord("101", 2050.00));
	}

	private List<EPCostRecord> getBBTotalCostEntities(){
		long timestamp = System.currentTimeMillis();
		return Arrays.asList(
				new EPCostRecord("101", 500.00),
				new EPCostRecord("102", 225.00),
				new EPCostRecord("103", 1325.00),
				new EPCostRecord("104", 613.00),
				new EPCostRecord("105", 1254.00)
		);
	}

	private List<EPCostRecord> getBBAmortizedTotalCostEntities(){
		long timestamp = System.currentTimeMillis();
		return Arrays.asList(
				new EPCostRecord("101", 810.00),
				new EPCostRecord("102", 535.00),
				new EPCostRecord("103", 1635.00),
				new EPCostRecord("104", 923.00),
				new EPCostRecord("105", 1564.00)
		);
	}

	private String expectPostEPCostReport(){
		return gson.toJson(Arrays.asList(new EPCostRecord("101", 2050.0)));
	}

	private String expectPostProdCostReport(){
		long timestamp = System.currentTimeMillis();
		return gson.toJson(Arrays.asList(
				new EPCostRecord("101", 500.0),
				new EPCostRecord("102", 225.0),
				new EPCostRecord("103", 1325.0),
				new EPCostRecord("104", 613.0),
				new EPCostRecord("105", 1254.0)
		));
	}

	private String expectAmortizedPostProdCostReport(){
		long timestamp = System.currentTimeMillis();
		return gson.toJson(Arrays.asList(
				new EPCostRecord("101", 810.0),
				new EPCostRecord("102", 535.0),
				new EPCostRecord("103", 1635.0),
				new EPCostRecord("104", 923.0),
				new EPCostRecord("105", 1564.0)
		));
	}

}