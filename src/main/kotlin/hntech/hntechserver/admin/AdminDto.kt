package hntech.hntechserver.admin

import hntech.hntechserver.file.File

const val CI = "ci"
const val ORG_CHART = "orgChart"
const val HISTORY = "companyHistory"


data class IntroduceDto(
    var newIntroduce: String,
)

data class ImageResponse(
    var serverSavedFilename: String,
)

data class FooterDto(
    var address: String,
    var afterService: String,
    var phone: String,
    var fax: String,
)

fun convertDto(newImageFile: File) {

}

fun convertDto(a: Admin, type: String): Any {
    return when (type) {
        CI -> ImageResponse(a.compInfoImage!!.serverFileName)
        ORG_CHART -> ImageResponse(a.orgChartImage!!.serverFileName)
        HISTORY -> ImageResponse(a.companyHistoryImage!!.serverFileName)
        else -> FooterDto(a.address, a.afterService, a.phone, a.fax)
    }
}