package com.example.ficiapp.components.componentList

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ficiapp.R
import com.example.ficiapp.components.data.Component
import com.example.ficiapp.core.TAG
import kotlinx.android.synthetic.main.view_component.view.*

class ComponentListAdapter(
    private val fragment: Fragment
) : RecyclerView.Adapter<ComponentListAdapter.ViewHolder>() {

    var components = emptyList<Component>()
        set(value) {
            field = value
            notifyDataSetChanged();
        }

    private var onComponentClick: View.OnClickListener

    init {
        onComponentClick = View.OnClickListener { view ->
            val component = view.tag as Component
            fragment.findNavController()
                .navigate(
                    R.id.action_ComponentListFragment_to_ComponentEditFragment,
                    bundleOf("component" to component)
                )
        }
    }

    fun searchAndFilter(substring: String, inStock: Boolean, notInStock : Boolean): MutableList<Component> {
        val filteredList: MutableList<Component> = ArrayList()
        val substring = substring.toLowerCase().trim()
        for (component in components) {
            if (substring.isNotEmpty() && !component.name.toLowerCase().contains(substring))
                continue
            if(inStock && component.inStock!=inStock)
                continue
            if(notInStock && component.inStock==notInStock)
                continue
            filteredList.add(component)
        }
        return filteredList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_component, parent, false)
        Log.v(TAG, "onCreateViewHolder")
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(holder, position)
    }

    override fun getItemCount() = components.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.name
        private val quantity: TextView = view.quantity
        private val releaseDate: TextView = view.releaseDate
        private val starIc: ImageView = view.starRating
        private val componentPicture: ImageView = view.componentPicture
        private val latitude: TextView = view.lat
        private val longitude: TextView = view.lon

        fun bind(holder: ViewHolder, position: Int) {
            val component = components[position]

            with(holder) {
                itemView.tag = component
                name.text = component.name
                quantity.text = component.quantity.toString()
                releaseDate.text = component.releaseDate
                itemView.setOnClickListener(onComponentClick)
                if(component.inStock) starIc.visibility = View.VISIBLE
                else starIc.visibility = View.GONE
                if(component.picturePath.isNullOrEmpty())
                    componentPicture.visibility = View.GONE
                else {
                    componentPicture.setImageURI(Uri.parse(component.picturePath))
                    componentPicture.visibility = View.VISIBLE
                }
                if(component.latitude == null) component.latitude = 0f
                if(component.longitude == null) component.longitude = 0f
                latitude.text = "lat: ${component.latitude}"
                longitude.text = "long: ${component.longitude}"
            }
        }
    }
}