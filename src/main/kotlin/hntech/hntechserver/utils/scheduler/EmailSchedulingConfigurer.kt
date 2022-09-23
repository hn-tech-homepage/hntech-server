package hntech.hntechserver.utils.scheduler

import hntech.hntechserver.utils.logging.logger
import org.springframework.context.annotation.Lazy
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component

@Lazy(false)
@Component
@EnableScheduling
@EnableAsync
class EmailSchedulingConfigurer(private val emailManager: EmailManager): SchedulingConfigurer {

    private val log = logger()

    // cron 식 기준은 UTC (KST = UTC+9) 초기값 : KST 매일 낮 12시
    private var cron: String = "0 0 3 * * MON-FRI"
    
    fun setCron(koreaTime: String) {
        val utcTime = koreaTime.toInt()-9
        this.cron = "0 0 $utcTime * * MON-FRI"
        log.info("Updated cron: {}", this.cron)
    }

    // 스케쥴러 작업 등록 메소드
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.addTriggerTask(
            {
                // cron 시간마다 실행할 작업
                emailManager.sendMail()
            },
            {
                // 위에 정의한 작업의 실행 트리거 설정
                CronTrigger(cron).nextExecutionTime(it);
            }
        )
    }
}