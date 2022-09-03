package hntech.hntechserver.domain.question.model

import hntech.hntechserver.utils.BaseTimeEntity
import javax.persistence.*

@Entity
class Question(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    var id: Long? = null,

    var writer: String = "",
    var password: String = "",
    var FAQ: String = "",
    var status: String = "대기중", // 대기중, 처리중, 답변완료

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf(),

    var title: String = "",

    @Column(length = 750)
    var content: String = "",

    ) : BaseTimeEntity() {

    fun update(
        title: String? = null,
        content: String? = null,
        status: String? = null,
        FAQ: String? = null,
    ) {
        title?.let { this.title = title }
        content?.let { this.content = content }
        status?.let { this.status = status }
        FAQ?.let { this.FAQ = FAQ }
    }

    fun addComment(comment: Comment) { this.comments.add(comment) }
}




