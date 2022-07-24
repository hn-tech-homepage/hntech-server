package hntech.hntechserver.category

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/category")
class CategoryController(private val categoryService: CategoryService) {

    /**
     * 카테고리 생성
     */
    // 제품 카테고리 생성 : form-data 방식으로 file value에는 이미지를, text value에는 이름을 넣는다.
    @PostMapping("/item")
    fun createItemCategory(@ModelAttribute form: CreateItemCategoryRequest): ItemCategoryResponse
    = categoryService.createItemCategory(form)

    // 자료실 카테고리 생성
    @PostMapping("/archive")
    fun createArchiveCategory(@RequestBody form: CreateArchiveCategoryRequest): ArchiveCategoryResponse
    = categoryService.createArchiveCategory(form)

    /**
     * 카테고리 조회
     */
    @GetMapping("/item")
    fun getAllItemCategories() = categoryService.getAllItemCategories()

    @GetMapping("/archive")
    fun getAllArchiveCategories() = categoryService.getAllArchiveCategories()

//    /**
//     * 카테고리 수정
//     */
//    @PutMapping("/item")
//    fun updateItemCategory()


}