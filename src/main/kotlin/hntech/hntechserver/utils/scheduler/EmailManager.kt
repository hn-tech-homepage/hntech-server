package hntech.hntechserver.utils.scheduler

import hntech.hntechserver.domain.admin.AdminRepository
import hntech.hntechserver.domain.question.EMAIL_NOT_FOUND
import hntech.hntechserver.domain.question.EMAIL_SEND_ERROR
import hntech.hntechserver.domain.question.EmailException
import hntech.hntechserver.domain.question.QuestionAlarmManager
import hntech.hntechserver.utils.PropertiesManager
import hntech.hntechserver.utils.logging.logger
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class EmailManager(
    private val mailSender: JavaMailSender,
    private val questionAlarmManager: QuestionAlarmManager,
    private val propertiesManager: PropertiesManager,
    private val adminRepository: AdminRepository
) {
    private val log = logger()

    fun sendMail() {
        // 설정 파일로부터 송신 이메일 찾기
        val sendEmail = propertiesManager.getConfiguration()?.getString("spring.mail.username")
            ?: throw EmailException(EMAIL_NOT_FOUND)

        // DB 에서 수신 이메일 찾기
        val receiveEmail = adminRepository.findAll()[0].receiveEmailAccount
        log.info("송신 이메일 주소 : {}", sendEmail)
        log.info("수신 이메일 주소 : {}", receiveEmail)

        try {
            val mail = mailSender.createMimeMessage()
            val mailHelper = MimeMessageHelper(mail, false, "UTF-8")
            val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            val questions = questionAlarmManager.getQuestionListToSend()
            var text = ""

            mailHelper.setFrom(sendEmail, "HNTECH 웹페이지")
            mailHelper.setTo(receiveEmail)
            mailHelper.setSubject("HNTECH 웹페이지: $now 신규 등록된 문의사항")
            text += "<html>" +
                    "<head>" +
                    "   <meta charset='utf-8'>" +
                    "</head>" +
                    "<body>"

            text += "<h3>금일 등록된 문의 내역</h3>" +
                    "<table width='100%' style='border-collapse: collapse;' border=1>" +
                    "<th>번호</th> <th>작성자</th> <th>제목</th>"
            questions.newQuestions.forEach {
                text += "<tr> " +
                        "<td style='text-align: center;'>${it.id}</td> " +
                        "<td style='text-align: center;'>${it.writer}</td> " +
                        "<td style='text-align: center;'>${it.title}</td> " +
                        "</tr>"
            }
            text += "</table>"
            
            text += "<h3>금일 새 댓글이 달린 문의 내역</h3>" +
                    "<table width='100%' style='border-collapse: collapse;' border=1>" +
                    "<th>번호</th> <th>작성자</th> <th>제목</th>"
            questions.newCommentQuestions.forEach {
                text += "<tr>" +
                        "<td style='text-align: center;'>${it.id}</td>" +
                        "<td style='text-align: center;'>${it.writer}</td> " +
                        "<td style='text-align: center;'>${it.title}</td> " +
                        "</tr>"
            }
            text += "</table>"

            text += "</body>" +
                    "</html>"

            mailHelper.setText(text, true)

            mailSender.send(mail)

            questionAlarmManager.clearQuestionList()
        } catch (e: Exception) {
            log.warn(e.message)
            throw EmailException(EMAIL_SEND_ERROR)
        }
    }
}