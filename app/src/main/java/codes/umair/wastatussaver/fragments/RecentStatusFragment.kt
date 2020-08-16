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


class RecentStatusFragment : Fragment() {

    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        avedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.framgent_recent_status, container, false)
        val swipeRlayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRLayout)
        val recyclerView_recent = view.findViewById<RecyclerView>(R.id.recyclerView_recent)

        MobileAds.initialize(context) {}
        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        recyclerView_recent.adapter = StatusAdapter(
            context!!,
            Utils().getListFiles(
                File(
                    Environment.getExternalStorageDirectory()
                        .toString() + WHATSAPP_STATUSES_LOCATION
                )
            ),
            false
        )
        recyclerView_recent.layoutManager = GridLayoutManager(context, 2)


        swipeRlayout.setOnRefreshListener {
            recyclerView_recent.adapter = StatusAdapter(
                context!!,
                Utils().getListFiles(
                    File(
                        Environment.getExternalStorageDirectory()
                            .toString() + WHATSAPP_STATUSES_LOCATION
                    )
                ),
                false
            )
            swipeRlayout.isRefreshing = false
        }
        return view
    }


    companion object {
        private const val WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses"
    }


}