package junmin.netflix.costreportservice.controller;

import junmin.netflix.costreportservice.exception.InvalidProductionException;
import junmin.netflix.costreportservice.exception.InvalidReportException;
import junmin.netflix.costreportservice.pojo.EPCostRecord;
import junmin.netflix.costreportservice.service.EPCostReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CostReportController {
	private EPCostReportService service;
	private final Logger LOGGER = LoggerFactory.getLogger(CostReportController.class);

	public CostReportController(EPCostReportService service){
		this.service = service;
	}

	@PostMapping(path="/report/production/{prodName}/ep",consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<EPCostRecord>> postEPCostReport(@PathVariable("prodName") String prodName, @RequestBody List<EPCostRecord> costs){
		try {
			List<EPCostRecord> result = service.processEPCostReport(prodName, costs);
			return ResponseEntity.ok(result);
		}catch(InvalidReportException e){
			LOGGER.error("POST /api/report/production/"+prodName+"/ep failed: " + e.getMessage());
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Invalid request payload", e);
		}
	}

	@GetMapping(path="/report/production/{prodName}",consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<EPCostRecord>> getProdCostReport(@PathVariable("prodName") String prodName, @RequestParam(name="epCode",required = false) String epCode){
		try {
			List<EPCostRecord> result = service.getCostReportByProdNameAndEpCode(prodName, epCode);
			return ResponseEntity.ok(result);
		}catch(InvalidProductionException e){
			LOGGER.error("GET /api/report/production/"+prodName+"?epCode="+epCode+" failed: " +  e.getMessage());
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Resource Not Found", e);
		}
	}

	@PostMapping(path="/report/production/{prodName}",consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<EPCostRecord>> postProdCostReport(@PathVariable("prodName") String prodName, @RequestBody List<EPCostRecord> costs){
		try {
			List<EPCostRecord> result = service.processProdCostReport(prodName, costs);
			return ResponseEntity.ok(result);
		}catch(InvalidReportException e){
			LOGGER.error("POST /api/report/production/"+prodName+" failed: "+e.getMessage());
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Invalid request payload", e);
		}
	}

	@PostMapping(path="/report/production/{prodName}/amortized",consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<EPCostRecord>> postAmortizedProdCostReport(@PathVariable("prodName") String prodName, @RequestBody List<EPCostRecord> costs){
		try {
			List<EPCostRecord> result = service.processAmortizedProdCostReport(prodName, costs);
			return ResponseEntity.ok(result);
		}catch(InvalidReportException e){
			LOGGER.error("POST /api/report/production/"+prodName+"/amortized failed: "+e.getMessage());
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, "Invalid request payload", e);
		}
	}
}
