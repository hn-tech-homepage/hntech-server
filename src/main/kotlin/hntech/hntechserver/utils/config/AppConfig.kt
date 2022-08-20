package hntech.hntechserver.utils.config

import com.querydsl.jpa.impl.JPAQueryFactory
import hntech.hntechserver.utils.logging.LoggingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class AppConfig(private val loggingInterceptor: LoggingInterceptor) : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loggingInterceptor)
            .order(1)
            .addPathPatterns("/**")

//        registry.addInterceptor(LoginCheckInterceptor())
//            .order(2)
//            .addPathPatterns("/**")
//            .excludePathPatterns(
//                "/api/**", "/swagger-ui.html", "/webjars/**", "/v2/**",
//                "/swagger-resources/**", "/swagger**/**"
//            )
    }

    // CORS 설정
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods(
                HttpMethod.HEAD.name,
                HttpMethod.GET.name,
                HttpMethod.POST.name,
                HttpMethod.PUT.name,
                HttpMethod.PATCH.name,
                HttpMethod.DELETE.name
            )
    }

    // 정적 이미지 단순 조회
//    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
//        registry.addResourceHandler("/file/image/**")
//            .addResourceLocations("file://" + FILE_SAVE_PATH_LINUX)
//    }
}

// JPAQueryFactory 빈 등록
@Configuration
class QuerydslConfig(@PersistenceContext private val em: EntityManager) {
    @Bean fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(this.em)
}