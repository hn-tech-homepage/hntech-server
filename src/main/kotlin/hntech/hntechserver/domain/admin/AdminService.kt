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
    private val bannerRepository: BannerRepository,
    private val fileService: FileService,
    private val fileRepository: FileRepository,
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
        fileRepository.save(File(originalFilename = "관리자용 더미 파일", serverFilename = ""))
        adminRepository.save(Admin(password = password))
        getAdmin().update(
            newMailSendingTime = "12",
            newSendEmailAccount = "ahdwjdtprtm@naver.com",
            newSendEmailPassword = "wnrduqjflsek1",
            newReceiveEmailAccount = "ahdwjdtprtm@naver.com"
        )
        return getAdmin()
    }

    /**
     * 관리자 정보 수정
     */
    // 관리자 비밀번호 변경
    fun updatePassword(form: UpdatePasswordForm): String {
        if (!form.passwordCheck()) throw AdminException(ADMIN_PASSWORD_CHECK_FAIL)
        if (form.curPassword != getAdmin().password)
            throw AdminException(ADMIN_PASSWORD_VALID_FAIL)

        getAdmin().update(newPassword = form.newPassword)
        return getAdmin().password
    }

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
        val savedFile = fileService.updateFile(curImage, newImage,
            ADMIN_SAVE_PATH_WINDOW
//            ADMIN_IMAGE_SAVE_PATH_LINUX
        )
        return savedFile.serverFilename
    }

    // 로고 수정
    fun updateLogo(newImage: MultipartFile): String {
        val admin = getAdmin()
        admin.update(newLogo = updateImage(newImage, admin.logoImage))
        return admin.logoImage
    }

    // 배너 여러장 수정
    fun updateBanner(newImages: List<String>): List<String> {
        val admin = getAdmin()

        // 기존에 저장된 배너 삭제
        bannerRepository.deleteAll()
        admin.bannerImages.clear()

        newImages.forEach {
            val banner = Banner(admin = admin, imgServerFilename = it)
            bannerRepository.save(banner)
        }

        return newImages
    }

    // 조직도 수정
    fun updateOrgChart(newImage: MultipartFile): String {
        val admin = getAdmin()
        admin.update(newOrgChartImage = updateImage(newImage, admin.orgChartImage))
        return admin.orgChartImage
    }

    // CI 수정
    fun updateCI(newImage: MultipartFile): String {
        val admin = getAdmin()
        admin.update(newCompInfoImage = updateImage(newImage, admin.compInfoImage))
        return admin.compInfoImage
    }

    // 연혁 수정
    fun updateCompanyHistory(newImage: MultipartFile): String {
        val admin = getAdmin()
        admin.update(newHistoryImage = updateImage(newImage, admin.historyImage))
        return admin.historyImage
    }

    // 하단 (footer) 수정
    fun updateFooter(form: FooterDto): Admin =
        getAdmin().updateFooter(
            newAddress = form.address,
            newAS = form.afterService,
            newPhone = form.phone,
            newFax = form.fax
        )

    // 메일 변경
    fun updateMail(form: UpdateEmailAccountForm): UpdateEmailAccountForm {
        val yml = PrintWriter(YAML_FILE_PATH_WINDOW)
//        val yml = PrintWriter(YAML_FILE_PATH_LINUX)
        yml.print("")
        yml.write(
            "spring:\n" +
            "  mail:\n" +
            "    host: smtp.naver.com\n" +
            "    port: 465\n" +
            "    username: " + form.email + "\n" +
            "    password: " + form.password + "\n" +
            "    properties:\n" +
            "      mail.smtp:\n" +
            "        auth: true\n" +
            "        ssl:\n" +
            "          enable: true\n" +
            "          trust: smtp.naver.com\n" +
            "        starttls.enable: true"
        )
        yml.close()
        val admin = getAdmin()
        admin.update(
            newSendEmailAccount = form.email,
            newSendEmailPassword = form.password
        )
        return form
    }

    // 메일 전송 시각 조회
    fun getMailSendingTime(): String = getAdmin().emailSendingTime

    // 메일 전송 시각 수정
    fun updateMailSendingTime(newTime: String): String {
        getAdmin().update(newMailSendingTime = newTime)
        emailSchedulingConfigurer.setCron(newTime)
        return getMailSendingTime()
    }
}