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
import com.dl.lotto.dto.LottoPrizeNumDTO;
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
     * 红球走势
     */
	 public BaseResult<List<LottoFirstDTO>> queryFirstData(){
		 List<LottoFirstDTO> lottoFirstDTOs = new ArrayList<>();
		 LottoFirstDTO lottoFirstDTO = new LottoFirstDTO();
		 
		 List<Lotto> lottos = lottoMapper.queryHistoryPrizeData();
		 List<LottoPrizeNumDTO> lottoPrizeNumDTOs = new ArrayList<>();
		 if(lottos.size() >= 0) {
			 lottos.forEach(l->{
				 LottoPrizeNumDTO lottoPrizeNumDTO = new LottoPrizeNumDTO();
				 lottoPrizeNumDTO.setTermNum(l.getTerm_num()+"期");
				 lottoPrizeNumDTO.setNumList((Arrays.asList(l.getPrize_num().split(","))));
				 lottoPrizeNumDTOs.add(lottoPrizeNumDTO);
			 });
		 }
		 LottoDrop lottoDrop = lottoDropMapper.queryLatelyDataByColor();
		 Lotto lotto = lottoMapper.queryLatelyPrizeData();
		 lottoFirstDTO.setTerm_num(lotto.getTerm_num()+1);
		 lottoFirstDTO.setEndDate(TermEndDateUtil.getChoseEndTime());
		 lottoFirstDTO.setPrizes(lotto.getPrizes());
		 lottoFirstDTO.setPrizeList(lottoPrizeNumDTOs);
		 lottoFirstDTO.setPreList(Arrays.asList(lottoDrop.getPre_drop().split(",")));
		 lottoFirstDTO.setPostList(Arrays.asList(lottoDrop.getPost_drop().split(",")));
		 
		 lottoFirstDTOs.add(lottoFirstDTO);
		 return ResultGenerator.genSuccessResult("success", lottoFirstDTOs);
	 }
	  
	 
}
