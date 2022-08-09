package hntech.hntechserver.archive

const val ARCHIVE_NOT_FOUND = "해당 자료글이 존재하지 않습니다."

class ArchiveException(message: String): RuntimeException(message)