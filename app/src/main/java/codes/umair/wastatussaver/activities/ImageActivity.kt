package codes.umair.wastatussaver.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import codes.umair.wastatussaver.R
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val statusPath = intent!!.getStringExtra("ImagePath")

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        Glide.with(this)
            .load(statusPath)
            .into(statusImageView)


    }
}