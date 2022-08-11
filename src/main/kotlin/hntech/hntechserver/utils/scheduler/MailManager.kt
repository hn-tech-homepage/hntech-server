package hntech.hntechserver.utils.scheduler

import hntech.hntechserver.question.*
import hntech.hntechserver.utils.PropertiesManager
import hntech.hntechserver.utils.logger
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class MailManager(
    private val mailSender: JavaMailSender,
    private val questionAlarmManager: QuestionAlarmManager,
    private val propertiesManager: PropertiesManager
) {
    private val log = logger()

    fun sendMail() {
        // 설정 파일로부터 이메일 주소 찾기
        val email = propertiesManager.getConfiguration()?.let {
            it.getString("spring.mail.username")
        } ?: run { throw EmailException(EMAIL_NOT_FOUND) }
        log.info("송신 이메일 주소 : {}", email)

        try {
            val mail = mailSender.createMimeMessage()
            val mailHelper = MimeMessageHelper(mail, false, "UTF-8")
            val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            val questions = questionAlarmManager.getQuestionListToSend()
            var text = ""

            mailHelper.setFrom(email, "HNTECH 웹페이지")
            mailHelper.setTo(email)
            mailHelper.setSubject("HNTECH 웹페이지: $now 신규 등록된 문의사항")
            text += "<html>" +
                    "<body>" +
                    "<h3>HNTECH 웹페이지에 금일 등록된 문의사항 목록입니다.</h3>"
            // TODO QuestionsToSend 받아서 파싱
            text += "</body>" +
                    "</html>"

            mailHelper.setText(text, true)

            mailSender.send(mail)

            questionAlarmManager.clearQuestionList()
        } catch (e: Exception) {
            log.debug(e.message)
            throw EmailException(EMAIL_SEND_ERROR)
        }
    }
}