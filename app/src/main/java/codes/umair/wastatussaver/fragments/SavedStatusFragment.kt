package codes.umair.wastatussaver.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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


class SavedStatusFragment : Fragment(), StatusAdapter.OnItemClickListener {
    private val SAVED_WHATSAPP_STATUSES_LOCATION = "/Status Saver/"
    private var actionModeCallback: ActionModeCallback? = null
    private var actionMode: ActionMode? = null
    private var fileList = ArrayList<File>()

    lateinit var mAdapter: StatusAdapter
    lateinit var mAdView: AdView
    lateinit var recyclerView_saved: RecyclerView
    lateinit var ctx : MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        avedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_saved_status, container, false)
        val swipeRlayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRLayout)
        recyclerView_saved = view.findViewById<RecyclerView>(R.id.recyclerView_saved)

        ctx = (activity as MainActivity)
        MobileAds.initialize(context) {}
        mAdView = view.findViewById(R.id.adView1)
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
            Log.d("SavedStatusFragment", "setMenuVisibility: $menuVisible ")
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
        recyclerView_saved.layoutManager = GridLayoutManager(context, 2)
        recyclerView_saved.adapter = mAdapter
        actionModeCallback = ActionModeCallback()
    }

    private fun getFiles() {
        fileList = Utils().getListFiles(
            File(
                Environment.getExternalStorageDirectory()
                    .toString() + SAVED_WHATSAPP_STATUSES_LOCATION
            )
        )
    }

    override fun onItemLongClicked(position: Int) {
        enableActionMode(position);
    }

    override fun onItemClicked(position: Int) {
        if (actionMode == null) {
            val currentFile = fileList[position]
            val status = currentFile.absolutePath
            if (mAdapter.isVideo(currentFile)) {
                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra("videoPath", status)
                intent.putExtra("isSaved", true)
                startActivity(intent)
            } else {
                val intent = Intent(context, ImageActivity::class.java)
                intent.putExtra("ImagePath", status)
                intent.putExtra("isSaved", true)
                startActivity(intent)
            }
        } else {
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
//        if (mAdapter.getSelectedItemCount() != fileList.size) {
//            mAdapter.selectAll()
//            val count = mAdapter.getSelectedItemCount()
//            if (mAdapter.selectedFilesList.isEmpty()) {
//                actionMode!!.finish()
//            } else {
//                actionMode!!.title = count.toString()
//                actionMode!!.invalidate()
//            }
//        } else {
//            mAdapter.clearSelections()
//            actionMode?.finish()
//        }
//
//    }

    private fun deleteAllSelected() {
        val selectedItemPositions: ArrayList<File> = mAdapter.getSelectedItems()
        for (i in selectedItemPositions.indices.reversed()) {
            if (selectedItemPositions[i].exists()) {
                selectedItemPositions[i].delete()
                Utils().scanFile(activity!!,selectedItemPositions[i])
                mAdapter.removeItem(selectedItemPositions[i])
            }
        }
        Toast.makeText(activity, "${selectedItemPositions.size} files Deleted", Toast.LENGTH_LONG)
            .show()
        mAdapter.notifyDataSetChanged()
        actionMode = null
    }


    private fun shareAllSelected() {
        val selectedItemPositions: ArrayList<File> = mAdapter.getSelectedItems()
        val uris = ArrayList<Uri>()
        for (i in selectedItemPositions) {
            val contentUri: Uri = Utils().getFileUri(ctx,i)
            uris.add(contentUri)
        }
        Utils().shareMediaItem(ctx,uris)
    }


    inner class ActionModeCallback : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            mode.menuInflater.inflate(R.menu.action_menu, menu)
            mode.menu.getItem(1).isVisible = false
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
                R.id.action_delete_all -> {
                    // delete all the selected rows
                    MaDialog.Builder(activity)
                        .setTitleTextColor(Color.RED)
                        .setTitle("Delete?")
                        .setMessage("Are you sure you want to delete all selected items?")
                        .setPositiveButtonText("Yes")
                        .setPositiveButtonListener {
                            deleteAllSelected()
                            mode.finish()
                        }
                        .setNegativeButtonText("Cancel")
                        .setNegativeButtonListener { }
                        .build()

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