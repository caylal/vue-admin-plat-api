package com.avenir.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {
	/**
	 * 错误输入日志
	 */
	public static final Logger log = LoggerFactory.getLogger(LogUtils.class);

	/**
	 * 记录一直 info信息
	 * 
	 * @param message
	 */
	public static void info(String message) {
		StringBuilder s = new StringBuilder();
		s.append((message));
		log.info(s.toString());
	}

	public static void info(String message, Throwable e) {
		StringBuilder s = new StringBuilder();
		s.append(("exception : -->>"));
		s.append((message));
		log.info(s.toString(), e);
	}

	public static void warn(String message) {
		StringBuilder s = new StringBuilder();
		s.append((message));

		log.warn(s.toString());
	}

	public static void warn(String message, Throwable e) {
		StringBuilder s = new StringBuilder();
		s.append(("exception : -->>"));
		s.append((message));
		log.warn(s.toString(), e);
	}

	public static void debug(String message) {
		StringBuilder s = new StringBuilder();
		s.append((message));
		log.debug(s.toString());
	}

	public static void debug(String message, Throwable e) {
		StringBuilder s = new StringBuilder();
		s.append(("exception : -->>"));
		s.append((message));
		log.debug(s.toString(), e);
	}

	public static void error(String message) {
		StringBuilder s = new StringBuilder();
		s.append(message);
		log.error(s.toString());
	}

	/**
	 * 记录日志错误信息
	 * 
	 * @param message
	 * @param e
	 */
	public static void error(String message, Throwable e) {
		StringBuilder s = new StringBuilder();
		s.append(("exception : -->>"));
		s.append((message));
		log.error(s.toString(), e);
	}
}
