package com.dl.lotto.dto;

import java.util.List;

import com.dl.lotto.param.SaveBetInfoParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DIZQUserBetLottoDTO {

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
	
	private Integer ticketNum;
	private Double money;
	private Double bonusAmount;
	private Double surplus;
	private Double thirdPartyPaid;
	private String requestFrom;
	private Integer userId;
	
	
	public DIZQUserBetLottoDTO() {
	}
	public DIZQUserBetLottoDTO(SaveBetInfoParam param) {
		this.times = param.getTimes();
//		this.lotteryClassifyId = param.getLotteryClassifyId();
//		this.lotteryPlayClassifyId = param.getLotteryPlayClassifyId();
		this.bonusId = param.getBonusId();
		this.betNum = param.getBetNum();
		this.orderMoney = param.getOrderMoney();
		this.isAppend = param.getIsAppend();
//		this.termNum = param.getTermNum();
//		this.matchTime = param.getMatchTime();
		this.betInfos = param.getBetInfos();
	}
	
	
	
}
