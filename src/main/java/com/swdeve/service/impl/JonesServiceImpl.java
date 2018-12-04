package com.swdeve.service.impl;

import com.swdeve.annotation.SwdeveService;
import com.swdeve.service.JonesService;

@SwdeveService("jonesServiceTest")
public class JonesServiceImpl implements JonesService {

	public String query(String name, String age) {
		// TODO Auto-generated method stub
		return "name=" + name + ";age=" + age;
	}

}
