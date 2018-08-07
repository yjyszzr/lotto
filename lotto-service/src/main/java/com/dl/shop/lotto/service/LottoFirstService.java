package com.dl.shop.lotto.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dl.base.result.BaseResult;
import com.dl.base.result.ResultGenerator;
import com.dl.lotto.dto.LottoDTO;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.dto.LottoNumDTO;
import com.dl.shop.lotto.dao2.LottoDropMapper;
import com.dl.shop.lotto.dao2.LottoMapper;
import com.dl.shop.lotto.model.Lotto;
import com.dl.shop.lotto.model.LottoDrop;
import com.dl.shop.lotto.utils.TermEndDateUtil;

@Service
@Transactional(value="transactionManager2")
public class LottoFirstService {
	@Resource
    private LottoMapper lottoMapper;
	@Resource
	private LottoDropMapper lottoDropMapper;
	
	  
	/**
     * 投注页数据
     */
	 public LottoFirstDTO queryFirstData(){
		 LottoFirstDTO lottoFirstDTO = null;
		 //获取最近10期的开奖
		 List<Lotto> lottos = lottoMapper.getLastNumLottos(10);
		 List<LottoNumDTO> lottoPrizeNumDTOs = new ArrayList<>();
		 if(lottos.size() >= 0) {
			 Lotto lastLotto = lottos.get(0);
			 //@Todo
			 //判断该期次和当前日期是否匹配???
			 lottos.sort((item1,item2)->item1.getTermNum().compareTo(item2.getTermNum()));
			 lottos.forEach(l->{
				 LottoNumDTO lottoPrizeNumDTO = new LottoNumDTO();
				 lottoPrizeNumDTO.setTermNum(l.getTermNum()+"期");
				 lottoPrizeNumDTO.setNumList((Arrays.asList(l.getPrizeNum().split(","))));
				 lottoPrizeNumDTOs.add(lottoPrizeNumDTO);
			 });
			 //
			 lottoFirstDTO = new LottoFirstDTO();
			 lottoFirstDTO.setTerm_num(lastLotto.getTermNum()+1);
			 lottoFirstDTO.setEndDate(TermEndDateUtil.getChoseEndTime());
			 lottoFirstDTO.setPrizes(lastLotto.getPrizes());
			 lottoFirstDTO.setPrizeList(lottoPrizeNumDTOs);
			 LottoDrop lottoDrop = lottoDropMapper.getLottoDropByTermNum(lastLotto.getTermNum());
			 if(lottoDrop != null) {
				 lottoFirstDTO.setPreList(Arrays.asList(lottoDrop.getPreDrop().split(",")));
				 lottoFirstDTO.setPostList(Arrays.asList(lottoDrop.getPostDrop().split(",")));
			 }
		 }
		 return lottoFirstDTO;
	 }
	  
	 
}
