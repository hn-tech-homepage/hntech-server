package hntech.hntechserver.admin

import hntech.hntechserver.file.FileRepository
import hntech.hntechserver.file.FileService
import hntech.hntechserver.logResult
import hntech.hntechserver.testFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@Transactional
class AdminServiceTest {
    @Autowired lateinit var adminService: AdminService
    @Autowired lateinit var adminRepository: AdminRepository
    @Autowired lateinit var fileService: FileService
    @Autowired lateinit var fileRepository: FileRepository

    @BeforeEach
    fun `어드민 생성`() {
        adminService.createAdmin("1234")
    }

    @AfterEach
    fun `mock 파일 삭제`() = fileService.deleteAllFiles(fileRepository.findAll())


    @Test
    fun `관리자 조회 실패 - 관리자 없음`() {
        adminRepository.delete(adminService.getAdmin())
        shouldThrow<AdminException> { adminService.getAdmin() }
    }

    @Test
    fun `관리자 생성 성공`() {
        val expected: Admin = adminRepository.findByPassword("1234")!!
        val actual: Admin = adminService.getAdmin()

        actual shouldBe expected
        logResult(actual, expected)
    }

    @Test
    fun `관리자 생성 실패 - 중복 관리자`() =
        shouldThrow<AdminException> { adminService.createAdmin("1234") }


    @Test
    fun `관리자 비밀번호 변경 성공`() {
        // given
        val form = PasswordRequest("1234", "1234", "1111")

        // when
        val expected: String = adminService.updatePassword(form)
        val actual: String = adminService.getAdmin().password

        // then
        expected shouldBe actual
        logResult(expected, actual)
    }

    @Test
    fun `관리자 비밀번호 변경 실패 - 현재 비밀번호를 틀림`() {
        val form = PasswordRequest("1234", "1111", "1234")
        shouldThrow<AdminException> { adminService.updatePassword(form) }
    }

    @Test
    fun `인사말 수정 성공`() {
        // given
        val newIntroduce = "안녕"
        val curIntroduce = adminService.getAdmin().introduce

        // when
        val expected: String = adminService.updateIntroduce(newIntroduce)

        // then
        expected shouldBe newIntroduce
        expected shouldNotBe curIntroduce
    }

    @Test
    fun `조직도 수정 성공`() {
        val expected: String = adminService.updateOrgChart(testFile)
        val actual: String = adminService.getAdmin().orgChartImage

        expected shouldBe actual
        logResult(actual, expected)
    }

    @Test
    fun `CI 수정 성공`() {
        val expected: String = adminService.updateCI(testFile)
        val actual: String = adminService.getAdmin().compInfoImage

        expected shouldBe actual
        logResult(actual, expected)
    }

    @Test
    fun `연혁 수정 성공`() {
        val expected: String = adminService.updateCompanyHistory(testFile)
        val actual: String = adminService.getAdmin().historyImage

        expected shouldBe actual
        logResult(actual, expected)
    }

    @Test
    fun `footer 수정 성공`() {
        // given
        val form = FooterDto("1", "2", "3", "4")

        // when
        val expected: Admin = adminService.updateFooter(form)
        val actual: Admin = adminService.getAdmin()

        // then
        expected.address shouldBe actual.address
        expected.afterService shouldBe actual.afterService
        expected.phone shouldBe actual.phone
        expected.fax shouldBe actual.fax
    }

}