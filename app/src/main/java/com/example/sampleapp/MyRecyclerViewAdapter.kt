package com.example.sampleapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sampleapp.databinding.ListItemBinding
import com.example.sampleapp.db.User

class MyRecyclerViewAdapter (private val clickListener: (User)->Unit)
    : RecyclerView.Adapter<MyViewHolder>(){

    private val userList =  ArrayList<User>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding:ListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(userList[position],clickListener)
    }

    fun setList(users: List<User>){
        userList.clear()
        userList.addAll(users)
    }

    override fun getItemCount(): Int {
        return userList.size
    }


}


class MyViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(user: User, clickListener: (User)->Unit){
        binding.nameTextView.text = user.name
        binding.emailTextView.text = user.email
        binding.itemListLayout.setOnClickListener{
            clickListener(user)
        }
    }
}