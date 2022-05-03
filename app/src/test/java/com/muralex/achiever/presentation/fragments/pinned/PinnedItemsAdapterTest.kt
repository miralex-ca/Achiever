package com.muralex.achiever.presentation.fragments.pinned

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.usemodels.PinnedItem
import com.muralex.achiever.presentation.utils.Constants.Action
import com.muralex.achiever.utilities.TestApplication
import com.muralex.achiever.utilities.TestDoubles
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class PinnedItemsAdapterTest {

    private lateinit var context: Context
    private val pinnedListAdapter = PinnedItemsListAdapter()
    private lateinit var frameLayout: FrameLayout
    private var testItemClickListener: ((Action, PinnedItem) -> Unit)? = mock()
    private val testPinnedItem = TestDoubles.testPinnedItem

    @Before
    fun setup() {
        val application: Application = ApplicationProvider.getApplicationContext()
        context = application
        frameLayout = FrameLayout(context)
    }

    @Test
    fun onCreateViewHolder_returnViewHolder() {
        val viewHolder = pinnedListAdapter.onCreateViewHolder(frameLayout,1)
        assertThat(viewHolder).isInstanceOf(PinnedItemsListAdapter.ViewHolder::class.java)
    }

    @Test
    fun bindViewHolder_defaultItem_InitialState() {
        val holder =  PinnedItemsListAdapter.ViewHolder.from(frameLayout)
        holder.bind(testPinnedItem , testItemClickListener)
        assertThat(holder.binding.tvNoteTitle.text).isEqualTo(testPinnedItem.title)
        assertThat(holder.binding.tvGroupName.text).isEqualTo(testPinnedItem.groupName)
        assertThat(holder.binding.ivPinned.visibility).isEqualTo(View.VISIBLE)
    }

    @Test
    fun bindViewHolder_clickCard_ActionClick () {
        val holder =  PinnedItemsListAdapter.ViewHolder.from(frameLayout)
        holder.bind( testPinnedItem, testItemClickListener)
        holder.binding.itemCardWrap.performClick()
        verify(testItemClickListener, times(1))?.invoke(Action.Click , testPinnedItem)
    }

    @Test
    fun bindViewHolder_longClickCard_ActionClick () {
        val holder =  PinnedItemsListAdapter.ViewHolder.from(frameLayout)
        holder.bind( testPinnedItem , testItemClickListener)
        holder.binding.itemCardWrap.performLongClick()
        verify(testItemClickListener, times(1))?.invoke(Action.LongClick , testPinnedItem )
    }

    @Test
    fun bindViewHolder_clickListName_ActionOpenGroup () {
        val holder =  PinnedItemsListAdapter.ViewHolder.from(frameLayout)
        holder.bind( testPinnedItem, testItemClickListener)
        holder.binding.llSection.performClick()
        verify(testItemClickListener, times(1))?.invoke(Action.OpenGroup , testPinnedItem)
    }

}