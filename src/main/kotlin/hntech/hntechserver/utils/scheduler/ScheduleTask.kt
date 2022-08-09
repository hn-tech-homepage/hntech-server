package hntech.hntechserver.utils.scheduler

import hntech.hntechserver.utils.logger
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cron")
class SchedulerController(private val scheduleTask: ScheduleTask) {

    @GetMapping("/mail")
    fun setMailCron(@RequestParam("time") time: String) =
        scheduleTask.setTime(time)
}

@Lazy(false)
@Component
class ScheduleTask(private val mailService: MailService): SchedulingConfigurer {

    private val log = logger()

    private var cron: String = "0/5 * * * * ?"

    fun setTime(time: String) { this.cron = "0/$time * * * * ?" }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.addTriggerTask(
            {
                log.info("현재 cron : {}", this.cron)
                mailService.testLog()
            },
            {
                CronTrigger(cron).nextExecutionTime(it);
            }
        );
    }
}