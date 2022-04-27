package com.muralex.achiever.data.models.datamodels

import androidx.room.*
import com.muralex.achiever.presentation.utils.Constants

@Entity(tableName = "group_table")
data class Group(
    @PrimaryKey
    @ColumnInfo(name = "group_id")
    var id: String,
    @ColumnInfo(name = "group_title")
    var title: String,
    @ColumnInfo(name = "group_text")
    var text: String,
    @ColumnInfo(name = "group_image")
    var image: String?,
    @ColumnInfo(name = "group_favorite")
    var favorite: Int = 0,
    @ColumnInfo(name = "group_archived")
    var archived: Int = 0,
    @ColumnInfo(name = "group_detail")
    var displayDetail: Int = 0,
    @ColumnInfo(name = "group_progress_display")
    var displayProgress: Int = 0,
    @ColumnInfo(name = "group_list_layout")
    var listLayout: Int = 0,
    @ColumnInfo(name = "group_sort")
    var sort: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "group_created")
    var createdAt: Long = System.currentTimeMillis()

)

data class GroupWithDataItems (
    @Embedded
    val group: Group?,
    @Relation(
        parentColumn = "group_id",
        entityColumn = "group_id"
    )
    val items: List<DataItem>
)

fun Group.showProgress() = this.displayProgress == 0
fun Group.showDetails() = this.displayDetail == 0