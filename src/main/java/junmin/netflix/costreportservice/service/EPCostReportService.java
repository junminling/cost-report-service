package junmin.netflix.costreportservice.service;

import junmin.netflix.costreportservice.exception.ErrorEnum;
import junmin.netflix.costreportservice.exception.InvalidProductionException;
import junmin.netflix.costreportservice.exception.InvalidReportException;
import junmin.netflix.costreportservice.model.EPItemizedCostEntity;
import junmin.netflix.costreportservice.model.EPTotalCostEntity;
import junmin.netflix.costreportservice.pojo.EPCostRecord;
import junmin.netflix.costreportservice.repository.EPItemizedCostRepository;
import junmin.netflix.costreportservice.repository.EPTotalCostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EPCostReportService {
	private EPTotalCostRepository epTotalCostRepo;
	private EPItemizedCostRepository epItemizedCostRepo;

	public EPCostReportService(EPTotalCostRepository epTotalCostRepo, EPItemizedCostRepository epItemizedCostRepo){
		this.epTotalCostRepo = epTotalCostRepo;
		this.epItemizedCostRepo = epItemizedCostRepo;
	}

	private final Logger LOGGER = LoggerFactory.getLogger(EPCostReportService.class);
	private final String AMORIZEDCODE = "001";


	public List<EPCostRecord> processEPCostReport(String prodName, List<EPCostRecord> costs) throws InvalidReportException
	{
		validateProdName(prodName);
		String epCode = getEPCodeFromEPReport(costs);
		long timestamp = System.currentTimeMillis();
		double totalCost=0.00d;

		for(int i=0; i<costs.size(); i++){
			totalCost += costs.get(i).getAmount();
			epItemizedCostRepo.save(new EPItemizedCostEntity(prodName, epCode, costs.get(i).getAmount(), timestamp));
		}
		epTotalCostRepo.save(new EPTotalCostEntity(prodName, epCode, totalCost, 0, totalCost, timestamp));
		return Arrays.asList(new EPCostRecord(epCode, totalCost));
	}

	public List<EPCostRecord> getCostReportByProdNameAndEpCode(String prodName, String epCode) throws InvalidProductionException {
		List<EPTotalCostEntity> entities;
		if(epCode == null || epCode.isEmpty()){
			entities = epTotalCostRepo.findByProdName(prodName);
			if(entities==null || entities.size()==0){
				throw new InvalidProductionException(
						ErrorEnum.PRODUCTION_NOT_EXIST.getId(), prodName + " does not exist");
			}
		}else {
			entities = epTotalCostRepo.findByProdNameAndEpCode(prodName, epCode);
			if(entities==null || entities.size()==0){
				throw new InvalidProductionException(
						ErrorEnum.PRODUCTION_EP_NOT_EXIST.getId(), prodName+":"+epCode+ " does not exist");
			}
		}

		List<EPCostRecord> costs = new ArrayList<>();
		for(EPTotalCostEntity entity: entities){
			costs.add(new EPCostRecord(entity.getEpCode(), entity.getTotalCost()));
		}
		return costs;
	}

	public List<EPCostRecord> processProdCostReport(String prodName, List<EPCostRecord> itemizedCosts) throws InvalidReportException
	{
		validateProdName(prodName);
		// store ep keys in order in the Map
		Map<String, Double> prodCostMap = new TreeMap<>();
		// store each epCostRecord, calculate total cost for each EPCode
		long timestamp = System.currentTimeMillis();
		for(EPCostRecord cost : itemizedCosts){
			String epCode = cost.getEpisodeCode();
			double amount = cost.getAmount();
			if(prodCostMap.get(epCode)==null){
				prodCostMap.put(epCode, amount);
			}else{
				prodCostMap.put(epCode, prodCostMap.get(epCode)+amount);
			}
//			epItemizedCostRepo.save(new EPItemizedCostEntity(prodName, epCode, amount, timestamp));
		}

		// store each EP total cost, return back EP total cost summary
		List<EPCostRecord> totalCostRecords = new ArrayList<>();
		for(String key : prodCostMap.keySet()){
			epTotalCostRepo.save(new EPTotalCostEntity(prodName, key, prodCostMap.get(key), 0, prodCostMap.get(key), timestamp));
			totalCostRecords.add(new EPCostRecord(key, prodCostMap.get(key)));
		}
		return totalCostRecords;
	}

	public List<EPCostRecord> processAmortizedProdCostReport(String prodName, List<EPCostRecord> itemizedCosts) throws InvalidReportException
	{
		validateProdName(prodName);
		// store ep keys in order in the Map
		Map<String, Double> prodCostMap = new TreeMap<>();

		// store each itemized cost into DB, and calculate total cost for each EPCode
		long timestamp = System.currentTimeMillis();
		for(EPCostRecord cost : itemizedCosts){
			String epCode = cost.getEpisodeCode();
			double amount = cost.getAmount();
			if(prodCostMap.get(epCode)==null){
				prodCostMap.put(epCode, amount);
			}else{
				prodCostMap.put(epCode, prodCostMap.get(epCode)+amount);
			}
//			epItemizedCostRepo.save(new EPItemizedCostEntity(prodName, epCode, amount, timestamp));
		}

		// calculate amortized cost
		Double amoritzedCost = prodCostMap.get(AMORIZEDCODE);
		double epAmortizedCost = 0;
		if(amoritzedCost!=null) {
			int totalEps = prodCostMap.keySet().size()-1;
			epAmortizedCost = amoritzedCost/totalEps;
		}

		// store each EP total cost including amortized cost, return back EP total cost summary
		List<EPCostRecord> totalCostRecords = new ArrayList<>();
		for(String key : prodCostMap.keySet()){
			if(key.equalsIgnoreCase(AMORIZEDCODE)) continue;
			epTotalCostRepo.save(new EPTotalCostEntity(prodName, key, prodCostMap.get(key), epAmortizedCost, prodCostMap.get(key)+epAmortizedCost, timestamp));
			totalCostRecords.add(new EPCostRecord(key, prodCostMap.get(key)+epAmortizedCost));
		}
		return totalCostRecords;
	}

	private String getEPCodeFromEPReport(List<EPCostRecord> costList) throws InvalidReportException {
		// throw InvalidReportException if report is empty
		if(costList==null || costList.size()==0) {
			throw createInvalidReportException(ErrorEnum.EMPTY_REPORT, "cost report is empty");
		}

		String epCode = null;
		for(EPCostRecord cost : costList){
			if(epCode == null){
				epCode = cost.getEpisodeCode();
			}
			// throw InvalidReportException if a single EP cost report contains multiple EP code,
			if(epCode!=null && !epCode.equalsIgnoreCase(cost.getEpisodeCode())){
				throw createInvalidReportException(ErrorEnum.INVALID_EP_CODE, cost.getEpisodeCode() + " is not valid EPCode");
			}
		}

		return epCode;
	}

	private void validateProdName(String prodName) throws InvalidReportException {
		if (prodName == null || prodName.isEmpty()) {
			InvalidReportException exception = new InvalidReportException(
					ErrorEnum.PRODUCTION_NAME_IS_EMPTY.getId(), "production name in request URL path cannot be empty");
			LOGGER.error(ErrorEnum.PRODUCTION_NAME_IS_EMPTY.getName(), exception);
			throw exception;
		}
	}

	private InvalidReportException createInvalidReportException(ErrorEnum errorEnum, String message) {
		InvalidReportException exception = new InvalidReportException(errorEnum.getId(), message);
		LOGGER.error(errorEnum.getName(), exception);
		return exception;
	}
}
