package junmin.netflix.costreportservice.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
public class EPItemizedCostEntity {
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Id
	long itemizedId;

	@Column(name="PRODUCTION_NAME")
	String prodName;

	@Column(name="EPISODE_CODE")
	String epCode;

	@Column(name="ITEMIZED_COST")
	double itemizedCost;

	@Column(name="TIMESTAMP")
	long timestamp;

	public EPItemizedCostEntity(){};
	public EPItemizedCostEntity(String prodName, String epCode, double itemizedCost, long timestamp) {
		this.prodName = prodName;
		this.epCode = epCode;
		this.itemizedCost = itemizedCost;
		this.timestamp = timestamp;
	}

	public long getItemizedId() {
		return itemizedId;
	}

	public void setItemizedId(long itemizedId) {
		this.itemizedId = itemizedId;
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

	public double getItemizedCost() {
		return itemizedCost;
	}

	public void setItemizedCost(double itemizedCost) {
		this.itemizedCost = itemizedCost;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
