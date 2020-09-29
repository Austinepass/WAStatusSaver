package codes.umair.wastatussaver.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import codes.umair.wastatussaver.R
import codes.umair.wastatussaver.activities.ImageActivity
import codes.umair.wastatussaver.activities.VideoActivity
import com.bumptech.glide.Glide
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class StatusAdapter(private val activity: Context, private val filesList: ArrayList<File>, private val saved: Boolean, private val listener: OnItemClickListener) : RecyclerView.Adapter<StatusAdapter.FileHolder>() {

    val selectedFilesList: ArrayList<File> = ArrayList()
    private var mListener: OnItemClickListener? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
        return FileHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false),listener)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: FileHolder, position: Int) {
        when (holder) {
            is FileHolder -> {
                holder.bind(filesList[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return filesList.size
    }

    private fun isSelected(file: File): Boolean{
        return selectedFilesList.contains(file)
    }



    interface OnItemClickListener {
        fun onItemLongClicked(position: Int)
        fun onItemClicked(position: Int)
    }


    fun toggleSelection(pos: Int) {
        val currentSelectedIndex = filesList[pos]
        if (selectedFilesList.contains(currentSelectedIndex)) {
            selectedFilesList.remove(currentSelectedIndex)
        } else {
            selectedFilesList.add(currentSelectedIndex)
        }
        notifyItemChanged(pos)
    }

//    fun selectAll() {
//        for (i in 0 until filesList.size){
//            if (!selectedFilesList.contains(filesList[i])){
//                selectedFilesList.add(filesList[i])
//                notifyDataSetChanged()
//            }
//
//        }
//    }

    fun clearSelections() {
        selectedFilesList.clear();
        notifyDataSetChanged();
    }

    fun getSelectedItemCount(): Int {
        return selectedFilesList.size
    }

    fun getSelectedItems(): ArrayList<File> {
        return selectedFilesList;
    }

    fun removeItem(file: File) {
        filesList.remove(file);
        notifyDataSetChanged()
    }

    fun isVideo(currentFile: File):Boolean{
        return currentFile.extension == "mp4"
    }

    inner class FileHolder constructor(itemView: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        var statusItemView: ImageView = itemView.findViewById<View>(R.id.videoView) as ImageView
        var statusItemType: TextView = itemView.findViewById<View>(R.id.itemType) as TextView
        var activity: Context = itemView.context
        var listener = listener


        fun bind(currentFile: File) {
            val status = currentFile.absolutePath

            if (isVideo(currentFile)) {
                statusItemType.text = "Video"
            } else {
                statusItemType.text = "Image"
            }

            Glide.with(activity)
                .load(status)
                .into(statusItemView)

            if (isSelected(currentFile)){
                itemView.scaleX = 0.9f;
                itemView.scaleY = 0.9f;
                statusItemView.alpha = 0.5f
            }else{
                statusItemView.alpha = 1f
                itemView.scaleX = 1f;
                itemView.scaleY = 1f;
            }
            itemView.setOnClickListener {
                listener.onItemClicked(adapterPosition)
            }
            itemView.setOnLongClickListener(this)
        }

        override fun onLongClick(v: View): Boolean {
            listener.onItemLongClicked(adapterPosition);
            return true;
        }
    }


}