package com.example.sampleapp

import android.util.Patterns
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sampleapp.db.User
import com.example.sampleapp.db.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository): ViewModel(), Observable {

    val users = repository.users
    private var isUpdateOrDelete = false
    private lateinit var userToUpdateOrDelete:User

    @Bindable
    val inputName = MutableLiveData<String?>()
    @Bindable
    val inputEmail = MutableLiveData<String?>()
    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()
    @Bindable
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message: LiveData<Event<String>>
    get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "save"
        clearAllOrDeleteButtonText.value ="clear all"
    }

    fun saveOrUpdate(){

        if(inputName.value==null){
            statusMessage.value = Event("please enter user's name")
        }
        else if(inputEmail.value==null){
            statusMessage.value = Event("please enter user's email")
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()){
            statusMessage.value = Event("please enter a correct email")
        }
        else{
            if (isUpdateOrDelete){
                userToUpdateOrDelete.name  = inputName.value!!
                userToUpdateOrDelete.email  = inputEmail.value!!
                update(userToUpdateOrDelete)
            }
            else{
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(User(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }
    }

    fun clearAllOrDelete(){
        if(isUpdateOrDelete){
            delete(userToUpdateOrDelete)
        }
        else{
            clearAll()
        }
    }

    private fun insert(user: User) = viewModelScope.launch {
        val newRowId = repository.insert(user)
        if(newRowId>-1){
            statusMessage.value = Event("User inserted in row $newRowId")
        }
        else{
            statusMessage.value = Event("Error occurred!!")
        }
       }

    fun update(user: User) = viewModelScope.launch {
        val noOfRows = repository.update(user)
        if(noOfRows>0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "save"
            clearAllOrDeleteButtonText.value = "clear all"
            statusMessage.value = Event("$noOfRows row updated")
        }
        else{
            statusMessage.value = Event("Error occurred!!")
        }
    }

    fun delete(user: User) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(user)
        if(noOfRowsDeleted > 0) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "save"
            clearAllOrDeleteButtonText.value = "clear all"
            statusMessage.value = Event("$noOfRowsDeleted row deleted")
        }else{
            statusMessage.value = Event("Error occurred!!")
        }
    }

    private fun clearAll() = viewModelScope.launch{
        val noOfRowsDeleted = repository.deleteAll()
        if(noOfRowsDeleted > 0) {
            statusMessage.value = Event("$noOfRowsDeleted users deleted")
        }
        else{
            statusMessage.value = Event("Error occurred!!")
        }
    }

    fun initUpdateAndDelete(user: User){
        inputName.value = user.name
        inputEmail.value = user.email
        isUpdateOrDelete  = true
        userToUpdateOrDelete = user
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }


}