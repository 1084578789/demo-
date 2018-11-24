package com.yc.bean;

import java.sql.Timestamp;

public class UserLoginRecord implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long userId;
	private Timestamp time;
	private String loginIp;
}
