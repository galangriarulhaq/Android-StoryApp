package com.bangkit.storyapp.ui.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.bangkit.storyapp.DataDummy
import com.bangkit.storyapp.MainDispatcherRule
import com.bangkit.storyapp.data.local.room.StoryEntity
import com.bangkit.storyapp.data.repository.StoryRepository
import com.bangkit.storyapp.getOrAwaitValue
import com.bangkit.storyapp.ui.adapter.StoryAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.Dispatchers
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val data: PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = data

        Mockito.`when`(storyRepository.getAllStories("token")).thenReturn(expectedStories)

        val homeViewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryEntity> = homeViewModel.stories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])

    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<StoryEntity>>()
        expectedStories.value = data

        Mockito.`when`(storyRepository.getAllStories("token")).thenReturn(expectedStories)

        val homeViewModel = HomeViewModel(storyRepository)
        val actualStories: PagingData<StoryEntity> = homeViewModel.stories("token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        Assert.assertEquals(0, differ.snapshot().size)

    }

}

class StoryPagingSource : PagingSource<Int, StoryEntity>() {
    companion object {
        fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryEntity>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryEntity> {
        return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}