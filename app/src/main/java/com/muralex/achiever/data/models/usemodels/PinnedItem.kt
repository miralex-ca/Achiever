package com.muralex.achiever.data.models.usemodels

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group

data class PinnedItem(
    val id: String,
    val title: String,
    val groupImage: String,
    val groupName: String,
    val statusSort: Int,
    val displayStatus: String,
    val data: DataItem,
    val groupData: Group
)
