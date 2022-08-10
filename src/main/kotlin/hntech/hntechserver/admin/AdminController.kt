package hntech.hntechserver.admin

import hntech.hntechserver.utils.error.ValidationException
import hntech.hntechserver.utils.logger
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/admin")
class AdminController(private val adminService: AdminService) {
    val log = logger()

    // 비밀번호 변경
    @PostMapping("/password")
    fun updatePassword(
        @Valid @RequestBody form: PasswordRequest,
        bindingResult: BindingResult
    ): PasswordResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return PasswordResponse(adminService.updatePassword(form))
    }

    // 인사말 조회
    @GetMapping("/introduce")
    fun getIntroduce() = IntroduceDto(adminService.getAdmin().introduce)

    // 인사말 수정
    @PostMapping("/introduce")
    fun updateIntroduce(@RequestBody form: IntroduceDto) =
        IntroduceDto(adminService.updateIntroduce(form.newIntroduce))

    // 조직도, CI, 연혁 수정
    @PostMapping("/image")
    fun updateOrgChart(@ModelAttribute form: AdminImageRequest) =
        AdminImageResponse(
            where = form.where,
            updatedServerFilename = when(form.where) {
            ORG_CHART -> adminService.updateOrgChart(form.file)
            CI -> adminService.updateCI(form.file)
            else -> adminService.updateCompanyHistory(form.file) // history
        })

    // 메일 설정
    @PostMapping("/mail")
    fun updateMail(@Valid @RequestBody form: EmailRequest,
                   bindingResult: BindingResult
    ) {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        adminService.updateMail(form)
    }

    // 하단 (footer) 조회
    @GetMapping("/footer")
    fun getFooter() = FooterDto(adminService.getAdmin())

    // 하단 (footer) 수정
    @PostMapping("/footer")
    fun updateFooter(@RequestBody form: FooterDto) = FooterDto(adminService.updateFooter(form))
}