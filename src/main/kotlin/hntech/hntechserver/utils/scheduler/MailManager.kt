package hntech.hntechserver.utils.scheduler

import hntech.hntechserver.question.*
import hntech.hntechserver.utils.PropertiesManager
import hntech.hntechserver.utils.logging.logger
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
            log.debug(e.message)
            throw EmailException(EMAIL_SEND_ERROR)
        }
    }
}