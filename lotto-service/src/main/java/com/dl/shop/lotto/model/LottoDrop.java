package com.dl.shop.lotto.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "dl_super_lotto_drop")
public class LottoDrop {
	@Id
    @Column(name = "term_num")
    private Integer termNum;
	/**
     * 前区遗漏
     */
	@Column(name = "pre_drop")
	private String preDrop;
	/**
     * 后区遗漏
     */
	@Column(name = "post_drop")
	private String postDrop;
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
	public String getPreDrop() {
		return preDrop;
	}
	public void setPreDrop(String preDrop) {
		this.preDrop = preDrop;
	}
	public String getPostDrop() {
		return postDrop;
	}
	public void setPostDrop(String postDrop) {
		this.postDrop = postDrop;
	}
	public Integer getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	
	
	
}
