package hntech.hntechserver.admin

import hntech.hntechserver.file.File
import javax.persistence.*

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

    // 조직도 이미지 저장 경로
    @OneToOne
    @JoinColumn(name = "org_chart_file_id")
    var orgChartImage: File? = null,

    // CI 소개 이미지 저장 경로
    @OneToOne
    @JoinColumn(name = "ci_file_id")
    var compInfoImage: File? = null,

    // 회사 연혁 이미지 저장 경로
    @OneToOne
    @JoinColumn(name = "company_history_file_id")
    var companyHistoryImage: File? = null,

    /**
     * 하단 (footer) 정보
     */
    // 본사
    var address: String = "",

    // A/S
    var afterService: String = "",

    // TEL
    var phone: String = "",

    // FAX
    var fax: String = "",
) {
    fun updatePassword(new: String): Admin  {
        this.password = new
        return this
    }

    fun updateIntroduce(new: String): Admin {
        this.introduce = new
        return this
    }

    fun updateOrgChart(newFile: File): Admin {
        this.orgChartImage = newFile
        return this
    }

    fun updateCI(newFile: File): Admin {
        this.compInfoImage = newFile
        return this
    }

    fun updateCompanyHistory(newFile: File): Admin {
        this.companyHistoryImage = newFile
        return this
    }

    fun updateFooter(newAddress: String, newAS: String, newPhone: String, newFax: String): Admin {
        this.address = newAddress
        this.afterService = newAS
        this.phone = newPhone
        this.fax = newFax
        return this
    }

}