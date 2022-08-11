package hntech.hntechserver.question

import hntech.hntechserver.question.dto.QuestionSimpleResponse
import org.springframework.stereotype.Component

@Component
class QuestionAlarmManager {

    data class QuestionsToSend(
        var newQuestions: MutableList<QuestionSimpleResponse> = mutableListOf(),
        var newCommentQuestions: MutableList<QuestionSimpleResponse> = mutableListOf()
    )

    private var newQuestionList = mutableListOf<QuestionSimpleResponse>()
    private var newCommentQuestionList = mutableListOf<QuestionSimpleResponse>()

    fun addNewQuestion(question: Question) {
        this.newQuestionList.add(QuestionSimpleResponse(question))
    }

    fun addNewCommentQuestion(question: Question) {
        this.newCommentQuestionList.add(QuestionSimpleResponse(question))
    }

    fun getQuestionListToSend(): QuestionsToSend {
        return QuestionsToSend(
            newQuestions = this.newQuestionList,
            newCommentQuestions = this.newCommentQuestionList
        )
    }

    fun clearQuestionList() {
        this.newQuestionList.clear()
        this.newCommentQuestionList.clear()
    }
}