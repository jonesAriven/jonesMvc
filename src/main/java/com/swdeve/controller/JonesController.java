package com.swdeve.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swdeve.annotation.SwdeveAutowired;
import com.swdeve.annotation.SwdeveController;
import com.swdeve.annotation.SwdeveRequestParam;
import com.swdeve.annotation.SwdeveRequestMapping;
import com.swdeve.service.JonesService;

@SwdeveController
@SwdeveRequestMapping("/JonesControllerTest")
public class JonesController {

	@SwdeveAutowired("jonesServiceTest")
	private JonesService jonesService;

	@SwdeveRequestMapping("/query")
	public void query(HttpServletRequest req, HttpServletResponse rep, @SwdeveRequestParam("name") String name,
			@SwdeveRequestParam("age") String age) throws IOException {
		// System.out.println("=====name=" + name + "~age=" + age);
		String resut = jonesService.query(name, age);
		// System.out.println("resut=" + resut);
		try {
			// PrintWriter write = new PrintWriter(resut);
			PrintWriter write = rep.getWriter();
			write.write(resut);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
