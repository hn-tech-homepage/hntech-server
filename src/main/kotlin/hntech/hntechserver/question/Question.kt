package hntech.hntechserver.question

import hntech.hntechserver.comment.Comment
import hntech.hntechserver.utils.BaseTimeEntity
import javax.persistence.*

@Entity
class Question(
    @Id @GeneratedValue
    @Column(name = "question_id")
    var id: Long? = null,

    var writer: String = "",
    var password: String = "",
    var FAQ: Boolean = false,

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    var comments: MutableList<Comment> = mutableListOf(),

    var title: String = "",
    var content: String = "",
) : BaseTimeEntity() {

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
    fun update(FAQ: Boolean) {
        this.FAQ = FAQ
    }
    fun addComment(comment: hntech.hntechserver.comment.Comment) { this.comments.add(comment) }
}




