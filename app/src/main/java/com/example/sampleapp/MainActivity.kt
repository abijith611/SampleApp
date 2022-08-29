 package com.example.sampleapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sampleapp.databinding.ActivityMainBinding
import com.example.sampleapp.db.User
import com.example.sampleapp.db.UserDatabase
import com.example.sampleapp.db.UserRepository

 class MainActivity : AppCompatActivity() {

     private  lateinit var binding: ActivityMainBinding
     private  lateinit var userViewModel: UserViewModel
     private lateinit var adapter:MyRecyclerViewAdapter

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val dao = UserDatabase.getInstance(application).userDAO
        val repository = UserRepository(dao)
        val factory = UserViewModelFactory(repository)
        userViewModel = ViewModelProvider(this,factory)[UserViewModel::class.java]
        binding.myViewModel = userViewModel
        binding.lifecycleOwner = this
        initRecyclerView()

         userViewModel.message.observe(this){
             it.getContentIfNotHandled()?.let{
                 Toast.makeText(this, it, Toast.LENGTH_LONG).show()
             }
         }
    }

     private fun initRecyclerView(){
         binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
         adapter = MyRecyclerViewAdapter{
                 selectedItem: User -> listItemClicked(selectedItem)
         }
         binding.userRecyclerView.adapter = adapter
         displayUsersList()
     }

     private fun displayUsersList(){
        userViewModel.users.observe(this) {
            Log.i("MYTAG", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        }
     }

     private fun listItemClicked(user: User){
         //Toast.makeText(this, "selected name is ${user.name}", Toast.LENGTH_LONG).show()
         userViewModel.initUpdateAndDelete(user)
     }

}