package hntech.hntechserver.domain.admin

import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

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
    @field:NotEmpty
    var curPassword: String,

    @field:NotEmpty
    var newPassword: String,

    @field:NotEmpty
    var newPasswordCheck: String,
) {
    fun passwordCheck(): Boolean = newPassword == newPasswordCheck
}

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

data class UpdateEmailAccountForm(
    @field:Email
    var email: String,
    var password: String,
)

data class EmailSendingTimeResponse(
    var time: String,
)

data class FooterDto(
    var address: String,
    var afterService: String,
    var phone: String,
    var fax: String,
) {
    constructor(a: Admin) : this(a.address, a.afterService, a.phone, a.fax)
}

data class AdminPanelInfoResponse(
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