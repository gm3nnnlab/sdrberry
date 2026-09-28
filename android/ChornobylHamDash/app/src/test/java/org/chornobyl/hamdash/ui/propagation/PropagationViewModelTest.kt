package org.chornobyl.hamdash.ui.propagation

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.chornobyl.hamdash.data.repository.RadioDataRepository
import org.chornobyl.hamdash.database.entity.BandEntity
import org.chornobyl.hamdash.database.entity.DigitalModeEntity
import org.chornobyl.hamdash.database.entity.PropagationEntity
import org.chornobyl.hamdash.database.entity.RepeaterEntity
import org.chornobyl.hamdash.database.entity.TalkgroupEntity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PropagationViewModelTest {

    private class FakeRepository : RadioDataRepository {
        var propagationRefreshes = 0
        var nextResult = CompletableDeferred(true)

        override suspend fun refreshPropagation(): Boolean {
            propagationRefreshes++
            return nextResult.await()
        }

        override fun observeRepeaters(): Flow<List<RepeaterEntity>> = emptyFlow()
        override fun observeBands(): Flow<List<BandEntity>> = emptyFlow()
        override fun observeDigitalModes(): Flow<List<DigitalModeEntity>> = emptyFlow()
        override fun observeTalkgroups(): Flow<List<TalkgroupEntity>> = emptyFlow()
        override fun searchTalkgroups(query: String): Flow<List<TalkgroupEntity>> = emptyFlow()
        override fun observePropagation(): Flow<PropagationEntity?> = emptyFlow()
        override fun observeLastSync(): Flow<Long?> = emptyFlow()
        override suspend fun ensureSeeded() = Unit
        override suspend fun refresh(): Boolean = false
        override suspend fun toggleFavorite(repeaterId: String) = Unit
        override suspend fun clearCache() = Unit
    }

    private val repository = FakeRepository()
    private var online = true

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = PropagationViewModel(repository, isOnline = { online })

    @Test
    fun `a successful refresh fetches once and clears the message`() {
        val vm = viewModel()
        vm.refresh()
        assertEquals(1, repository.propagationRefreshes)
        assertFalse(vm.refreshState.value.inProgress)
        assertNull(vm.refreshState.value.message)
    }

    @Test
    fun `a failed refresh keeps the stored reading and says so`() {
        repository.nextResult = CompletableDeferred(false)
        val vm = viewModel()
        vm.refresh()
        assertFalse(vm.refreshState.value.inProgress)
        assertEquals(PropagationViewModel.MESSAGE_FAILED, vm.refreshState.value.message)
    }

    @Test
    fun `refreshing offline does not call NOAA`() {
        online = false
        val vm = viewModel()
        vm.refresh()
        assertEquals(0, repository.propagationRefreshes)
        assertEquals(PropagationViewModel.MESSAGE_OFFLINE, vm.refreshState.value.message)
    }

    @Test
    fun `taps during a refresh are ignored`() {
        repository.nextResult = CompletableDeferred()
        val vm = viewModel()
        vm.refresh()
        assertTrue(vm.refreshState.value.inProgress)
        vm.refresh()
        assertEquals(1, repository.propagationRefreshes)

        repository.nextResult.complete(true)
        assertFalse(vm.refreshState.value.inProgress)
    }
}
