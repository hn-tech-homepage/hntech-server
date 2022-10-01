package hntech.hntechserver.domain.question

import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.config.FILE_TYPE_QUESTION
import hntech.hntechserver.config.QUESTION_SAVE_PATH
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.domain.question.dto.*
import hntech.hntechserver.domain.question.model.Comment
import hntech.hntechserver.domain.question.model.Question
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val questionAlarmManager: QuestionAlarmManager,
    private val fileService: FileService
) {

    @Transactional(readOnly = true)
    fun getQuestion(id: Long): Question = 
        questionRepository.findById(id).orElseThrow { 
            throw QuestionException(QUESTION_NOT_FOUND) 
        }
    
    fun getQuestionToDto(id: Long) = QuestionDetailResponse(getQuestion(id))

    private fun getQuestionByIdAndPassword(questionId: Long, password: String): Question =
        questionRepository.findByIdAndPassword(questionId, password) ?:
            throw QuestionException(QUESTION_NOT_FOUND)

    /**
     * 문의사항 생성
     */
    fun createQuestion(form: CreateQuestionForm): QuestionDetailResponse =
        questionRepository.save(convertEntity(form)).let { question ->
            form.files?.forEach { file ->
                question.addFile(fileService.saveFile(file, FILE_TYPE_QUESTION).update(fileQuestion = question))
            }
            questionAlarmManager.addNewQuestion(question)
            QuestionDetailResponse(question)
        }

    @Transactional(readOnly = true)
    // 전체 문의사항 페이징 조회
    fun findAllQuestions(pageable: Pageable) =
        QuestionPagedResponse(questionRepository.findAll(pageable))

    // 자주 묻는 질문 리스트 조회
    fun findFAQ(pageable: Pageable) =
        QuestionPagedResponse(questionRepository.findAllFAQ(pageable))

    // 작성한 비밀번호로 해당 문의사항 조회
    fun findQuestionByIdAndPassword(id: Long, password: String) =
        QuestionDetailResponse(getQuestionByIdAndPassword(id, password))

    // 문의사항 제목, 내용 수정
    fun updateClientQuestion(id: Long, form: UpdateClientQuestionForm): QuestionDetailResponse =
        getQuestion(id).let { question ->
            form.files?.forEach { file ->
                question.addFile(fileService.saveFile(file, FILE_TYPE_QUESTION).update(fileQuestion = question))
            }
            question.update(title = form.title, content = form.content)
            QuestionDetailResponse(question)
        }

    fun updateAdminQuestion(id: Long, form: UpdateAdminQuestionForm): QuestionDetailResponse =
        getQuestion(id).let { question ->
            form.files?.forEach { file ->
                question.addFile(fileService.saveFile(file, FILE_TYPE_QUESTION).update(fileQuestion = question))
            }
            question.update(title = form.title, content = form.content, FAQ = form.FAQ)
            QuestionDetailResponse(question)
        }

    // 문의사항 처리 상태 변경
    fun setStatusComplete(id: Long): QuestionDetailResponse {
        val question = getQuestion(id)
        question.update(status = "완료")
        return QuestionDetailResponse(question)
    }
    
    // 문의사항 삭제
    fun deleteQuestion(id: Long): BoolResponse {
        questionRepository.delete(getQuestion(id))
        return BoolResponse(true)
    }

    // 문의사항 첨부 파일 삭제
    fun deleteAttachedFile(questionId: Long, fileId: Long) =
        getQuestion(questionId).let {
            it.files.remove(fileService.getFile(fileId))
        }

    /**
     * 댓글
     */
    private fun toListResponse(comments: List<Comment>) =
        CommentListResponse(comments.map { CommentResponse(it) })

    fun createComment(questionId: Long, form: CreateCommentForm): CommentListResponse {
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

        questionRepository.flush()

        return toListResponse(question.comments)
    }

    fun updateComment(
        questionId: Long,
        commentId: Long,
        form: UpdateCommentForm
    ): CommentListResponse {
        val question = getQuestion(questionId)
        question.comments
            .find { it.id == commentId }
            ?.update(form.content) ?: throw CommentException(COMMENT_NOT_FOUND)

        questionRepository.flush()

        return toListResponse(question.comments)
    }

    fun deleteComment(questionId: Long, commentId: Long): CommentListResponse {
        val question = getQuestion(questionId)
        question.comments
            .find { it.id == commentId }
            .let { question.comments.remove(it) }

        questionRepository.flush()

        return toListResponse(question.comments)
    }
}

