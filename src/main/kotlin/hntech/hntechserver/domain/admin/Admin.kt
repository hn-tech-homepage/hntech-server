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

    // 카다록 파일 서버 저장 이름
    var catalogFile: String = "",

    // 자재승인서 파일 서버 저장 이름
    var materialFile: String = "",

    // 시국세 파일 서버 저장 이름
    var taxFile: String = "",

    /**
     * 하단 (footer) 정보
     */
    var address: String = "", // 본사
    var afterService: String = "", // A/S
    var phone: String = "", // TEL
    var fax: String = "", // FAX

    // 패밀리 사이트맵 들
    @OneToMany(mappedBy = "admin", cascade = [CascadeType.ALL], orphanRemoval = true)
    var sites: MutableList<SiteMap> = mutableListOf(),

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
        newCatalogFile: String? = null,
        newMaterialFile: String? = null,
        newTaxFile: String? = null,
    ) {
        newPassword?.let { this.password = it }
        newIntroduce?.let { this.introduce = it }
        newLogoImage?.let { this.logoImage = it }
        newOrgChartImage?.let { this.orgChartImage = it }
        newCompInfoImage?.let { this.compInfoImage = it }
        newHistoryImage?.let { this.historyImage = it }
        newCatalogFile?.let { this.catalogFile = it }
        newMaterialFile?.let { this.materialFile = it }
        newTaxFile?.let { this.taxFile = it }
    }

    fun updateBanner(bannerFile: File) { this.bannerImages.add(bannerFile) }

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
        this.sites.addAll(
            form.sites.map { SiteMap(null, this, it.buttonName, it.link) }
        )
    }

}

@Entity
class SiteMap(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    var admin: Admin,

    var buttonName: String = "",
    var link: String = "",
)