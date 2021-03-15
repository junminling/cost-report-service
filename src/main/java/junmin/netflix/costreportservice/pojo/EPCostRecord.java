package junmin.netflix.costreportservice.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(description = "Episode Total Cost Model")
public class EPCostRecord implements Serializable {
	@ApiModelProperty(notes = "Episode code")
	String episodeCode;
	@ApiModelProperty(notes = "Episode itemized cost")
	BigDecimal amount;

	public EPCostRecord(String episodeCode, BigDecimal amount) {
		this.episodeCode = episodeCode;
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getEpisodeCode() {
		return episodeCode;
	}

	public void setEpisodeCode(String episodeCode) {
		this.episodeCode = episodeCode;
	}
}
