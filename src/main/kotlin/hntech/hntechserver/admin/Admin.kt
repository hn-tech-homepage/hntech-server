package hntech.hntechserver.admin

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Admin(
    @Id @GeneratedValue
    @Column(name = "admin_id")
    var id: Long? = null,

    var password: String,

    /**
     * 회사 정보
     */
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

    // 문의사항 메일 발송 시각
    var mailSendingTime: String = "", // cron
) {
    fun update(
        newPassword: String? = null,
        newIntroduce: String? = null,
        newOrgChartImage: String? = null,
        newCompInfoImage: String? = null,
        newHistoryImage: String? = null,
        newMailSendingTime: String? = null,
    ) {
        newPassword?.let { this.password = it }
        newIntroduce?.let { this.introduce = it }
        newOrgChartImage?.let { this.orgChartImage = it }
        newCompInfoImage?.let { this.compInfoImage = it }
        newHistoryImage?.let { this.historyImage = it }
        newMailSendingTime?.let { this.mailSendingTime = it }
    }

    fun updateFooter(newAddress: String, newAS: String, newPhone: String, newFax: String): Admin {
        this.address = newAddress
        this.afterService = newAS
        this.phone = newPhone
        this.fax = newFax
        return this
    }

}