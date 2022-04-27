package com.muralex.achiever.data.models.mappers

import com.muralex.achiever.data.models.datamodels.Group
import com.muralex.achiever.data.models.datamodels.GroupWithDataItems
import com.muralex.achiever.data.models.usemodels.GroupData

class GroupDataListMapper : Function1<List<GroupWithDataItems>, List<GroupData>> {
    override fun invoke(itemsList: List<GroupWithDataItems>): List<GroupData> {

        return itemsList.map {
           val group = it.group

            GroupData(
                group?.id,
                group?.title,
                group?.text,
                group
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

