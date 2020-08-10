package codes.umair.wastatussaver.fragments

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import codes.umair.wastatussaver.R
import codes.umair.wastatussaver.Utils
import codes.umair.wastatussaver.adapters.StatusAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.io.File


class SavedStatusFragment : Fragment() {
    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        avedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_saved_status, container, false)
        val recyclerView_saved = view.findViewById<RecyclerView>(R.id.recyclerView_saved)
        val swipeRlayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRLayout)

        MobileAds.initialize(context) {}
        mAdView = view.findViewById(R.id.adView1)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        recyclerView_saved.adapter = StatusAdapter(
            context!!,
            Utils().getListFiles(
                File(
                    Environment.getExternalStorageDirectory()
                        .toString() + SAVED_WHATSAPP_STATUSES_LOCATION
                )
            ),
            true
        )
        recyclerView_saved.layoutManager = GridLayoutManager(context, 2)

        swipeRlayout.setOnRefreshListener {
            recyclerView_saved.adapter = StatusAdapter(
                context!!,
                Utils().getListFiles(
                    File(
                        Environment.getExternalStorageDirectory()
                            .toString() + SAVED_WHATSAPP_STATUSES_LOCATION
                    )
                ),
                true
            )
            swipeRlayout.isRefreshing = false
        }
        return view
    }


    companion object {
        private const val SAVED_WHATSAPP_STATUSES_LOCATION = "/Status Saver/"
        const val TAG = "Home"
    }


}