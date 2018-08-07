package com.dl.lotto.enums;

public enum LottoResultEnum {

	OPTION_ERROR(304000,"操作失败！"),
	GET_TICKET_INFO_NULL(304001,"暂停售卖！"),
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
