package com.shopme.admin.user;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

public class AbstractExporter {

	public void setResponseHeader(HttpServletResponse response ,
			String contnenType,String extention) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = dateFormatter.format(new Date());
		String filename = "users_" + timestamp + extention;
		response.setContentType(contnenType);
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + filename;
		response.setHeader(headerKey, headerValue);
	}
}
