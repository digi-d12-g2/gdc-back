package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;

import com.job.AbsencesStatusJob;
import com.job.PublicHolidaysJob;

@Configuration
public class Config {
    @Bean
    JobDetail absencesStatusJobDetails(){
        return JobBuilder.newJob(AbsencesStatusJob.class).withIdentity("AbsencesStatusJob").storeDurably().build();
    }

    @Bean
    public Trigger absencesStatusJobTrigger(@Autowired JobDetail absencesStatusJobDetails) {
        return TriggerBuilder.newTrigger()
          .forJob(absencesStatusJobDetails)
          .withIdentity("AbsencesStatusJob")
          .withSchedule(CronScheduleBuilder.cronSchedule("5 * * * * ?"))
          .build(); //une fois par jour...
    }

    @Bean
    JobDetail publicHolidaysJobDetails(){
        return JobBuilder.newJob(PublicHolidaysJob.class).withIdentity("PublicHolidaysJob").storeDurably().build();
    }

    @Bean
    public Trigger publicHolidaysJobTrigger(@Autowired JobDetail publicHolidaysJobDetails) {
        return TriggerBuilder.newTrigger()
          .forJob(publicHolidaysJobDetails)
          .withIdentity("PublicHolidaysJob")
          .withSchedule(CronScheduleBuilder.cronSchedule("1 * * 1 * ?"))
          .build(); //une fois par an...
    }
}
