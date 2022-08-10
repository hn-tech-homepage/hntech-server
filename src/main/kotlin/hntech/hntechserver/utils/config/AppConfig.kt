package hntech.hntechserver.utils.config

import hntech.hntechserver.utils.LoggingInterceptor
import hntech.hntechserver.utils.auth.LoginCheckInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AppConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LoggingInterceptor())
            .order(1)
            .addPathPatterns("/**")

        registry.addInterceptor(LoginCheckInterceptor())
            .order(2)
            .addPathPatterns("/**")

    }

    // CORS 설정
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
    }

    // 정적 이미지 단순 조회
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/file/image/**")
            .addResourceLocations(IMAGE_PATH_FOR_WINDOW)
    }
}