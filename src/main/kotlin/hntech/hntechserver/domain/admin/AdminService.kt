package hntech.hntechserver.domain.admin

import hntech.hntechserver.common.ADMIN
import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.common.LOGIN_FAIL
import hntech.hntechserver.config.FILE_TYPE_ADMIN
import hntech.hntechserver.config.YAML_FILE_PATH
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.utils.logging.logger
import hntech.hntechserver.utils.scheduler.EmailSchedulingConfigurer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.PrintWriter
import javax.security.auth.login.LoginException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
@Transactional
class AdminService(
    private val adminRepository: AdminRepository,
    private val fileService: FileService,
    private val emailSchedulingConfigurer: EmailSchedulingConfigurer
) {
    val log = logger()

    /**
     * 관리자 정보 조회
     */
    @Transactional(readOnly = true)
    fun getAdmin(): Admin =
        adminRepository.findById(1).orElseThrow { AdminException("관리자 계정 조회 실패") }

    @Transactional(readOnly = true)
    fun getIntroduce() = IntroduceDto(getAdmin().introduce)

    @Transactional(readOnly = true)
    fun getFooter() = FooterDto(getAdmin())

    // 카다록, 제품승인서 조회
    @Transactional(readOnly = true)
    fun getCatalogMaterial(): CatalogMaterialResponse {
        val admin = getAdmin()
        val catalogServerFilename = admin.catalogFile
        val materialServerFilename = admin.materialFile
        val taxServerFilename = admin.taxFile
        return CatalogMaterialResponse(
            catalogOriginalFilename = fileService.getOriginalFilename(catalogServerFilename),
            catalogServerFilename = catalogServerFilename,
            materialOriginalFilename = fileService.getOriginalFilename(materialServerFilename),
            materialServerFilename = materialServerFilename,
            taxOriginalFilename = fileService.getOriginalFilename(taxServerFilename),
            taxServerFilename = taxServerFilename
        )
    }

    // 회사소개 모든 사진 조회
    @Transactional(readOnly = true)
    fun getAllImages(): AdminImagesResponse {
        val admin = getAdmin()
        return AdminImagesResponse(admin, fileService.getFile(admin.logoImage))
    }

    /**
     * 관리자 로그인, 로그아웃
     */
    fun login(password: String, request: HttpServletRequest): BoolResponse {
        val admin = getAdmin()
        if (password == admin.password) {
            val session = request.getSession(true)
            session.setAttribute(ADMIN, admin)
            log.info("admin login")
            return BoolResponse(true)
        }
        throw LoginException(LOGIN_FAIL)
    }

    fun logout(request: HttpServletRequest, response: HttpServletResponse): BoolResponse {
        request.session.invalidate()
        request.cookies
            .find { it.name == "JSESSIONID" }
            ?.let {
                it.maxAge = 0; it.path = "/"
                response.addCookie(it)
            }
        log.info("admin logout")
        return BoolResponse(true)
    }

    /**
     * 관리자 생성
     */
    fun createAdmin(admin: Admin): Admin {
        if (adminRepository.findAll().isNotEmpty())
            throw AdminException("기존 관리자 존재. 중복 관리자 생성 불가능.")
        return adminRepository.save(admin)
    }

    /**
     * 관리자 정보 수정
     */
    // 인사말 수정
    fun updateIntroduce(newIntroduce: String): IntroduceDto {
        val admin = getAdmin()
        admin.update(newIntroduce = newIntroduce)
        return IntroduceDto(admin.introduce)
    }

    // 배너 여러장 등록, 수정
    fun updateBanner(form: AdminImagesRequest): AdminImagesResponse {
        val admin = getAdmin()

        form.files?.forEach {
            admin.updateBanner(
                fileService.saveFile(it, FILE_TYPE_ADMIN).update(fileAdmin = admin)
            )
        }
        return AdminImagesResponse(admin, fileService.getFile(admin.logoImage))
    }

    fun updateHistoryImages(form: AdminImagesRequest): AdminImagesResponse {
        val admin = getAdmin()

        form.files?.forEach {
            admin.updateHistoryImages(
                fileService.saveFile(it, FILE_TYPE_ADMIN).update(fileAdminHistory = admin)
            )
        }
        return AdminImagesResponse(admin, fileService.getFile(admin.logoImage))
    }

    private fun updateImage(newImage: MultipartFile, where: String, type: String) =
        fileService.updateFile(where, newImage, FILE_TYPE_ADMIN)
            .update(type = type).serverFilename

    // 로고 수정
    private fun updateLogo(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        admin.update(
            newLogoImage = updateImage(newImage, admin.logoImage, "로고")
        )
        return admin
    }

    // 조직도 수정
    private fun updateOrgChart(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        admin.update(
            newOrgChartImage = updateImage(newImage, admin.orgChartImage, "조직도")
        )
        return admin
    }

    // CI 수정
    private fun updateCI(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        admin.update(
            newCompInfoImage = updateImage(newImage, admin.compInfoImage, "CI")
        )
        return admin
    }

    // 로고, 조직도, CI, 연혁 등록, 수정
    fun updateOthers(form: AdminImageRequest): AdminImagesResponse {
        val admin = getAdmin()
        val logoImage = fileService.getFile(admin.logoImage)
        return if (form.file.isEmpty)
            AdminImagesResponse(admin, logoImage)
        else
            AdminImagesResponse(
                when (form.where) {
                    LOGO -> updateLogo(form.file)
                    ORG_CHART -> updateOrgChart(form.file)
                    else -> updateCI(form.file)
                }, logoImage
            )
    }

    /**
     * 관리자 패널
     * - 관리자 비밀번호
     * - 메일 송신 계정 + 비번
     * - 메일 수신 계정
     * - 메일 발송 시각
     * - footer 회사 정보
     * - 카다록, 자재승인서, 시국세
     */
    // 관리자 패널 정보 조회
    @Transactional(readOnly = true)
    fun getPanelInfo(): AdminPanelResponse {
        val admin = getAdmin()
        return AdminPanelResponse(admin, admin.catalogFile, admin.materialFile)
    }

    // 관리자 비밀번호 변경
    fun updatePassword(form: UpdatePasswordForm): PasswordResponse {
        // 비밀번호 검증
        if (form.newPassword != form.newPasswordCheck)
            throw AdminException(ADMIN_PASSWORD_CHECK_FAIL)
        if (form.curPassword != getAdmin().password)
            throw AdminException(ADMIN_PASSWORD_VALID_FAIL)

        val admin = getAdmin()
        getAdmin().update(newPassword = form.newPassword)
        return PasswordResponse(admin.password)
    }

    // 패널 정보 수정 (비번, 메일 송신 계정+비번, 수신 계정, 시각, footer)
    fun updatePanel(form: UpdateAdminPanelForm): AdminPanelResponse {
        // 메일 전송 계정 yml 수정
        val yml = PrintWriter(YAML_FILE_PATH)
        yml.print(""); yml.write(
            "spring:\n" +
                    "  mail:\n" +
                    "    host: smtp.naver.com\n" +
                    "    port: 465\n" +
                    "    username: " + form.sendEmailAccount + "\n" +
                    "    password: " + form.sendEmailPassword + "\n" +
                    "    properties:\n" +
                    "      mail.smtp:\n" +
                    "        auth: true\n" +
                    "        ssl:\n" +
                    "          enable: true\n" +
                    "          trust: smtp.naver.com\n" +
                    "        starttls.enable: true"
        ); yml.close()
        // 어드민 업데이트
        val admin = getAdmin()
        admin.updatePanel(form)
        adminRepository.flush()

        // 이메일 발송 시각 변경
        emailSchedulingConfigurer.setCron(form.emailSendingTime)

        return AdminPanelResponse(admin)
    }
    
    // 카다록, 자재승인서 수정
    fun updateCatalogMaterial(form: UpdateCatalogMaterialForm): AdminPanelResponse {
        val admin = getAdmin()

        // Multipart 가 비어있으면 변경을 안 한다는 뜻이므로, 원래 저장되어 있던걸 다시 반환
        val catalog: String = when (form.catalogFile.isEmpty) {
            false -> fileService.updateFile(
                oldFileServerFilename = admin.catalogFile,
                newMultipartFile = form.catalogFile,
                saveType = FILE_TYPE_ADMIN
            ).serverFilename
            else -> admin.catalogFile
        }
        
        val material: String = when (form.materialFile.isEmpty) {
            false -> fileService.updateFile(
                oldFileServerFilename = admin.materialFile,
                newMultipartFile = form.materialFile,
                saveType = FILE_TYPE_ADMIN
            ).serverFilename
            else -> admin.materialFile
        }

        val tax: String = when (form.taxFile.isEmpty) {
            false -> fileService.updateFile(
                oldFileServerFilename = admin.taxFile,
                newMultipartFile = form.taxFile,
                saveType = FILE_TYPE_ADMIN
            ).serverFilename
            else -> admin.taxFile
        }

        admin.update(
            newCatalogFile = catalog,
            newMaterialFile = material,
            newTaxFile = tax
        )

        return AdminPanelResponse(
            admin,
            catalog = fileService.getFile(admin.catalogFile).originalFilename,
            material = fileService.getFile(admin.materialFile).originalFilename,
            tax = fileService.getFile(admin.materialFile).originalFilename,
        )
    }
}