package hntech.hntechserver.domain.question

import hntech.hntechserver.domain.question.model.Comment
import hntech.hntechserver.domain.question.comment.CreateCommentForm
import hntech.hntechserver.domain.question.comment.UpdateCommentForm
import hntech.hntechserver.domain.question.dto.CreateQuestionForm
import hntech.hntechserver.domain.question.dto.UpdateAdminQuestionForm
import hntech.hntechserver.domain.question.dto.UpdateClientQuestionForm
import hntech.hntechserver.domain.question.dto.convertEntity
import hntech.hntechserver.domain.question.model.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val questionAlarmManager: QuestionAlarmManager
) {

    @Transactional(readOnly = true)
    fun getQuestion(questionId: Long): Question =
        questionRepository.findById(questionId).orElseThrow { throw QuestionException(QUESTION_NOT_FOUND) }

    private fun getQuestionByIdAndPassword(questionId: Long, password: String): Question =
        questionRepository.findByIdAndPassword(questionId, password) ?:
            throw QuestionException(QUESTION_NOT_FOUND)


    fun createQuestion(form: CreateQuestionForm): Question {
        val question = questionRepository.save(convertEntity(form))
        questionAlarmManager.addNewQuestion(question)
        return question
    }

    @Transactional(readOnly = true)
    // 전체 문의사항 페이징 조회
    fun findAllQuestions(pageable: Pageable): Page<Question> =
        questionRepository.findAll(pageable)

    
    // 자주 묻는 질문 리스트 조회
    fun findFAQ(pageable: Pageable): Page<Question> = questionRepository.findAllFAQ(pageable)

    // 작성한 비밀번호로 해당 문의사항 조회
    fun findQuestionByIdAndPassword(id: Long, password: String): Question =
        getQuestionByIdAndPassword(id, password)


    // 문의사항 제목, 내용 수정
    fun updateClientQuestion(id: Long, form: UpdateClientQuestionForm): Question {
        val question = getQuestion(id)
        question.update(title = form.title, content = form.content)
        return question
    }

    fun updateAdminQuestion(id: Long, form: UpdateAdminQuestionForm): Question {
        val question = getQuestion(id)
        question.update(title = form.title, content = form.content, FAQ = form.FAQ)
        return question
    }

    // 문의사항 처리 상태 변경
    fun setStatusComplete(id: Long): Question {
        val question = getQuestion(id)
        question.update(status = "완료")
        return question
    }
    
    // 문의사항 삭제
    fun deleteQuestion(id: Long): Boolean {
        questionRepository.delete(getQuestion(id))
        return true
    }

    /**
     * 댓글
     * 게시판 연관관계 매핑 중, OneToMany 에서 orphanRemoval = true 옵션을 주면, 댓글의 모든 생명주기를
     * 레포지토리 없이 게시판(부모)이 관리할 수 있다.
     * 댓글 생성 - 게시판 댓글 리스트에 추가하면 자동으로 INSERT 쿼리 발생
     * 댓글 수정 - 더티 체킹으로 UPDATE 쿼리 발생
     * 댓글 삭제 - 게시판 댓글 리스트에서 삭제 -> 부모(게시판) 객체와 단절 -> 단절된 고아 객체는 자동으로 DELETE 쿼리 발생
     */
    fun createComment(questionId: Long, form: CreateCommentForm): List<Comment> {
        val question = getQuestion(questionId)

        val comment = Comment(
            question = question,
            writer = form.writer,
            content = form.content,
            sequence = question.comments.size + 1
        )

        question.addComment(comment)

        // 클라이언트가 새 댓글 등록 시 메일로 보낼 문의사항 리스트에 추가
        if (form.writer != "관리자")
            questionAlarmManager.addNewCommentQuestion(question)
        else // 관리자가 답글 작성시 문의사항 진행도 변경
            question.update(status = "처리중")

        return question.comments
    }

    fun updateComment(questionId: Long, commentId: Long, form: UpdateCommentForm): List<Comment> {
        val question = getQuestion(questionId)
        question.comments
            .find { it.id == commentId }
            ?.update(form.content) ?: throw CommentException(COMMENT_NOT_FOUND)

        return question.comments
    }

    fun deleteComment(questionId: Long, commentId: Long): List<Comment> {
        val question = getQuestion(questionId)
        question.comments
            .find { it.id == commentId }
            .let { question.comments.remove(it) }

        return question.comments
    }
}

