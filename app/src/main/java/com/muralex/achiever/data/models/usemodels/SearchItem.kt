package com.muralex.achiever.data.models.usemodels

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.presentation.utils.Constants.ITEM_STATUS


data class SearchItem(var title: String) {
    var description = ""
    var image = ""
    var id = ""
    var data: DataItem? = null
    var groupData: Group?  = null
    var displayStatus: String = ITEM_STATUS[0]

    constructor(item: DataItem) : this ( item.title ) {
        description = item.text
        id = item.id
    }

}
