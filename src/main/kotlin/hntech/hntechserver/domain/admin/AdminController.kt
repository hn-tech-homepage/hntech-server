package hntech.hntechserver.domain.admin

import hntech.hntechserver.auth.Auth
import hntech.hntechserver.common.BoolResponse
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping("/admin")
class AdminController(private val adminService: AdminService) {
    /**
     * 사용자 모드
     */
    // 인사말 조회
    @GetMapping("/introduce")
    fun getIntroduce(): IntroduceDto = adminService.getIntroduce()

    // 하단 (footer) 조회
    @GetMapping("/footer")
    fun getFooter(): FooterDto = adminService.getFooter()

    // 카다록, 제품승인서 서버 파일 이름 조회
    @GetMapping("/catalog-material")
    fun getCatalogMaterial(): CatalogMaterialResponse =
        adminService.getCatalogMaterial()

    /**
     * 관리자 모드
     */
    // 관리자 로그인
    @PostMapping("/login")
    fun login(@RequestBody form: LoginForm, request: HttpServletRequest): BoolResponse =
        adminService.login(form.password, request)

    // 관리자 로그아웃
    @GetMapping("/logout")
    fun logout(request: HttpServletRequest): BoolResponse = adminService.logout(request)

    // 회사소개 모든 사진 조회
    @GetMapping("/images")
    fun getAllImages(): AdminImagesResponse =
        adminService.getAllImages()

    // 인사말 수정
    @Auth
    @PutMapping("/introduce")
    fun updateIntroduce(@RequestBody form: IntroduceDto) =
        adminService.updateIntroduce(form.newIntroduce)

    // 로고, 조직도, CI, 연혁 등록, 수정
    @ApiOperation(
        value = "로고, 조직도, CI, 연혁 등록/수정",
        notes = "세 개를 범용으로 수정함. where로 어느 부분인지 명시. 로고 : logo, 조직도 : orgChart, CI : ci, 연혁 : companyHistory")
    @Auth
    @PostMapping("/image")
    fun updateOthers(@ModelAttribute form: AdminImageRequest): AdminImagesResponse =
        adminService.updateOthers(form)


    // 배너 등록, 수정
    @Auth
    @PostMapping("/banner")
    fun updateBanners(@ModelAttribute form: AdminImagesRequest): AdminImagesResponse =
        adminService.updateBanner(form)

    // 관리자 패널 정보 조회
    @Auth
    @GetMapping("/panel")
    fun getPanelInfo(): AdminPanelResponse = adminService.getPanelInfo()

    // 관리자 비밀번호 변경
    @Auth
    @PutMapping("/password")
    fun updatePassword(
        @RequestBody form: UpdatePasswordForm
    ): PasswordResponse = adminService.updatePassword(form)

    // 관리자 패널 정보 수정
    @Auth
    @PutMapping("/panel")
    fun updatePanelInfo(
        @Valid @RequestBody form: UpdateAdminPanelForm
    ): AdminPanelResponse = adminService.updatePanel(form)
    
    // 카다록, 자재승인서 수정
    @Auth
    @PostMapping("/catalog-material")
    fun updateCatalogMaterial(
        @ModelAttribute form: UpdateCatalogMaterialForm
    ): AdminPanelResponse = adminService.updateCatalogMaterial(form)
}