package com.dl.shop.lotto.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dl.base.result.BaseResult;
import com.dl.base.result.ResultGenerator;
import com.dl.lotto.dto.LottoDropDTO;
import com.dl.lotto.dto.LottoHeatColdDTO;
import com.dl.lotto.dto.LottoNumDTO;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.lotto.param.HeatColdParam;
import com.dl.shop.lotto.dao2.LottoDropMapper;
import com.dl.shop.lotto.model.LottoDrop;

@Service
@Transactional(value="transactionManager2")
public class LottoDropService {
	@Resource
    private LottoDropMapper lottoDropMapper;
	
	/**
     * 红球走势
     */
	 public BaseResult<List<LottoHeatColdDTO>> queryHeatColdByColor(HeatColdParam heatColdParam){
		 //查询遗漏
		 List<LottoDrop> lottoDrop = lottoDropMapper.getLastNumLottoDrops(1);
		 List<String> preList = new ArrayList<>();
		 preList = Arrays.asList(lottoDrop.get(0).getPreDrop().split(","));
		 List<LottoDrop> lottoDrops =null;// lottoDropMapper.queryHeatColdByColor(heatColdParam);
		 List<LottoHeatColdDTO> lottoDTOs = new ArrayList<>();
		 if(lottoDrops.size() >= 0) {
			 Map<Integer,List<String>> mapA = new HashMap<>();
			 Map<Integer,List<String>> mapB = new HashMap<>();
			 Map<Integer,List<String>> mapC = new HashMap<>();
			//最近30，50，100期数据转List
			 for (int i = 0; i < lottoDrops.size(); i++) {
				 LottoDrop drop = lottoDrops.get(i);
				 String preDrop = drop.getPreDrop();
				 String postDrop = drop.getPostDrop();
				 List<String> list = new ArrayList<>();
				 if(heatColdParam.getColor()==0) {//红
					 list = Arrays.asList(preDrop.split(","));
				 }
				 if(heatColdParam.getColor()==1) {//蓝
					 list = Arrays.asList(postDrop.split(","));
				 }
				
				 for (int j = 1; j <= list.size(); j++) {
					if (i < 30) {
						if (mapA.get(j) == null) {
							List<String> l1 = new ArrayList<>();
							l1.add(list.get(j - 1));
							mapA.put(j, l1);
						} else {
							List<String> l1 = mapA.get(j);
							l1.add(list.get(j - 1));
							mapA.put(j, l1);
						}
					}
					if (i < 50) {
						if (mapB.get(j) == null) {
							List<String> l2 = new ArrayList<>();
							l2.add(list.get(j - 1));
							mapB.put(j, l2);
						} else {
							List<String> l2 = mapB.get(j);
							l2.add(list.get(j - 1));
							mapB.put(j, l2);
						}
					}
					if (i < 100) {
						if (mapC.get(j) == null) {
							List<String> l3 = new ArrayList<>();
							l3.add(list.get(j - 1));
							mapC.put(j, l3);
						} else {
							List<String> l3 = mapC.get(j);
							l3.add(list.get(j - 1));
							mapC.put(j, l3);
						}
					}
				}
			 }
			 
			 for (int i = 1; i < mapA.size()+1; i++) {
				 LottoHeatColdDTO lottoHeatColdDTO = new LottoHeatColdDTO();
				 List<String> listA = mapA.get(i);
				 List<String> listB = mapB.get(i);
				 List<String> listC = mapC.get(i);
				 //出现次数
				 Stream<String> sA = listA.stream().filter(l->{return l.equals("0");});
				 Stream<String> sB = listB.stream().filter(l->{return l.equals("0");});
				 Stream<String> sC = listC.stream().filter(l->{return l.equals("0");});
				 lottoHeatColdDTO.setNum(i);
//				 lottoHeatColdDTO.setDrop(preList.get(i-1));
//				 lottoHeatColdDTO.setCountA(sA.count()+"");
//				 lottoHeatColdDTO.setCountB(sB.count()+"");
//				 lottoHeatColdDTO.setCountC(sC.count()+"");
				 lottoDTOs.add(lottoHeatColdDTO);
			 }
			 if(heatColdParam.getOrder() == 1) {
				 Collections.reverse(lottoDTOs);//顺序反转
			 }
		 }
		 
		 return ResultGenerator.genSuccessResult("success", lottoDTOs);
	 }
	  
	/**
     * 红球走势
     */
	 public BaseResult<LottoDropDTO> queryChartDataByColor(ChartSetupParam setupParam){
		 LottoDropDTO lottoDropDTO = new LottoDropDTO();
//		 List<LottoDrop> lottoDrops = null;//lottoDropMapper.queryChartDataByColor(setupParam);
//		 List<LottoNumDTO> drops = new ArrayList<>();
//		 if(lottoDrops.size() >= 0) {
//			 Map<Integer,List<String>> map = new HashMap<>();
//			 lottoDrops.forEach(l->{
//				 LottoNumDTO drop = new LottoNumDTO();
//				 String preDrop = l.getPreDrop();
//				 String postDrop = l.getPostDrop();
//				 drop.setTermNum(l.getTermNum()+"");
//				 List<String> list = new ArrayList<>();
//				 List<String> list2 = new ArrayList<>();
//				 if(setupParam.getColor()==0) {//红
//					 list = Arrays.asList(preDrop.split(","));
//					 list2 = Arrays.asList(preDrop.split(","));
//				 }
//				 if(setupParam.getColor()==1) {//蓝
//					 list = Arrays.asList(postDrop.split(","));
//					 list2 = Arrays.asList(postDrop.split(","));
//				 }
//				 if(setupParam.getDrop() == 1) {
//					 for(int j = 0;j<list2.size();j++){
//						 if(!list2.get(j).equals("0")) {
//							 list2.set(j, "");
//						 }
//					 }
//				 }
//				 drop.setNumList(list2);
//				 drops.add(drop);
//				 
//				 //将每个号码的往期遗漏组成list
//				 for (int i = 1; i <= list.size(); i++) {
//					 if(map.get(i)==null) {
//						 List<String> l1 = new ArrayList<>();
//						 l1.add(list.get(i-1));
//						 map.put(i, l1);
//					 }else {
//						 List<String> l1 = map.get(i);
//						 l1.add(list.get(i-1));
//						 map.put(i, l1);
//					 }
//				}
//			 });
//			 if(setupParam.getOrderBY() == 0) {
//				 Collections.reverse(drops);//顺序反转
//			 }
//			 lottoDropDTO.setDrop(drops);
//			 //显示计算统计
//			 if(setupParam.getCompute() == 0) {
//				 lottoDropDTO.setCompute(getList(map));
//			 }
//			 
//		 }
		 
		 return ResultGenerator.genSuccessResult("success", lottoDropDTO);
	 }
	  
	 /**
	  * 计算每个号码的平均，最大，次数，连出
	  */
	 
	 private Map<String,List<String>>  getList(Map<Integer,List<String>> map) {
		 Map<String,List<String>> computeMap = new HashMap<>();
		 List<String> averageList = new ArrayList<>();
		 List<String> maxList = new ArrayList<>();
		 List<String> countList = new ArrayList<>();
		 List<String> continuityList =  new ArrayList<>();
		 for (int i = 1; i < map.size()+1; i++) {
			 List<String> list = map.get(i);
			 //遗漏总和
			 Integer sum = list.stream().collect(Collectors.summingInt(s-> Integer.parseInt(s)));
			 averageList.add(sum/list.size()+"");
			 //遗漏最大
			 Optional<String> max = list.stream().collect(Collectors.maxBy(Comparator.comparing(s-> Integer.parseInt(s))));
			 maxList.add(max.get());
			 //出现次数
			 Stream<String> lc = list.stream().filter(l->{return l.equals("0");});
			 countList.add(lc.count()+"");
			 //最大连出
			 int ctt = 0;
			 int ct = 0;
			 for(String s : list) {
				 if(s.equals("0")) {
					 ct = ct +1;
					 if(ct>ctt) {
						 ctt = ct;
					 }
				 }else {
					 ct = 0;
				 }
			 }
			 continuityList.add(ctt+"");
		 }
		 
		 computeMap.put("average", averageList);
		 computeMap.put("max", maxList);
		 computeMap.put("count", countList);
		 computeMap.put("continuity", continuityList);
		 
		 return computeMap;
	 }
}
