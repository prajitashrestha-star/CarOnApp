package com.example.caronapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.caronapp.model.UserModel
import com.example.caronapp.repository.UserRepo
import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq

class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepo: UserRepo

    @Mock
    private lateinit var allUsersObserver: Observer<List<UserModel>?>

    @Mock
    private lateinit var singleUserObserver: Observer<UserModel?>

    private lateinit var viewModel: UserViewModel
    private lateinit var closeable: AutoCloseable

    @Before
    fun setup() {
        closeable = MockitoAnnotations.openMocks(this)
        viewModel = UserViewModel(userRepo)
        viewModel.allUsers.observeForever(allUsersObserver)
        viewModel.users.observeForever(singleUserObserver)
    }

    @After
    fun tearDown() {
        viewModel.allUsers.removeObserver(allUsersObserver)
        viewModel.users.removeObserver(singleUserObserver)
        closeable.close()
    }

    @Test
    fun testLogin() {
        var successResult = false
        var messageResult = ""

        doAnswer {
            val cb = it.arguments[2] as (Boolean, String) -> Unit
            cb(true, "Success")
            null
        }.`when`(userRepo).login(eq("test@email.com"), eq("password"), any())

        viewModel.login("test@email.com", "password") { success, msg ->
            successResult = success
            messageResult = msg
        }

        verify(userRepo).login(eq("test@email.com"), eq("password"), any())
        assertEquals(true, successResult)
        assertEquals("Success", messageResult)
    }

    @Test
    fun testRegister() {
        var successResult = false
        var messageResult = ""
        var idResult = ""

        doAnswer {
            val cb = it.arguments[2] as (Boolean, String, String) -> Unit
            cb(true, "Registered", "uid123")
            null
        }.`when`(userRepo).register(eq("test@email.com"), eq("password"), any())

        viewModel.register("test@email.com", "password") { success, msg, id ->
            successResult = success
            messageResult = msg
            idResult = id
        }

        verify(userRepo).register(eq("test@email.com"), eq("password"), any())
        assertEquals(true, successResult)
        assertEquals("Registered", messageResult)
        assertEquals("uid123", idResult)
    }

    @Test
    fun testForgetPassword() {
        var successResult = false
        var messageResult = ""

        doAnswer {
            val cb = it.arguments[1] as (Boolean, String) -> Unit
            cb(true, "Email sent")
            null
        }.`when`(userRepo).forgetPassword(eq("test@email.com"), any())

        viewModel.forgetPassword("test@email.com") { success, msg ->
            successResult = success
            messageResult = msg
        }

        verify(userRepo).forgetPassword(eq("test@email.com"), any())
        assertEquals(true, successResult)
    }

    @Test
    fun testAddUserToDatabase() {
        val user = UserModel(userId = "uid123")
        var successResult = false

        doAnswer {
            val cb = it.arguments[2] as (Boolean, String) -> Unit
            cb(true, "Added")
            null
        }.`when`(userRepo).addUserToDatabase(eq("uid123"), eq(user), any())

        viewModel.addUserToDatabase("uid123", user) { success, _ ->
            successResult = success
        }

        verify(userRepo).addUserToDatabase(eq("uid123"), eq(user), any())
        assertEquals(true, successResult)
    }

    @Test
    fun testGetUserById_success() {
        val mockUser = UserModel(userId = "uid123")

        doAnswer {
            val cb = it.arguments[1] as (Boolean, UserModel?) -> Unit
            cb(true, mockUser)
            null
        }.`when`(userRepo).getUserById(eq("uid123"), any())

        viewModel.getUserById("uid123")

        verify(singleUserObserver).onChanged(mockUser)
        assertEquals(mockUser, viewModel.users.value)
    }

    @Test
    fun testGetAllUser_success() {
        val mockUsers = listOf(UserModel(userId = "uid123"))

        doAnswer {
            val cb = it.arguments[0] as (Boolean, List<UserModel>) -> Unit
            cb(true, mockUsers)
            null
        }.`when`(userRepo).getAllUser(any())

        viewModel.getAllUser()

        verify(allUsersObserver).onChanged(mockUsers)
        assertEquals(mockUsers, viewModel.allUsers.value)
    }

    @Test
    fun testGetCurrentUser() {
        val mockFirebaseUser: FirebaseUser = mock()

        `when`(userRepo.getCurrentUser()).thenReturn(mockFirebaseUser)

        val result = viewModel.getCurrentUser()
        assertEquals(mockFirebaseUser, result)
    }
}
