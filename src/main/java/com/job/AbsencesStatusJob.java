package com.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.service.AbsenceService;

@Component
public class AbsencesStatusJob implements Job {

    @Autowired
    private AbsenceService absenceService;

    /**
     * @param arg0
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        this.absenceService.modifyAbsencesStatus();
        this.absenceService.modifyEmployerRttStatus();
    }

}
