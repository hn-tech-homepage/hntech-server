package hntech.hntechserver.file

/**
 * errors
 */
const val FILE_SAVING_ERROR = "파일 저장 오류 : savedFile 호출 오류 [후보 = 확장자 추출 실패, 서버 파일 저장 실패]"
const val FILE_FIND_ERROR = "파일 조회 오류 : 해당 파일을 찾을 수 없음"

class FileException(message: String) : RuntimeException(message)