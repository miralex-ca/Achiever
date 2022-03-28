package com.muralex.achiever.data.models.usemodels

import com.muralex.achiever.data.models.datamodels.Group

data class GroupData(
    val id: String?,
    var title: String?,
    var text: String?,
    var group: Group? = null

)
