package com.dl.lotto.dto;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LottoDropDTO {
	@ApiModelProperty(value="期号")
	private Integer term_num;
	@ApiModelProperty(value=" 遗漏号码")
	private List<String> list; 
}
