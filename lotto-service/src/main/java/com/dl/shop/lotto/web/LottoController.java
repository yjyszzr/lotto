package com.dl.shop.lotto.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dl.base.enums.MatchPlayTypeEnum;
import com.dl.base.model.UserDeviceInfo;
import com.dl.base.param.EmptyParam;
import com.dl.base.result.BaseResult;
import com.dl.base.result.ResultGenerator;
import com.dl.base.util.JSONHelper;
import com.dl.base.util.MD5Util;
import com.dl.base.util.SessionUtil;
import com.dl.lotto.dto.BetPayInfoDTO;
import com.dl.lotto.dto.DIZQUserBetLottoDTO;
import com.dl.lotto.dto.LottoBetInfoDTO;
import com.dl.lotto.dto.LottoChartDataDTO;
import com.dl.lotto.dto.LottoFirstDTO;
import com.dl.lotto.enums.LottoResultEnum;
import com.dl.lotto.param.ChartSetupParam;
import com.dl.lotto.param.SaveBetInfoParam;
import com.dl.member.api.IUserBonusService;
import com.dl.member.api.IUserService;
import com.dl.member.dto.UserBonusDTO;
import com.dl.member.dto.UserDTO;
import com.dl.member.param.BonusLimitConditionParam;
import com.dl.member.param.StrParam;
import com.dl.shop.lotto.core.ProjectConstant;
import com.dl.shop.lotto.service.LottoService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/lotto")
public class LottoController {

	@Resource
	private LottoService lottoFirstService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private IUserBonusService userBonusService;
	@Resource
	private IUserService userService;

	
	@ApiOperation(value = "选号投注页数据", notes = "选号投注页数据")
	@PostMapping("/getTicketInfo")
	public BaseResult<LottoFirstDTO> getTicketInfo(@RequestBody EmptyParam emprt) {
		LottoFirstDTO queryFirstData = lottoFirstService.queryFirstData();
		if(queryFirstData != null) {
			return ResultGenerator.genSuccessResult("", queryFirstData);
		}
		return ResultGenerator.genResult(LottoResultEnum.GET_TICKET_INFO_NULL.getCode(), LottoResultEnum.GET_TICKET_INFO_NULL.getMsg());
	}
	
	@ApiOperation(value = "走势图数据", notes = "走势图数据")
	@PostMapping("/getChartData")
	public BaseResult<LottoChartDataDTO> getChartData(@RequestBody ChartSetupParam param) {
		LottoChartDataDTO lottoChartData = lottoFirstService.getChartData(param);
		if(lottoChartData != null) {
			return ResultGenerator.genSuccessResult("", lottoChartData);
		}
		return ResultGenerator.genResult(LottoResultEnum.GET_CHART_DATA_NULL.getCode(), LottoResultEnum.GET_CHART_DATA_NULL.getMsg());
	}
	
	@ApiOperation(value = "投注确认", notes = "投注确认")
	@PostMapping("/saveBetInfo")
	public BaseResult<BetPayInfoDTO> saveBetInfo(@RequestBody SaveBetInfoParam param) {
		String orderMoneyStr = param.getOrderMoney();
		if(StringUtils.isBlank(orderMoneyStr)) {
			return ResultGenerator.genResult(LottoResultEnum.PARAM_ERROR.getCode(), LottoResultEnum.PARAM_ERROR.getMsg());
		}
		Double orderMoney = Double.valueOf(orderMoneyStr);
		List<LottoBetInfoDTO> betInfos = param.getBetInfos();
		if(CollectionUtils.isEmpty(betInfos)) {
			return ResultGenerator.genResult(LottoResultEnum.PARAM_ERROR.getCode(), LottoResultEnum.PARAM_ERROR.getMsg());
		}
		Integer ticketNum = betInfos.size();
		//用户信息
		StrParam strParam = new StrParam();
		BaseResult<UserDTO> userInfoExceptPassRst = userService.userInfoExceptPass(strParam);
		if(userInfoExceptPassRst.getCode() != 0 || null == userInfoExceptPassRst.getData()) {
			return ResultGenerator.genResult(LottoResultEnum.OPTION_ERROR.getCode(), LottoResultEnum.OPTION_ERROR.getMsg());
		}
		boolean isOk = lottoFirstService.checkBetInfo(param);
		if(!isOk) {
			return ResultGenerator.genResult(LottoResultEnum.BET_INFO_ERROR.getCode(), LottoResultEnum.BET_INFO_ERROR.getMsg());
		}
		Double maxBetAmount = 0.0;
		if(maxBetAmount >= 20000) {
			return ResultGenerator.genResult(LottoResultEnum.BET_MONEY_LIMIT.getCode(), LottoResultEnum.BET_MONEY_LIMIT.getMsg());
		}
		int betNum = param.getBetNum();
		if(betNum >= 10000 || betNum < 0) {
			return ResultGenerator.genResult(LottoResultEnum.BET_NUMBER_LIMIT.getCode(), LottoResultEnum.BET_NUMBER_LIMIT.getMsg());
		}
	/*	String betMoney = betInfo.getMoney();
		Double orderMoney = Double.valueOf(betMoney);
		Double minBetMoney = lotteryMatchService.getMinBetMoney();
		if(orderMoney < minBetMoney) {
			return ResultGenerator.genResult(LottoResultEnum.BET_MATCH_WC.getCode(), "最低投注"+minBetMoney.intValue()+"元!");
		}
		int canBetMoney = lotteryMatchService.canBetMoney();
		if(orderMoney > canBetMoney) {
			return ResultGenerator.genResult(LottoResultEnum.BET_MATCH_STOP.getCode(), LottoResultEnum.BET_MATCH_STOP.getMsg());
		}*/
		String totalMoney = userInfoExceptPassRst.getData().getTotalMoney();
		Double userTotalMoney = Double.valueOf(totalMoney);
		//红包包
		BonusLimitConditionParam bonusLimitConditionParam = new BonusLimitConditionParam();
		bonusLimitConditionParam.setOrderMoneyPaid(BigDecimal.valueOf(orderMoney));
		BaseResult<List<UserBonusDTO>> userBonusListRst = userBonusService.queryValidBonusList(bonusLimitConditionParam);
		if(userBonusListRst.getCode() != 0) {
			return ResultGenerator.genResult(LottoResultEnum.OPTION_ERROR.getCode(), LottoResultEnum.OPTION_ERROR.getMsg());
		}
		
		List<UserBonusDTO> userBonusList = userBonusListRst.getData();
		UserBonusDTO userBonusDto = null;
		if(!CollectionUtils.isEmpty(userBonusList)) {
			String bonusIdStr = param.getBonusId();
			if(StringUtils.isNotBlank(bonusIdStr) && Integer.valueOf(bonusIdStr) != 0) {//有红包id
				if(Integer.valueOf(bonusIdStr) != -1) {
					Optional<UserBonusDTO> findFirst = userBonusList.stream().filter(dto->dto.getUserBonusId().equals(Integer.valueOf(bonusIdStr))).findFirst();
					userBonusDto = findFirst.isPresent()?findFirst.get():null;
				}
			}else {//没有传红包id
				List<UserBonusDTO> userBonuses = userBonusList.stream().filter(dto->{
					double minGoodsAmount = dto.getBonusPrice().doubleValue();
					return orderMoney < minGoodsAmount ? false : true;
				}).sorted((n1,n2)->n2.getBonusPrice().compareTo(n1.getBonusPrice()))
						.collect(Collectors.toList());
				if(userBonuses.size() > 0) {
					userBonusDto = userBonuses.get(0);
				}
			}
		}
		String bonusId = userBonusDto != null?userBonusDto.getUserBonusId().toString():null;
		Double bonusAmount = userBonusDto!=null?userBonusDto.getBonusPrice().doubleValue():0.0;
		Double amountTemp = orderMoney - bonusAmount;//红包扣款后的金额
		Double surplus = 0.0;
		Double thirdPartyPaid = 0.0;
		if(amountTemp < 0) {//红包大于订单金额
			bonusAmount = orderMoney;
		}else {
			surplus = userTotalMoney>amountTemp?amountTemp:userTotalMoney;
			thirdPartyPaid = amountTemp - surplus;
		}
		
		//缓存订单支付信息
		DIZQUserBetLottoDTO dto = new DIZQUserBetLottoDTO(param);
		dto.setBetNum(betNum);
		dto.setMoney(orderMoney);
		dto.setTicketNum(ticketNum);
		dto.setBonusAmount(bonusAmount);
		dto.setBonusId(bonusId);
		dto.setSurplus(surplus);
//		String forecastMoney = betInfo.getMinBonus() + "~" + betInfo.getMaxBonus();
		dto.setThirdPartyPaid(thirdPartyPaid);
		String requestFrom = "0";
		UserDeviceInfo userDevice = SessionUtil.getUserDevice();
		if(userDevice != null) {
			requestFrom = userDevice.getPlat();
		}
		dto.setRequestFrom(requestFrom);
		dto.setUserId(SessionUtil.getUserId());
		String dtoJson = JSONHelper.bean2json(dto);
		String keyStr = "bet_lotto_info_" + SessionUtil.getUserId() +"_"+ System.currentTimeMillis();
		String key = "lotto_" + MD5Util.crypt(keyStr);
		stringRedisTemplate.opsForValue().set(key, dtoJson, ProjectConstant.BET_INFO_EXPIRE_TIME, TimeUnit.MINUTES);
		//返回页面信息
		BetPayInfoDTO betPlayInfoDTO = new BetPayInfoDTO();
		betPlayInfoDTO.setPayToken(key);
		betPlayInfoDTO.setBonusAmount(String.format("%.2f", bonusAmount));
		betPlayInfoDTO.setBonusId(bonusId);
		betPlayInfoDTO.setBonusList(userBonusList);
		betPlayInfoDTO.setOrderMoney(orderMoneyStr);
		betPlayInfoDTO.setSurplus(String.format("%.2f", surplus));
		betPlayInfoDTO.setThirdPartyPaid(String.format("%.2f", thirdPartyPaid));
		return ResultGenerator.genSuccessResult("success", betPlayInfoDTO);
	}
}
