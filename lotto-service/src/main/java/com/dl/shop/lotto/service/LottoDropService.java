package com.dl.shop.lotto.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.dl.lotto.param.TermNumParam;
import com.dl.shop.lotto.dao2.LottoDropMapper;
import com.dl.shop.lotto.model.LottoDrop;
import tk.mybatis.mapper.entity.Condition;

@Service
@Transactional(value="transactionManager2")
public class LottoDropService {
	@Resource
    private LottoDropMapper lottoDropMapper;
	
	/**
     * 红球走势
     */
	 public BaseResult<List<LottoHeatColdDTO>> queryHeatColdByColor(){
		 //查询遗漏
		 LottoDrop lottoDrop = lottoDropMapper.queryLatelyDataByColor();
		 List<String> preList = new ArrayList<>();
		 preList = Arrays.asList(lottoDrop.getPre_drop().split(","));
		 
		 TermNumParam termNumParam = new TermNumParam();
		 termNumParam.setCount(100);
		 List<LottoDrop> lottoDrops = lottoDropMapper.queryChartDataByColor(termNumParam);
		 List<LottoHeatColdDTO> lottoDTOs = new ArrayList<>();
		 if(lottoDrops.size() >= 0) {
			 Map<Integer,List<String>> mapA = new HashMap<>();
			 Map<Integer,List<String>> mapB = new HashMap<>();
			 Map<Integer,List<String>> mapC = new HashMap<>();
			 //最近30期数据转List
			 for (int i = 0; i < 30; i++) {
				 LottoDrop drop = lottoDrops.get(i);
				 String preDrop = drop.getPre_drop();
				 List<String> list = Arrays.asList(preDrop.split(","));
				 for (int j = 1; j <= list.size(); j++) {
					 if(mapA.get(j)==null) {
						 List<String> l1 = new ArrayList<>();
						 l1.add(list.get(j-1));
						 mapA.put(j, l1);
					 }else {
						 List<String> l1 = mapA.get(j);
						 l1.add(list.get(j-1));
						 mapA.put(j, l1);
					 }
				}
			 }
			//最近50期数据转List
			 for (int i = 0; i < 50; i++) {
				 LottoDrop drop = lottoDrops.get(i);
				 String preDrop = drop.getPre_drop();
				 List<String> list = Arrays.asList(preDrop.split(","));
				 for (int j = 1; j <= list.size(); j++) {
					 if(mapB.get(j)==null) {
						 List<String> l1 = new ArrayList<>();
						 l1.add(list.get(j-1));
						 mapB.put(j, l1);
					 }else {
						 List<String> l1 = mapB.get(j);
						 l1.add(list.get(j-1));
						 mapB.put(j, l1);
					 }
				}
			 }
			//最近100期数据转List
			 for (int i = 0; i < 100; i++) {
				 LottoDrop drop = lottoDrops.get(i);
				 String preDrop = drop.getPre_drop();
				 List<String> list = Arrays.asList(preDrop.split(","));
				 for (int j = 1; j <= list.size(); j++) {
					 if(mapC.get(j)==null) {
						 List<String> l1 = new ArrayList<>();
						 l1.add(list.get(j-1));
						 mapC.put(j, l1);
					 }else {
						 List<String> l1 = mapC.get(j);
						 l1.add(list.get(j-1));
						 mapC.put(j, l1);
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
				 lottoHeatColdDTO.setDrop(preList.get(i-1));
				 lottoHeatColdDTO.setCountA(sA.count()+"");
				 lottoHeatColdDTO.setCountB(sB.count()+"");
				 lottoHeatColdDTO.setCountC(sC.count()+"");
				 lottoDTOs.add(lottoHeatColdDTO);
			 }
		 }
		 
		 return ResultGenerator.genSuccessResult("success", lottoDTOs);
	 }
	  
	/**
     * 红球走势
     */
	 public BaseResult<List<LottoDropDTO>> queryChartDataByColor(TermNumParam termNumParam){
		 List<LottoDrop> lottoDrops = lottoDropMapper.queryChartDataByColor(termNumParam);
		 
		 List<LottoDropDTO> lottoDTOs = new ArrayList<>();
		 if(lottoDrops.size() >= 0) {
			 Map<Integer,List<String>> map = new HashMap<>();
			 lottoDrops.forEach(l->{
				 LottoDropDTO dropDTO = new LottoDropDTO();
				 String preDrop = l.getPre_drop();
				 String postDrop = l.getPost_drop();
				 dropDTO.setTerm_num(l.getTerm_num());
				 List<String> list = new ArrayList<>();
				 if(termNumParam.getColor()==0) {//红
					 list = Arrays.asList(preDrop.split(","));
				 }
				 if(termNumParam.getColor()==1) {//蓝
					 list = Arrays.asList(postDrop.split(","));
				 }
				 dropDTO.setList(list);
				 lottoDTOs.add(dropDTO);
				 
				 //将每个号码的往期遗漏组成list
				 for (int i = 1; i <= list.size(); i++) {
					 if(map.get(i)==null) {
						 List<String> l1 = new ArrayList<>();
						 l1.add(list.get(i-1));
						 map.put(i, l1);
					 }else {
						 List<String> l1 = map.get(i);
						 l1.add(list.get(i-1));
						 map.put(i, l1);
					 }
				}
			 });
			  getList(lottoDTOs,map);
			 
		 }
		 
		 return ResultGenerator.genSuccessResult("success", lottoDTOs);
	 }
	  
	 /**
	  * 计算每个号码的平均，最大，次数，连出
	  */
	 
	 private List<LottoDropDTO>  getList(List<LottoDropDTO>  lottoDTOs,Map<Integer,List<String>> map) {
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
		 
		 LottoDropDTO dropDTO1 = new LottoDropDTO();
		 dropDTO1.setTerm_num(1);
		 dropDTO1.setList(averageList);
		 lottoDTOs.add(dropDTO1);
		 LottoDropDTO dropDTO2 = new LottoDropDTO();
		 dropDTO2.setTerm_num(2);
		 dropDTO2.setList(maxList);
		 lottoDTOs.add(dropDTO2);
		 LottoDropDTO dropDTO3 = new LottoDropDTO();
		 dropDTO3.setTerm_num(3);
		 dropDTO3.setList(countList);
		 lottoDTOs.add(dropDTO3);
		 LottoDropDTO dropDTO4 = new LottoDropDTO();
		 dropDTO4.setTerm_num(4);
		 dropDTO4.setList(continuityList);
		 lottoDTOs.add(dropDTO4);
		 
		 return lottoDTOs;
	 }
}
