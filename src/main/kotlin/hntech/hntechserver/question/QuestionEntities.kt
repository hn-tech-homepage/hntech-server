package hntech.hntechserver.question

import hntech.hntechserver.utils.BaseTimeEntity
import javax.persistence.*

@Entity
class Question(
    @Id @GeneratedValue
    @Column(name = "question_id")
    var id: Long? = null,

    var writer: String = "",
    var password: String = "",
    var isFAQ: String = "false",

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    var comments: MutableList<Comment> = mutableListOf(),

    var title: String = "",
    var content: String = "",
) : BaseTimeEntity() {

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
    fun update(isFAQ: String) {
        this.isFAQ = isFAQ
    }
    fun addComment(comment: Comment) { this.comments.add(comment) }
}

@Entity
class Comment (
    @Id @GeneratedValue
    @Column(name = "comment_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    var question: Question,

    var writer: String = "",
    var sequence: Int = 0,

    // 중복되는 부분
    var content: String = "",
) : BaseTimeEntity() {

    fun update(content: String) {
        this.content = content
    }
}


