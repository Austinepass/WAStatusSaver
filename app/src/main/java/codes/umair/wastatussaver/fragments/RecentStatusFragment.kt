package codes.umair.wastatussaver.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.telecom.VideoProfile.isVideo
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import codes.umair.wastatussaver.R
import codes.umair.wastatussaver.Utils
import codes.umair.wastatussaver.activities.ImageActivity
import codes.umair.wastatussaver.activities.MainActivity
import codes.umair.wastatussaver.activities.VideoActivity
import codes.umair.wastatussaver.adapters.StatusAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import umairayub.madialog.MaDialog
import java.io.File


class RecentStatusFragment : Fragment(), StatusAdapter.OnItemClickListener {

    lateinit var mAdView: AdView
    lateinit var recyclerView_recent: RecyclerView
    lateinit var mAdapter: StatusAdapter
    private var fileList = ArrayList<File>()
    private val WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses"
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private lateinit var ctx : MainActivity

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.framgent_recent_status, container, false)

        ctx = (activity as MainActivity)
        
        val swipeRlayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRLayout)
        recyclerView_recent = view.findViewById<RecyclerView>(R.id.recyclerView_recent)

        MobileAds.initialize(context) {}
        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        setupRecyclerView()

        swipeRlayout.setOnRefreshListener {
            actionMode?.finish()
            setupRecyclerView()
            swipeRlayout.isRefreshing = false
        }
        return view
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible){

        }else{
            Log.d("RecentStatusFragment", "setMenuVisibility: $menuVisible ")
            actionMode?.finish()

        }
    }


    override fun onStop() {
        super.onStop()
        actionMode?.finish()
    }

    override fun onPause() {
        super.onPause()
        actionMode?.finish()
    }

    override fun onResume() {
        super.onResume()
        actionMode?.finish()
        setupRecyclerView()
    }
    private fun setupRecyclerView() {
        getFiles()
        mAdapter = StatusAdapter(context!!, fileList, true, this)
        recyclerView_recent.layoutManager = GridLayoutManager(context, 2)
        recyclerView_recent.adapter = mAdapter
        actionModeCallback = ActionModeCallback()
    }

    private fun getFiles() {
        fileList = Utils().getListFiles(
            File(
                Environment.getExternalStorageDirectory().toString() + WHATSAPP_STATUSES_LOCATION
            )
        )
    }

    override fun onItemLongClicked(position: Int) {
        enableActionMode(position);
    }
    override fun onItemClicked(position: Int) {
        if (actionMode == null){
            val currentFile = fileList[position]
            val status = currentFile.absolutePath
            if (mAdapter.isVideo(currentFile)) {
                val intent = Intent(activity, VideoActivity::class.java)
                intent.putExtra("videoPath", status)
                intent.putExtra("isSaved", false)
                startActivity(intent)
            } else {
                val intent = Intent(activity, ImageActivity::class.java)
                intent.putExtra("ImagePath", status)
                intent.putExtra("isSaved", false)
                startActivity(intent)
            }
        }else{
            toggleSelection(position);
        }

    }
    private fun enableActionMode(position: Int) {
        if (actionMode == null) {
            actionMode = ctx.startSupportActionMode(actionModeCallback!!)
            toggleSelection(position)

        }
    }

    private fun toggleSelection(position: Int) {
        mAdapter.toggleSelection(position)
        val count: Int = mAdapter.getSelectedItemCount()
        if (count == 0) {
            actionMode!!.finish()
            actionMode = null
        } else {
            actionMode!!.title = count.toString()
            actionMode!!.invalidate()
        }
    }

//    private fun selectAll() {
//        if (mAdapter.getSelectedItemCount() != fileList.size){
//            mAdapter.selectAll()
//            val count = mAdapter.getSelectedItemCount()
//            if (mAdapter.selectedFilesList.isEmpty()) {
//                actionMode!!.finish()
//            } else {
//                actionMode!!.title = count.toString()
//                actionMode!!.invalidate()
//            }
//        }else{
//            mAdapter.clearSelections()
//            actionMode?.finish()
//        }
//
//    }
    private fun saveAllSelected() {
        val selectedItemPositions: ArrayList<File> = mAdapter.getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            if (selectedItemPositions[i].exists()){
                 Utils().downloadMediaItem(ctx,selectedItemPositions[i]) 
            }
        }
        mAdapter.notifyDataSetChanged()
        actionMode = null
    }
    private fun shareAllSelected(){
        val selectedItemPositions: ArrayList<File> = mAdapter.getSelectedItems()
        val uris = ArrayList<Uri>()
        for (i in selectedItemPositions){
            val contentUri: Uri = Utils().getFileUri(ctx,i)
            uris.add(contentUri)
        }
        Utils().shareMediaItem(ctx,uris)
    }


    inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            mode.menuInflater.inflate(R.menu.action_menu, menu)
            menu?.getItem(2)?.isVisible = false
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }

        override fun onActionItemClicked(
            mode: ActionMode,
            item: MenuItem
        ): Boolean {
            Log.d("API123", "here")
            return when (item.itemId) {
                R.id.action_share_all -> {
                    // delete all the selected rows
                    shareAllSelected()
                    mode.finish()
                    true
                }
                R.id.action_save_all -> {
                    // delete all the selected rows
                    saveAllSelected()
                    mode.finish()
                    true
                }

//                R.id.action_select_all -> {
//                    item.icon = R.drawable.ic_deselect_all
//                    selectAll()
//                    true
//                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            mAdapter.clearSelections()
            actionMode = null
        }
    }


}
