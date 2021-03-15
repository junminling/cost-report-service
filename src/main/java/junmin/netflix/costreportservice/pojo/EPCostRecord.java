package junmin.netflix.costreportservice.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Episode Total Cost Model")
public class EPCostRecord implements Serializable {
	@ApiModelProperty(notes = "Episode code")
	String episodeCode;
	@ApiModelProperty(notes = "Episode itemized cost")
	double amount;

	public EPCostRecord(String episodeCode, double amount) {
		this.episodeCode = episodeCode;
		this.amount = amount;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getEpisodeCode() {
		return episodeCode;
	}

	public void setEpisodeCode(String episodeCode) {
		this.episodeCode = episodeCode;
	}
}
