package hntech.hntechserver.domain.admin

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
    // 로고
    var logoImage: String = "",
    
    // 배너 사진
    @OneToMany(mappedBy = "admin", cascade = [CascadeType.ALL])
    var bannerImages: MutableList<Banner> = mutableListOf(),

    // 인사말
    var introduce: String = "",

    // 조직도 이미지 서버 저장 이름
    var orgChartImage: String = "",

    // CI 소개 이미지 서버 저장 이름
    var compInfoImage: String = "",

    // 회사 연혁 이미지 서버 저장 이름
    var historyImage: String = "",

    /**
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
        newLogo: String? = null,
        newPassword: String? = null,
        newIntroduce: String? = null,
        newOrgChartImage: String? = null,
        newCompInfoImage: String? = null,
        newHistoryImage: String? = null,
        newSendEmailAccount: String? = null,
        newSendEmailPassword: String? = null,
        newReceiveEmailAccount: String? = null,
        newMailSendingTime: String? = null,
    ) {
        newLogo?.let { this.logoImage = it }
        newPassword?.let { this.password = it }
        newIntroduce?.let { this.introduce = it }
        newOrgChartImage?.let { this.orgChartImage = it }
        newCompInfoImage?.let { this.compInfoImage = it }
        newHistoryImage?.let { this.historyImage = it }
        newSendEmailAccount?.let { this.sendEmailAccount = it }
        newSendEmailPassword?.let { this.sendEmailPassword = it }
        newReceiveEmailAccount?.let { this.receiveEmailAccount = it }
        newMailSendingTime?.let { this.emailSendingTime = it }
    }

    fun updateFooter(newAddress: String, newAS: String, newPhone: String, newFax: String): Admin {
        this.address = newAddress
        this.afterService = newAS
        this.phone = newPhone
        this.fax = newFax
        return this
    }

}

@Entity
class Banner(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    var admin: Admin,

    var imgServerFilename: String = "",
)