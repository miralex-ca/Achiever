package com.muralex.achiever.presentation.fragments.home

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.muralex.achiever.data.models.usemodels.GroupData
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
class HomeListAdapterTest {

    private lateinit var context: Context
    private val homeAdapter = HomeListAdapter()
    private lateinit var frameLayout: FrameLayout
    private var testItemClickListener: ((Action, GroupData) -> Unit)? = mock()

    @Before
    fun setup() {
        val application: Application = ApplicationProvider.getApplicationContext()
        context = application
        frameLayout = FrameLayout(context)
    }

    @Test
    fun onCreateViewHolder_returnViewHolder() {
        val viewHolder = homeAdapter.onCreateViewHolder(frameLayout,1)
        assertThat(viewHolder).isInstanceOf(HomeListAdapter.ViewHolder::class.java)
    }

    @Test
    fun bindViewHolder_defaultList_InitialState() {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        holder.bind( testGroupData , testItemClickListener)
        assertThat(holder.binding.cardTodayIndicator.visibility).isEqualTo(View.GONE)
        assertThat(holder.binding.cardTodayIndicator.visibility).isEqualTo(View.GONE)
        assertThat(holder.binding.cardUrgentIndicator.visibility).isEqualTo(View.GONE)
        assertThat(holder.binding.progressBar.progress).isEqualTo(0)
    }

    @Test
    fun bindViewHolder_ListHasDueToday_TodayIndicatorIsVisible() {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        val group = testGroupData.copy()
        group.todayItems = 2
        holder.bind( group , testItemClickListener)
        assertThat(holder.binding.cardTodayIndicator.visibility).isEqualTo(View.VISIBLE)
    }

    @Test
    fun bindViewHolder_ListHasUrgent_UrgentIndicatorIsVisible() {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        val group = testGroupData.copy()
        group.urgentItems = 2
        holder.bind( group, testItemClickListener)
        assertThat(holder.binding.cardUrgentIndicator.visibility).isEqualTo(View.VISIBLE)
    }

    @Test
    fun bindViewHolder_ListHasProgress_ProgressWithValue() {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        val group = testGroupData.copy()
        group.progress = 50
        holder.bind( group , testItemClickListener)
        assertThat(holder.binding.progressBar.progress).isEqualTo(50)
    }

    @Test
    fun bindViewHolder_hideProgress_ProgressIsGone() {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        testGroupData.group?.displayProgress = 1
        holder.bind( testGroupData , testItemClickListener)
        assertThat(holder.binding.progressBar.visibility).isEqualTo(View.GONE)
    }

    @Test
    fun bindViewHolder_onClick () {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        holder.bind( testGroupData , testItemClickListener)
        holder.binding.homeCardWrap.performClick()
        verify(testItemClickListener, times(1))?.invoke(Action.Click , testGroupData )
    }

    @Test
    fun bindViewHolder_onLongClick () {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        holder.bind( testGroupData , testItemClickListener)
        holder.binding.homeCardWrap.performLongClick()
        verify(testItemClickListener, times(1))?.invoke(Action.LongClick , testGroupData )
    }

    @Test
    fun bindViewHolder_onMenuClick () {
        val holder =  HomeListAdapter.ViewHolder.from(frameLayout)
        holder.bind( testGroupData , testItemClickListener)
        holder.binding.ivGroupMenu.performClick()
        verify(testItemClickListener, times(1))?.invoke(Action.MenuClick , testGroupData )
    }


    companion object {
        val testGroupData = TestDoubles.testGroupData
    }


}