package hntech.hntechserver.admin

import Auth
import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.utils.exception.ValidationException
import hntech.hntechserver.utils.logger
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/admin")
class AdminController(private val adminService: AdminService) {
    val log = logger()

    /**
     * 사용자 모드
     */
    // 인사말 조회
    @GetMapping("/introduce")
    fun getIntroduce() = IntroduceDto(adminService.getAdmin().introduce)

    // 하단 (footer) 조회
    @GetMapping("/footer")
    fun getFooter() = FooterDto(adminService.getAdmin())

    /**
     * 관리자 모드
     */
    // 관리자 로그인
    @PostMapping("/login")
    fun login(
        @RequestBody form: LoginForm,
        request: HttpServletRequest
    ) = BoolResponse(adminService.login(form.password, request))

    // 관리자 로그아웃
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest) =
        BoolResponse(adminService.logout(request))

    // 비밀번호 변경
    @Auth @PostMapping("/password")
    fun updatePassword(
        @Valid @RequestBody form: PasswordRequest,
        bindingResult: BindingResult
    ): PasswordResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return PasswordResponse(adminService.updatePassword(form))
    }

    // 인사말 수정
    @Auth @PostMapping("/introduce")
    fun updateIntroduce(@RequestBody form: IntroduceDto) =
        IntroduceDto(adminService.updateIntroduce(form.newIntroduce))

    // 조직도, CI, 연혁 수정
    @Auth @PostMapping("/image")
    fun updateOrgChart(@ModelAttribute form: AdminImageRequest) =
        AdminImageResponse(
            where = form.where,
            updatedServerFilename = when(form.where) {
            ORG_CHART -> adminService.updateOrgChart(form.file)
            CI -> adminService.updateCI(form.file)
            else -> adminService.updateCompanyHistory(form.file) // history
        })

    // 메일 설정
    @Auth @PostMapping("/mail")
    fun updateMail(@Valid @RequestBody form: EmailRequest,
                   bindingResult: BindingResult
    ) {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        adminService.updateMail(form)
    }

    // 하단 (footer) 수정
    @Auth @PostMapping("/footer")
    fun updateFooter(@RequestBody form: FooterDto) = FooterDto(adminService.updateFooter(form))
}