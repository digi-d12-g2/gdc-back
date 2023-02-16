package com.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.PublicHolidayService;

@Component
public class PublicHolidaysJob implements Job {

    @Autowired
    private PublicHolidayService publicHolidayService;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        this.publicHolidayService.apiAddPublicHolidays();
    }
}
