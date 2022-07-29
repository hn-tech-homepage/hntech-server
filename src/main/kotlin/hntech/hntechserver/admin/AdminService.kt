//package hntech.hntechserver.admin
//
//import hntech.hntechserver.utils.logger
//import org.springframework.stereotype.Service
//import org.springframework.web.multipart.MultipartFile
//
//@Service
//class AdminService(private val adminRepository: AdminRepository) {
//    val log = logger()
//
//    fun getAdmin(): Admin = adminRepository.findAll()[0]
//
//    /**
//     * 관리자 생성
//     */
//    fun createAdmin(password: String): Admin =
//        adminRepository.save(Admin(password = password))
//
//    /**
//     * 관리자 정보 수정
//     */
//    // 인사말 수정
//    fun updateIntroduce(newIntroduce: String): Admin = getAdmin().updateIntroduce(newIntroduce)
//
//    // 조직도 수정
//    fun updateOrgChart(newImage: MultipartFile): Admin {
//        // 파일 저장
//        return getAdmin().updateOrgChart()
//    }
//
//    // CI 수정
//    fun updateCI(newImage: MultipartFile): Admin {
//        // 파일 저장
//        return getAdmin().updateCI()
//    }
//
//    // 연혁 수정
//    fun updateCompanyHistory(newImage: MultipartFile): Admin {
//        // 파일 저장
//        return getAdmin().updateCompanyHistory()
//    }
//
//    // 하단 (footer) 수정
//    fun updateFooter(form: FooterDto): Admin
//    = getAdmin().updateFooter(
//            newAddress = form.address,
//            newAS = form.afterService,
//            newPhone = form.phone,
//            newFax = form.fax
//        )
//
//
//}