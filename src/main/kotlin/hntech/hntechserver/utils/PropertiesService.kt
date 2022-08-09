package hntech.hntechserver.utils

import org.apache.commons.configuration2.Configuration
import org.apache.commons.configuration2.YAMLConfiguration
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder
import org.apache.commons.configuration2.builder.fluent.Parameters
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

/**
 * config yml 파일을 모니터링 하기 위한 컴포넌트
 * 해당 빈을 주입받으면 getConfiguration 메소드를 통해 지정한 설정 파일에 접근 가능
 */
@Component
class PropertiesService {
    val log = logger()

    lateinit var builder: ReloadingFileBasedConfigurationBuilder<YAMLConfiguration>

    @PostConstruct
    fun init() {
        /**
         * configuration2 에서 제공하는 reload 전용 클래스
         * ReloadingFileBasedConfigurationBuilder.configure 로 설정 파일 지정
         * 파일에 따라 제네릭에 PropertiesConfiguration, XMLConfiguration 클래스 등록
         */
        builder =
            ReloadingFileBasedConfigurationBuilder(YAMLConfiguration::class.java)
                .configure(Parameters().fileBased().setFile(
                    // yaml config 파일 경로
                    File("src/main/resources/application-mail.yml"))
                )

        /**
         * CONFIGURATION_REQUEST
         * builder.getConfiguration 호출을 감지하는 이벤트 리스너
         * onEvent 메소드를 오버라이딩하여 이벤트 발생 시 실행할 로직 작성
         */
//        builder.addEventListener(ConfigurationBuilderEvent.CONFIGURATION_REQUEST) {
//            @Override
//            fun onEvent(event: Event) {
//                // 실행할 로직
//            }
//        }

        // Reload 기간을 설정하는 트리거
        // TimeUnit 을 통해 reload 감지 시간을 설정 가능
        val configReloadTrigger = PeriodicReloadingTrigger(
            builder.reloadingController, null, 1, TimeUnit.SECONDS
        )

        configReloadTrigger.start()
    }

    fun getConfiguration(): Configuration? {
        try {
            // configuration 정보 반환
            return builder.configuration
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}