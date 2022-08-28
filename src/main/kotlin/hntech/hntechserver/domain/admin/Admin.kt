package hntech.hntechserver.domain.admin

import hntech.hntechserver.domain.file.File
import javax.persistence.*

@Entity
class Admin(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    var id: Long? = null,

    var password: String,

    /**
     * 회사 정보
     */
    // 로고 사진
    var logoImage: String = "",

    // 배너 사진
    @OneToMany(mappedBy = "fileAdmin", cascade = [CascadeType.ALL])
    var bannerImages: MutableList<File> = mutableListOf(),

    // 인사말
    var introduce: String = "",

    // 조직도 이미지 서버 저장 이름
    var orgChartImage: String = "",

    // CI 소개 이미지 서버 저장 이름
    var compInfoImage: String = "",

    // 회사 연혁 이미지 서버 저장 이름
    var historyImage: String = "",

    // 카다록
    var catalogFile: String = "",

    // 자재승인서
    var materialApprovalFile: String = "",

    /*
     * 하단 (footer) 정보
     */
    var address: String = "", // 본사
    var afterService: String = "", // A/S
    var phone: String = "", // TEL
    var fax: String = "", // FAX

    /**
     * 알림 메일 관련
     */
    var sendEmailAccount: String = "",
    var sendEmailPassword: String = "",
    var receiveEmailAccount: String = "",
    var emailSendingTime: String = "", // cron
) {
    fun update(
        newPassword: String? = null,
        newIntroduce: String? = null,
        newLogoImage: String? = null,
        newOrgChartImage: String? = null,
        newCompInfoImage: String? = null,
        newHistoryImage: String? = null,
    ) {
        newPassword?.let { this.password = it }
        newIntroduce?.let { this.introduce = it }
        newLogoImage?.let { this.logoImage = it }
        newOrgChartImage?.let { this.orgChartImage = it }
        newCompInfoImage?.let { this.compInfoImage = it }
        newHistoryImage?.let { this.historyImage = it }
    }

    fun updateBanner(newBanners: MutableList<File>) { this.bannerImages = newBanners }

    fun updateFooter(newAddress: String, newAS: String, newPhone: String, newFax: String): Admin {
        this.address = newAddress
        this.afterService = newAS
        this.phone = newPhone
        this.fax = newFax
        return this
    }

    fun updatePanel(form: UpdateAdminPanelForm) {
        this.sendEmailAccount = form.sendEmailAccount
        this.sendEmailPassword = form.sendEmailPassword
        this.receiveEmailAccount = form.receiveEmailAccount
        this.emailSendingTime = form.emailSendingTime

        // footer
        this.address = form.address
        this.afterService = form.afterService
        this.phone = form.phone
        this.fax = form.fax
    }

}