package hntech.hntechserver.domain.admin

import hntech.hntechserver.common.UNKNOWN
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.domain.file.FileResponse
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Email

const val LOGO = "logo"
const val CI = "ci"
const val ORG_CHART = "orgChart"
const val HISTORY = "companyHistory"
const val INTRODUCE = "introduce"

data class LoginForm(var password: String)

data class IntroduceDto(var newIntroduce: String)

data class UpdatePasswordForm(
    var curPassword: String,
    var newPassword: String,
    var newPasswordCheck: String
)

data class PasswordResponse(var newPassword: String)

data class AdminImageRequest(
    var where: String,
    var file: MultipartFile
)

data class AdminImagesRequest(var files: List<MultipartFile>?)

data class AdminImagesResponse(
    var logoImage: FileResponse,
    var bannerImages: List<FileResponse> = listOf(),
    var orgChartImage: String = "",
    var compInfoImage: String = "",
    var historyImage: List<FileResponse> = listOf(),
) {
    constructor(admin: Admin, logoFile: File): this(
        logoImage = FileResponse(logoFile),
        bannerImages = admin.bannerImages.map { FileResponse(it) },
        orgChartImage = admin.orgChartImage,
        compInfoImage = admin.compInfoImage,
        historyImage = admin.historyImages.map { FileResponse(it) }
    )
}

data class FooterDto(
    var address: String,
    var afterService: String,
    var phone: String,
    var fax: String,
    var sites: List<SiteMapResponse> = mutableListOf(),
) {
    constructor(a: Admin) : this(
        address = a.address,
        afterService = a.afterService,
        phone = a.phone,
        fax = a.fax,
        sites = a.sites.map { SiteMapResponse(it) }
    )
}

data class SiteMapForm(
    var buttonName: String,
    var link: String
)

data class SiteMapResponse(
    var id: Long,
    var buttonName: String,
    var link: String,
) {
    constructor(sm: SiteMap): this(
        id = sm.id!!,
        buttonName = sm.buttonName,
        link = sm.link
    )
}

data class UpdateCatalogMaterialForm(
    var catalogFile: MultipartFile,
    var materialFile: MultipartFile,
    var taxFile: MultipartFile
)

data class CatalogMaterialResponse(
    var catalogOriginalFilename: String,
    var catalogServerFilename: String,
    var materialOriginalFilename: String,
    var materialServerFilename: String,
    var taxOriginalFilename: String,
    var taxServerFilename: String
)

data class UpdateAdminPanelForm(
    // 메일 전송 계정 변경
    @field:Email
    var sendEmailAccount: String = "init@email.com",
    var sendEmailPassword: String = UNKNOWN,

    // 메일 수신 계정 변경
    var receiveEmailAccount: String = "init@email.com",

    // 메일 발송 시각 변경
    var emailSendingTime: String = "3",

    // footer
    var address: String = UNKNOWN,
    var afterService: String = UNKNOWN,
    var phone: String = UNKNOWN,
    var fax: String = UNKNOWN,
    var sites: List<SiteMapForm> = mutableListOf()
)

data class AdminPanelResponse(
    var adminPassword: String,
    var sendEmailAccount: String,
    var sendEmailPassword: String,
    var receiveEmailAccount: String,
    var emailSendingTime: String,
    var catalogOriginalFilename: String,
    var materialOriginalFilename: String,
    var taxOriginalFilename: String,
    var footer: FooterDto
) {
    constructor(a: Admin, catalog: String = "", material: String = "", tax: String = "") : this(
        adminPassword = a.password,
        sendEmailAccount = a.sendEmailAccount,
        sendEmailPassword = a.sendEmailPassword,
        receiveEmailAccount = a.receiveEmailAccount,
        emailSendingTime = (a.emailSendingTime.toInt()).toString(),
        catalogOriginalFilename = catalog,
        materialOriginalFilename = material,
        taxOriginalFilename = tax,
        footer = FooterDto(a)
    )
}



