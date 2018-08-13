package com.dl.shop.lotto.model;

import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dl.lotto.dto.LottoNumDTO;

@Table(name = "dl_super_lotto")
public class Lotto {

	@Id
    @Column(name = "term_num")
    private Integer termNum;
	/**
     * 开奖日期
     */
	@Column(name = "prize_date")
	private String prizeDate;
	/**
     * 中奖号码
     */
	@Column(name = "prize_num")
	private String prizeNum;
	/**
     * 奖池
     */
	@Column(name = "prizes")
	private String  prizes;
	/**
     * 创建时间
     */
	@Column(name = "create_time")
	private Integer createTime;
	
	public Integer getTermNum() {
		return termNum;
	}
	public void setTermNum(Integer termNum) {
		this.termNum = termNum;
	}
	public String getPrizeDate() {
		return prizeDate;
	}
	public void setPrizeDate(String prizeDate) {
		this.prizeDate = prizeDate;
	}
	public String getPrizeNum() {
		return prizeNum;
	}
	public void setPrizeNum(String prizeNum) {
		this.prizeNum = prizeNum;
	}
	public String getPrizes() {
		return prizes;
	}
	public void setPrizes(String prizes) {
		this.prizes = prizes;
	}
	public Integer getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	
	public LottoNumDTO lottoNumDto() {
		LottoNumDTO dto = new LottoNumDTO();
		dto.setNumList(Arrays.asList(this.getPrizeNum().split(",")));
		dto.setTermNum(this.getTermNum()+"期");
		return dto;
	}
	
}
