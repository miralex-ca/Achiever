package com.muralex.achiever.data.models.usemodels

import com.muralex.achiever.data.models.datamodels.DataItem
import com.muralex.achiever.data.models.datamodels.Group


data class SearchItem(var title: String) {
    var description = ""
    var image = ""
    var id = ""
    var groupData: Group?  = null

    constructor(item: DataItem) : this ( item.title ) {
        description = item.text
        id = item.id
    }

}
