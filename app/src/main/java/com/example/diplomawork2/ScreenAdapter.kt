package com.example.diplomawork2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScreenAdapter(var screens: List<Screen>, var context: Context) : RecyclerView.Adapter<ScreenAdapter.MyViewHolder>() {


    class MyViewHolder(view: View):RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.item_list_image)
        val title: TextView = view.findViewById(R.id.item_list_title)
        val desc:TextView = view.findViewById(R.id.item_list_desc)
        val playButton: Button = view.findViewById(R.id.item_list_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.screen_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return screens.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = screens[position].title
        holder.desc.text = screens[position].desc

        val  imageId = context.resources.getIdentifier(
            screens[position].image,
            "drawable",
            context.packageName
        )

        holder.playButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)

            //i'm transmitting image from one activity to another activity
            intent.putExtra("screenImage", screens[imageId].image)

            context.startActivity(intent)
        }

        holder.image.setImageResource(imageId)
    }
}