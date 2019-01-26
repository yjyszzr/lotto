package com.dl.lotto.enums;

public enum LottoResultEnum {

	OPTION_ERROR(304000,"操作失败！"),
	GET_TICKET_INFO_NULL(304001,"暂停售卖！"),
	GET_CHART_DATA_NULL(304002,"走势数据异常！"),
	PARAM_ERROR(304003,"参数错误！"),
	BET_INFO_ERROR(304004,"投注信息有误！"),
	BET_NUMBER_LIMIT(304005,"彩票总注数不得超过10000注"),
	BET_TIMES_LIMIT(304006,"彩票总倍数不得超过99999倍"),
	BET_MONEY_LIMIT(304007,"单注彩票金额不得超过2万元"),
	BET_DANTUO_FORBIT(304008,"该版本暂不支持胆拖拖住"),
	WAITE_FOR_PRIZE(304009,"等待开奖"),
	BET_MONEY_other(304100,"");
	
	
	private Integer code;
	private String msg;
	
	LottoResultEnum(Integer code, String msg){
		this.code = code;
		this.msg = msg;
	}

	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
