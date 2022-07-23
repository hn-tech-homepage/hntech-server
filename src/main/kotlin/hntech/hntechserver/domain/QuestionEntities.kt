package hntech.hntechserver.domain

import javax.persistence.*



@Entity
class Question(
    @Id @GeneratedValue
    @Column(name = "question_id")
    var id: Long? = null,

    var writer: String = "",
    var password: Int = 0,
    var status: String = "", // 대기, 진행중, 완료

    @OneToMany(mappedBy = "question", cascade = [CascadeType.ALL])
    var comments: MutableSet<Comment> = HashSet(),

    var title: String = "",
    var content: String = "",
    var viewCount: Int = 0,
) : BaseTimeEntity()

@Entity
class Comment (
    @Id @GeneratedValue
    @Column(name = "comment_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    var question: Question,

    var isAdmin: Boolean,
    var sequence: Int = 0,

    // 중복되는 부분
    var content: String = "",
) : BaseTimeEntity()


