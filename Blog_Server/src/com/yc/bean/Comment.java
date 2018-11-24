package com.yc.bean;

import java.sql.Timestamp;

public class Comment implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long titleId;
	private Long userId;
	private String content;
	private Timestamp commentTime;
}
