package com.dl.shop.lotto.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "dl_super_lotto")
public class Lotto {

	@Id
    @Column(name = "term_num")
    private Integer term_num;
	/**
     * 开奖日期
     */
	@Column(name = "prize_date")
	private String prize_date;
	/**
     * 中奖号码
     */
	@Column(name = "prize_num")
	private String prize_num;
	/**
     * 奖池
     */
	@Column(name = "prizes")
	private String  prizes;
	/**
     * 创建时间
     */
	@Column(name = "create_time")
	private Integer create_time;
	public Integer getTerm_num() {
		return term_num;
	}
	public void setTerm_num(Integer term_num) {
		this.term_num = term_num;
	}
	public String getPrize_date() {
		return prize_date;
	}
	public void setPrize_date(String prize_date) {
		this.prize_date = prize_date;
	}
	public String getPrize_num() {
		return prize_num;
	}
	public void setPrize_num(String prize_num) {
		this.prize_num = prize_num;
	}
	public String getPrizes() {
		return prizes;
	}
	public void setPrizes(String prizes) {
		this.prizes = prizes;
	}
	public Integer getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
	
}
