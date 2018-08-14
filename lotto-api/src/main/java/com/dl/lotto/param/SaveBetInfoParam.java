package com.dl.lotto.param;

import java.util.List;

import com.dl.lotto.dto.LottoBetInfoDTO;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class SaveBetInfoParam {

	@ApiModelProperty(value="投注倍数")
	private Integer times;
	@ApiModelProperty(value="彩票种类")
	private int lotteryClassifyId;
	@ApiModelProperty(value="彩票玩法类别")
	private int lotteryPlayClassifyId;
	@ApiModelProperty(value="用户红包id,如果没有不填写，可以为空")
	private String bonusId;
	@ApiModelProperty(value="投注注数")
	private Integer betNum;
	@ApiModelProperty(value="投注金额")
	private String orderMoney;
	@ApiModelProperty(value="是否追加，0否1是")
	private Integer isAppend;
	@ApiModelProperty(value="期次")
	private String termNum;
	@ApiModelProperty(value="开奖时间")
	private int matchTime;
	@ApiModelProperty(value="投注彩票")
	private List<LottoBetInfoDTO> betInfos;
}
