package hntech.hntechserver.domain.archive

const val ARCHIVE_NOT_FOUND = "해당 자료글이 존재하지 않습니다."
const val OVER_MAX_NOTICE_NUM = "생성 가능한 공지사항 개수를 초과했습니다."

class ArchiveException(message: String): RuntimeException(message)