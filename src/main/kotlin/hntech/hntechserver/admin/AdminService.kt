//package hntech.hntechserver.admin
//
//import hntech.hntechserver.file.FileService
//import hntech.hntechserver.utils.logger
//import org.springframework.stereotype.Service
//import org.springframework.web.multipart.MultipartFile
//
//@Service
//class AdminService(
//    private val adminRepository: AdminRepository,
//    private val fileService: FileService
//    ) {
//    val log = logger()
//
//    fun getAdmin(): Admin {
//        val adminResult = adminRepository.findAll()
//        if (adminResult.isEmpty()) throw java.util.NoSuchElementException("관리자 계정 조회 실패")
//        else return adminResult[0]
//    }
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
//    fun updateIntroduce(newIntroduce: String): Admin =
//        getAdmin().updateIntroduce(newIntroduce)
//
//    // 조직도 수정
//    fun updateOrgChart(newImage: MultipartFile): Admin {
//        val admin = getAdmin()
//        // 기존 이미지 삭제 후 새로운 이미지 업로드
//        val savedFile = fileService.updateFile(admin.orgChartImage!!, newImage)
//        return admin.updateOrgChart(savedFile)
//    }
//
//    // CI 수정
//    fun updateCI(newImage: MultipartFile): Admin {
//        val admin = getAdmin()
//        val savedFile = fileService.updateFile(admin.compInfoImage!!, newImage)
//        return admin.updateCI(savedFile)
//    }
//
//    // 연혁 수정
//    fun updateCompanyHistory(newImage: MultipartFile): Admin {
//        val admin = getAdmin()
//        val savedFile = fileService.updateFile(admin.companyHistoryImage!!, newImage)
//        return admin.updateCI(savedFile)
//    }
//
//    // 하단 (footer) 수정
//    fun updateFooter(form: FooterDto): Admin =
//        getAdmin().updateFooter(
//            newAddress = form.address,
//            newAS = form.afterService,
//            newPhone = form.phone,
//            newFax = form.fax
//        )
//}