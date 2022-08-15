package hntech.hntechserver.utils.config

import hntech.hntechserver.utils.LoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AppConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(LoggingInterceptor())
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