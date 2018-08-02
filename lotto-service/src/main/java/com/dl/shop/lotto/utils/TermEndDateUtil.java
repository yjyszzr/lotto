package com.dl.shop.lotto.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TermEndDateUtil {
	private static LocalTime TIME_PRIZE = LocalTime.of(20, 30, 0);
	private static LocalTime TIME_CHOSE = LocalTime.of(19, 30, 0);
	public static final DateTimeFormatter ymd_sdf = DateTimeFormatter.ofPattern("MM-dd HH:mm");

//	public static void main(String[] args) {
//		System.out.println(getTermEndTime());
//		System.out.println(getChoseEndTime());
//	}
	/**
	 * 
	 * 返回指定格式投注截止时间
	 * @return
	 */
	public static String getChoseEndTime() {
		LocalDateTime l = getLocalDateTime(TIME_CHOSE);
		return l.format(ymd_sdf);
	}
	
	/**
	 * 返回指定格式开奖时间
	 * @return
	 */
	public static String getTermEndTime() {
		LocalDateTime l = getLocalDateTime(TIME_PRIZE);
		return l.format(ymd_sdf);
	}
	
	/**
	 * 根据当前时间判断开奖时间
	 * @return
	 */
	private static LocalDateTime getLocalDateTime(LocalTime TIME) {
		LocalDateTime nowTime = LocalDateTime.now();
		LocalDate l = nowTime.toLocalDate();
		int dayOfWeek = l.getDayOfWeek().getValue();
		LocalDate data = l.plusDays(1 - dayOfWeek);
		LocalDateTime endTime = LocalDateTime.of(data, TIME);
		if (endTime.isAfter(nowTime)) {
			return endTime;
		}else {
			data = l.plusDays(3 - dayOfWeek);
			endTime = LocalDateTime.of(data, TIME);
			if (endTime.isAfter(nowTime)) {
				return endTime;
			}else {
				data = l.plusDays(6 - dayOfWeek);
				endTime = LocalDateTime.of(data, TIME);
				if (endTime.isAfter(nowTime)) {
					return endTime;
				}
			}
		}
		return endTime;
	}

}
