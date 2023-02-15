package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.service.PublicHolidayService;

@SpringBootApplication
public class GdcApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(GdcApplication.class, args);
		PublicHolidayService publicHolidayService = ctx.getBean(PublicHolidayService.class);
		publicHolidayService.addPublicHolidays();
	}
}