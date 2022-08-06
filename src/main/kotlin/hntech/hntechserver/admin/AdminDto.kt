package hntech.hntechserver.admin

import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotEmpty

const val CI = "ci"
const val ORG_CHART = "orgChart"
const val HISTORY = "companyHistory"
const val INTRODUCE = "introduce"

data class IntroduceDto(
    var newIntroduce: String,
)

data class PasswordRequest(
    @field:NotEmpty
    var curPassword: String,

    @field:NotEmpty
    var curPasswordCheck: String,

    @field:NotEmpty
    var newPassword: String,
)

data class PasswordResponse(
    var newPassword: String,
)

data class AdminImageRequest(
    var where: String,
    var newImage: MultipartFile,
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