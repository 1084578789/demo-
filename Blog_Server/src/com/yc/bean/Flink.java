package com.yc.bean;

import java.sql.Timestamp;

public class Flink implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String webUrl;
	private String linkImg;
	private String description;
	private String status;
	private String openWays;
	private Long sort;
	private String createBy;
	private Timestamp createDate;
}
