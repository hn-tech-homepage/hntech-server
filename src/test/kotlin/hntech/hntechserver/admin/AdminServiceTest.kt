package hntech.hntechserver.admin

import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@Transactional
class AdminServiceTest {

    @Autowired lateinit var adminService: AdminService
    @Autowired lateinit var adminRepository: AdminRepository

    val log = logger()

    fun <T> logResult(actual: T, expected: T) {
        log.info("result\nactual \t\t: {} \nexpected \t: {}", actual.toString(), expected.toString())
    }

    @BeforeEach
    fun setUp() {
        adminService.createAdmin("1234")
    }

    @Test @DisplayName("관리자 생성 성공")
    fun createAdmin() {
        val expected: Admin = adminRepository.findByPassword("1234")!!
        val actual: Admin = adminService.getAdmin()

        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test @DisplayName("관리자 조회 오류 - 중복 관리자")
    fun getAdminFail() {

    }
}