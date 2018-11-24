package com.yc.dao;

public class Test {
	public static void main(String[] args) {
		DBHelper db = new DBHelper();
		System.out.println(db.find("select * from columns", null));
	}
}
