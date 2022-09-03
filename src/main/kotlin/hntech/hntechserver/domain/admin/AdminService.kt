package hntech.hntechserver.domain.admin

import hntech.hntechserver.config.*
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.utils.logging.logger
import hntech.hntechserver.utils.scheduler.EmailSchedulingConfigurer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.PrintWriter
import javax.security.auth.login.LoginException
import javax.servlet.http.HttpServletRequest

@Service
@Transactional
class AdminService(
    private val adminRepository: AdminRepository,
    private val fileService: FileService,
    private val emailSchedulingConfigurer: EmailSchedulingConfigurer,
    )
{
    val log = logger()

    fun getAdmin(): Admin =
        adminRepository.findById(1).orElseThrow { AdminException("관리자 계정 조회 실패") }


    /**
     * 관리자 로그인, 로그아웃
     */
    fun login(password: String, request: HttpServletRequest): Boolean {
        val admin = getAdmin()
        if (password == admin.password) {
            val session = request.getSession(true)
            session.setAttribute(ADMIN, admin)
            return true
        }
        throw LoginException(LOGIN_FAIL)
    }

    fun logout(request: HttpServletRequest): Boolean {
        request.session.invalidate()
        return true
    }

    /**
     * 관리자 생성
     */
    fun createAdmin(password: String): Admin {
        if (adminRepository.findAll().isNotEmpty())
            throw AdminException("기존 관리자 존재. 중복 관리자 생성 불가능.")
        adminRepository.save(Admin(password = password, logoImage = "test0.jpg"))
        val initForm = UpdateAdminPanelForm(
            emailSendingTime = "12",
            sendEmailAccount = "ahdwjdtprtm@naver.com",
            sendEmailPassword = "wnrduqjflsek1",
            receiveEmailAccount = "ahdwjdtprtm@naver.com",

            address = "주소주소주소",
            afterService = "000-000-0000",
            phone = "031-337-4005",
            fax = "031-337-4006"
        )
        getAdmin().updatePanel(initForm)
        return getAdmin()
    }

    /**
     * 관리자 정보 수정
     */
    // 인사말 수정
    fun updateIntroduce(newIntroduce: String): String {
        val admin = getAdmin()
        admin.update(newIntroduce = newIntroduce)
        return admin.introduce
    }

    // 배너 여러장 등록, 수정
    fun updateImages(form: AdminImagesRequest): Admin {
        val admin = getAdmin()

        // TODO 배너 등록, 수정 로직 변경중
        admin.update()

        return admin
    }

    private fun updateImage(newImage: MultipartFile, where: String, type: String) =
        fileService.updateFile(where, newImage, FILE_TYPE_ADMIN)
            .update(type = type).serverFilename

    // 로고 수정
    fun updateLogo(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        admin.update(
            newLogoImage = updateImage(newImage, admin.logoImage, "로고")
        )
        return admin
    }

    // 조직도 수정
    fun updateOrgChart(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        admin.update(
            newOrgChartImage = updateImage(newImage, admin.orgChartImage, "조직도")
        )
        return admin
    }

    // CI 수정
    fun updateCI(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        admin.update(
            newCompInfoImage = updateImage(newImage, admin.compInfoImage, "CI")
        )
        return admin
    }

    // 연혁 수정
    fun updateCompanyHistory(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        admin.update(
            newHistoryImage = updateImage(newImage, admin.historyImage, "연혁")
        )
        return admin
    }

    /**
     * 관리자 패널
     * - 관리자 비밀번호
     * - 메일 송신 계정 + 비번
     * - 메일 수신 계정
     * - 메일 발송 시각
     * - footer 회사 정보
     * - 카다록, 자재승인서
     */
    // 관리자 비밀번호 변경
    fun updatePassword(form: UpdatePasswordForm): String {
        // 비밀번호 검증
        if (form.newPassword != form.newPasswordCheck)
            throw AdminException(ADMIN_PASSWORD_CHECK_FAIL)
        if (form.curPassword != getAdmin().password)
            throw AdminException(ADMIN_PASSWORD_VALID_FAIL)

        val admin = getAdmin()
        getAdmin().update(newPassword = form.newPassword)
        return admin.password
    }

    // 패널 정보 수정 (비번, 메일 송신 계정+비번, 수신 계정, 시각, footer)
    fun updatePanel(form: UpdateAdminPanelForm): Admin {
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

        // 이메일 발송 시각 변경
        emailSchedulingConfigurer.setCron(form.emailSendingTime)

        return admin
    }
    
    // 카다록, 자재승인서 수정
    fun updateCatalogMaterial(form: UpdateCatalogMaterialForm): Admin {
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
        admin.update(newCatalogFile = catalog, newMaterialFile = material)
        
        return admin
    }

}