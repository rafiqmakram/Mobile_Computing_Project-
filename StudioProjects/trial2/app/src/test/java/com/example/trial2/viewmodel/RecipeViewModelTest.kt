package com.example.trial2.viewmodel

import com.example.trial2.data.Recipe
import com.example.trial2.data.RecipeRepository
import com.example.trial2.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class RecipeViewModelTest {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var repository: RecipeRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()

        whenever(repository.getRecipes(any())).thenReturn(flowOf(emptyList()))
        viewModel = RecipeViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with correct credentials should be successful`() = runTest {

        val username = "testuser"
        val passwordHash = "password123"
        val user = User(userId = 1, username = username, passwordHash = passwordHash)
        whenever(repository.getUserByUsername(username)).thenReturn(user)


        var loginResult = false
        viewModel.login(username, passwordHash) { success ->
            loginResult = success
        }
        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(true, loginResult)
        assertNotNull(viewModel.currentUser.value)
        assertEquals(username, viewModel.currentUser.value?.username)
    }

    @Test
    fun `login with incorrect credentials should fail`() = runTest {

        val username = "testuser"
        val correctPasswordHash = "password123"
        val incorrectPasswordHash = "wrongpassword"
        val user = User(userId = 1, username = username, passwordHash = correctPasswordHash)
        whenever(repository.getUserByUsername(username)).thenReturn(user)


        var loginResult = true
        viewModel.login(username, incorrectPasswordHash) { success ->
            loginResult = success
        }
        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, loginResult)
        assertNull(viewModel.currentUser.value)
    }

    @Test
    fun `register with a new username should be successful`() = runTest {

        val username = "newuser"
        val passwordHash = "password123"
        val newUser = User(userId = 2, username = username, passwordHash = passwordHash)
        whenever(repository.getUserByUsername(username)).thenReturn(null)
            .thenReturn(newUser)


        var registerResult = false
        viewModel.register(username, passwordHash) { success ->
            registerResult = success
        }
        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(true, registerResult)
    }

    @Test
    fun `register with an existing username should fail`() = runTest {

        val username = "existinguser"
        val passwordHash = "password123"
        val existingUser = User(userId = 1, username = username, passwordHash = passwordHash)
        whenever(repository.getUserByUsername(username)).thenReturn(existingUser)


        var registerResult = true
        viewModel.register(username, passwordHash) { success ->
            registerResult = success
        }
        testDispatcher.scheduler.advanceUntilIdle()


        assertEquals(false, registerResult)
    }

    @Test
    fun `logout should clear current user and recipes`() = runTest {

        val user = User(userId = 1, username = "testuser", passwordHash = "password123")
        whenever(repository.getUserByUsername(user.username)).thenReturn(user)
        viewModel.login(user.username, user.passwordHash) { }
        testDispatcher.scheduler.advanceUntilIdle()
        assertNotNull(viewModel.currentUser.value)


        viewModel.logout()


        assertNull(viewModel.currentUser.value)
        assertEquals(emptyList<Recipe>(), viewModel.recipes.value)
    }
}
