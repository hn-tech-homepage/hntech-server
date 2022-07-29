package hntech.hntechserver.utils.config

import hntech.hntechserver.utils.LoggingInterceptor
import hntech.hntechserver.utils.LoginCheckInterceptor
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
            .addPathPatterns("/")

    }


    // CORS 설정
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
    }

    // 리소스 리다이렉션 (서버 내부에 있는 이미지 내려주기)
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/img/**")
            .addResourceLocations("리눅스에 파일 저장된 경로를 넣어줘야함")
    }
}