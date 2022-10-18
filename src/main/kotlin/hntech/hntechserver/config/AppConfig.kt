package hntech.hntechserver.config

import com.querydsl.jpa.impl.JPAQueryFactory
import hntech.hntechserver.common.CACHING_MINUTES
import hntech.hntechserver.utils.logging.JsonLoggingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.concurrent.TimeUnit
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class AppConfig(private val loggingInterceptor: JsonLoggingInterceptor) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loggingInterceptor)
            .order(1)
            .addPathPatterns("/**")
    }

    // CORS 설정
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
//            .allowedOrigins("*") // vite 쓰는 리액트에서 오리진 요청을 열어줘야함 (포트 주의)
            .allowedMethods("*") // http 모든 메소드 요청 허용
            .allowedHeaders("*") // 헤더 정보 모두 허용
//            .allowCredentials(true) // 쿠키, 세션 정보도 허용
    }

    // 정적 파일 단순 조회 (윈도우는 file: 만 리눅스는 file:// 까지 붙혀야함)
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // 파일
        registry.addResourceHandler("/files/**")
            .addResourceLocations("file:$FILE_SAVE_PATH")
            .setCacheControl(CacheControl.maxAge(CACHING_MINUTES, TimeUnit.MINUTES))

        // 관리자
        registry.addResourceHandler("/files/admin/**")
            .addResourceLocations("file:$ADMIN_SAVE_PATH")
            .setCacheControl(CacheControl.maxAge(CACHING_MINUTES, TimeUnit.MINUTES))

        // 카테고리
        registry.addResourceHandler("/files/category/**")
            .addResourceLocations("file:$CATEGORY_SAVE_PATH")
            .setCacheControl(CacheControl.maxAge(CACHING_MINUTES, TimeUnit.MINUTES))

        // 제품
        registry.addResourceHandler("/files/product/**")
            .addResourceLocations("file:$PRODUCT_SAVE_PATH")
            .setCacheControl(CacheControl.maxAge(CACHING_MINUTES, TimeUnit.MINUTES))

        // 자료실
        registry.addResourceHandler("/files/archive/**")
            .addResourceLocations("file:$ARCHIVE_SAVE_PATH")
            .setCacheControl(CacheControl.maxAge(CACHING_MINUTES, TimeUnit.MINUTES))

        // 문의사항
        registry.addResourceHandler("/files/question/**")
            .addResourceLocations("file:$QUESTION_SAVE_PATH")
            .setCacheControl(CacheControl.maxAge(CACHING_MINUTES, TimeUnit.MINUTES))
    }
}

// JPAQueryFactory 빈 등록
@Configuration
class QuerydslConfig(@PersistenceContext private val em: EntityManager) {
    @Bean
    fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(this.em)
}