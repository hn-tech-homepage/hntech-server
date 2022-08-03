package hntech.hntechserver.category

const val CATEGORY_NOT_FOUND = "해당 카테고리가 존재하지 않습니다."
const val DUPLICATE_CATEGORY_NAME = "해당 이름의 카테고리가 이미 존재합니다."

class CategoryException(message: String) : RuntimeException(message)