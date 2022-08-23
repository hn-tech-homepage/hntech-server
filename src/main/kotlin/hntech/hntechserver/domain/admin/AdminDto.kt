package hntech.hntechserver.domain.admin

import hntech.hntechserver.config.UNKNOWN
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Email

const val LOGO = "logo"
const val CI = "ci"
const val ORG_CHART = "orgChart"
const val HISTORY = "companyHistory"
const val INTRODUCE = "introduce"

data class LoginForm(
    var password: String,
)

data class IntroduceDto(
    var newIntroduce: String,
)

data class UpdatePasswordForm(
    var curPassword: String,
    var newPassword: String,
    var newPasswordCheck: String,
)

data class PasswordResponse(
    var newPassword: String,
)

data class BannerDto(
    var imgServerFilenameList: List<String>
)

data class AdminImageRequest(
    var where: String,
    var file: MultipartFile,
)

data class AdminImageResponse(
    var where: String,
    var updatedServerFilename: String,
)

data class FooterDto(
    var address: String,
    var afterService: String,
    var phone: String,
    var fax: String,
) {
    constructor(a: Admin) : this(a.address, a.afterService, a.phone, a.fax)
}

data class UpdateAdminPanelForm(
    // 메일 전송 계정 변경
    @field:Email
    var sendEmailAccount: String = "init@email.com",
    var sendEmailPassword: String = UNKNOWN,

    // 메일 수신 계정 변경
    var receiveEmailAccount: String = "init@email.com",

    // 메일 발송 시각 변경
    var emailSendingTime: String = "12",

    // footer
    var address: String = UNKNOWN,
    var afterService: String = UNKNOWN,
    var phone: String = UNKNOWN,
    var fax: String = UNKNOWN,
)

data class AdminPanelResponse(
    var adminPassword: String,
    var sendEmailAccount: String,
    var sendEmailPassword: String,
    var receiveEmailAccount: String,
    var emailSendingTime: String,
    var footer: FooterDto
) {
    constructor(a: Admin) : this(
        adminPassword = a.password,
        sendEmailAccount = a.sendEmailAccount,
        sendEmailPassword = a.sendEmailPassword,
        receiveEmailAccount = a.receiveEmailAccount,
        emailSendingTime = a.emailSendingTime,
        footer = FooterDto(a)
    )
}

