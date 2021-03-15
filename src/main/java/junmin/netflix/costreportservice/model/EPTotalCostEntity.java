package junmin.netflix.costreportservice.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@Table(name="EP_TOTAL_COST",uniqueConstraints={@UniqueConstraint(columnNames = {"prodName", "epCode", "timestamp"})})
public class EPTotalCostEntity {
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Id
	long id;

	String prodName;

	String epCode;

	double itemizedTotalCost;

	double amortizedCost;

	double totalCost;

	long timestamp;

	public EPTotalCostEntity(){};
	public EPTotalCostEntity(String prodName, String epCode, double itemizedTotalCost, double amortizedCost, double totalCost, long timestamp) {
		this.prodName = prodName;
		this.epCode = epCode;
		this.itemizedTotalCost = itemizedTotalCost;
		this.amortizedCost = amortizedCost;
		this.totalCost = totalCost;
		this.timestamp = timestamp;
	}

	public String getProdName() {
		return prodName;
	}

	public void setProdName(String prodName) {
		this.prodName = prodName;
	}

	public String getEpCode() {
		return epCode;
	}

	public void setEpCode(String epCode) {
		this.epCode = epCode;
	}

	public double getItemizedTotalCost() {
		return itemizedTotalCost;
	}

	public void setItemizedTotalCost(double itemizedTotalCost) {
		this.itemizedTotalCost = itemizedTotalCost;
	}

	public double getAmortizedCost() {
		return amortizedCost;
	}

	public void setAmortizedCost(double amortizedCost) {
		this.amortizedCost = amortizedCost;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
