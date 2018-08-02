package com.dl.shop.lotto.model;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "dl_super_lotto_drop")
public class LottoDrop {
	@Id
    @Column(name = "term_num")
    private Integer term_num;
	/**
     * 前区遗漏
     */
	@Column(name = "pre_drop")
	private String pre_drop;
	/**
     * 后区遗漏
     */
	@Column(name = "post_drop")
	private String post_drop;
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
	public String getPre_drop() {
		return pre_drop;
	}
	public void setPre_drop(String pre_drop) {
		this.pre_drop = pre_drop;
	}
	public String getPost_drop() {
		return post_drop;
	}
	public void setPost_drop(String post_drop) {
		this.post_drop = post_drop;
	}
	public Integer getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}
	
	
}
