//package hntech.hntechserver.domain.archive
//
//import com.querydsl.jpa.impl.JPAQueryFactory
//import hntech.hntechserver.domain.archive.QArchive.archive
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageImpl
//import org.springframework.data.domain.Pageable
//import org.springframework.stereotype.Repository
//
//@Repository
//class QArchiveRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory): QArchiveRepository {
//
//    // 동적 쿼리 사용 함수
//    fun keywordContains(keyword: String?) =
//        keyword?.let { archive.title.contains(it).or(archive.content.contains(it)) }
//    fun categoryNameEq(categoryName: String?) =
//        categoryName?.let { archive.category.categoryName.eq(it) }
//
//    override fun searchArchive(pageable: Pageable, categoryName: String?, keyword: String?): Page<Archive> {
//        val result: List<Archive> = jpaQueryFactory
//            .selectFrom(archive)
//            .where(
//                categoryNameEq(categoryName),
//                keywordContains(keyword),
//                archive.notice.eq("false")
//            )
//            .orderBy(archive.id.desc())
//            .offset(pageable.offset)
//            .limit(pageable.pageSize.toLong())
//            .fetch()
//
//        val totalCount = jpaQueryFactory
//            .select(archive.count())
//            .from(archive)
//            .where(
//                categoryNameEq(categoryName),
//                keywordContains(keyword),
//                archive.notice.eq("false")
//            )
//            .fetchOne()
//
//        return PageImpl(result, pageable, totalCount!!)
//    }
//}