package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.usemodels.GroupData

class GroupDataListMapper : Function1<List<Group>, List<GroupData>> {
    override fun invoke(itemsList: List<Group>): List<GroupData> {
        return itemsList.map {
            GroupData(
                it.id,
                it.title,
                it.text,
                it
            )
        }
    }
}

class GroupDataMapper : Function1<Group?, GroupData> {

    override fun invoke(group: Group?): GroupData {

        return GroupData(
            group?.id,
            group?.title,
            group?.text,
            group
        )

    }

}

