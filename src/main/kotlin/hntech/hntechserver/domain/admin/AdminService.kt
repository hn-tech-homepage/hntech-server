package hntech.hntechserver.domain.admin

import hntech.hntechserver.config.*
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.domain.file.FileRepository
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

    fun getAdmin(): Admin {
        val adminResult = adminRepository.findAll()
        if (adminRepository.findAll().isEmpty()) throw AdminException("관리자 계정 조회 실패")
        else return adminResult[0]
    }

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

    private fun updateImage(
        newImage: MultipartFile,
        serverFilename: String,
    ): String {
        val curImage = fileService.getFile(serverFilename)
        val savedFile = fileService.updateFile(curImage, newImage, ADMIN_SAVE_PATH)
        return savedFile.serverFilename
    }

    // 배너 여러장 등록, 수정
    fun updateImages(form: AdminImagesRequest): Admin {
        val admin = getAdmin()

        if (form.files[0].isEmpty) return admin

        admin.bannerImages.forEach { fileService.deleteFile(it) }
        admin.updateBanner(
            form.files.map {
                fileService.saveFile(it, FILE_TYPE_ADMIN).update(fileAdmin = admin)
            }.toMutableList()
        )

        return admin
    }

    // 로고 수정
    fun updateLogo(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        fileService.deleteFile(admin.logoImage)
        admin.update(
            newLogoImage = fileService.saveFile(newImage, FILE_TYPE_ADMIN).serverFilename
        )
        return admin
    }

    // 조직도 수정
    fun updateOrgChart(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        fileService.deleteFile(admin.orgChartImage)
        admin.update(
            newOrgChartImage = fileService.saveFile(newImage, FILE_TYPE_ADMIN).serverFilename
        )
        return admin
    }

    // CI 수정
    fun updateCI(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        fileService.deleteFile(admin.compInfoImage)
        admin.update(
            newCompInfoImage = fileService.saveFile(newImage, FILE_TYPE_ADMIN).serverFilename
        )
        return admin
    }

    // 연혁 수정
    fun updateCompanyHistory(newImage: MultipartFile): Admin {
        val admin = getAdmin()
        fileService.deleteFile(admin.historyImage)
        admin.update(
            newHistoryImage = fileService.saveFile(newImage, FILE_TYPE_ADMIN).serverFilename
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
        return admin
    }
}