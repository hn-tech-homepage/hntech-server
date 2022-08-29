package hntech.hntechserver.domain.admin

import hntech.hntechserver.auth.Auth
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.exception.ValidationException
import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.utils.logging.logger
import io.swagger.annotations.ApiOperation
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService,
    private val fileService: FileService
) {
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
    ): BoolResponse {
        log.info("admin login")
        return BoolResponse(adminService.login(form.password, request))
    }

    // 관리자 로그아웃
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): BoolResponse {
        log.info("admin logout")
        return BoolResponse(adminService.logout(request))
    }

    // 홈페이지 모든 이미지 조회
    @GetMapping("/images")
    fun getAllImages(): AdminImagesResponse {
        val admin = adminService.getAdmin()
        return AdminImagesResponse(admin, fileService.getFile(admin.logoImage))
    }

    // 인사말 수정
    @Auth
    @PutMapping("/introduce")
    fun updateIntroduce(@RequestBody form: IntroduceDto) =
        IntroduceDto(adminService.updateIntroduce(form.newIntroduce))

    // 조직도, CI, 연혁 등록, 수정
    @ApiOperation(
        value = "로고, 조직도, CI, 연혁 등록/수정",
        notes = "세 개를 범용으로 수정함. where로 어느 부분인지 명시. 로고 : logo 조직도 : orgChart, CI : ci, 연혁 : companyHistory")
    @Auth
    @PostMapping("/image")
    fun updateOthers(@ModelAttribute form: AdminImageRequest): AdminImagesResponse {
        val admin = adminService.getAdmin()
        if (form.file.isEmpty) return AdminImagesResponse(admin, fileService.getFile(admin.logoImage))
        return AdminImagesResponse(
            when (form.where) {
                LOGO -> adminService.updateLogo(form.file)
                ORG_CHART -> adminService.updateOrgChart(form.file)
                CI -> adminService.updateCI(form.file)
                else -> adminService.updateCompanyHistory(form.file) // history
            }, fileService.getFile(admin.logoImage)
        )
    }

    // 배너 등록, 수정
    @Auth
    @PostMapping("/banner")
    fun updateBanners(@ModelAttribute form: AdminImagesRequest): AdminImagesResponse {
        val admin = adminService.updateImages(form)
        return AdminImagesResponse(admin, fileService.getFile(admin.logoImage))
    }

    // 관리자 패널 정보 조회
    @Auth
    @GetMapping("/panel")
    fun getPanelInfo() = AdminPanelResponse(adminService.getAdmin())

    // 관리자 비밀번호 변경
    @Auth
    @PutMapping("/password")
    fun updatePassword(@RequestBody form: UpdatePasswordForm) =
        PasswordResponse(adminService.updatePassword(form))

    // 관리자 패널 정보 수정
    @PutMapping("/panel")
    fun updatePanelInfo(
        @Valid @RequestBody form: UpdateAdminPanelForm,
        br: BindingResult
    ): AdminPanelResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return AdminPanelResponse(adminService.updatePanel(form))
    }
}