package codes.umair.wastatussaver.activities

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import codes.umair.wastatussaver.R
import codes.umair.wastatussaver.Utils
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File

class ImageActivity : AppCompatActivity() {
    lateinit var mAdView: AdView
    lateinit var statusPath: String

    var isSaved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        statusPath = intent!!.getStringExtra("ImagePath")
        isSaved = intent!!.getBooleanExtra("isSaved", false)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        Glide.with(this)
            .load(statusPath)
            .into(statusImageView)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_menu, menu)
        if (isSaved) {
            menu!!.getItem(1).isVisible = false
        } else {
            menu!!.getItem(2).isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_all -> {
                Utils().downloadMediaItem(this, File(statusPath))
            }
            R.id.action_share_all -> {
                val uris = ArrayList<Uri>()
                val contentUri: Uri = Utils().getFileUri(this, File(statusPath))
                uris.add(contentUri)
                Utils().shareMediaItem(this, uris)
            }
            R.id.action_delete_all -> {
                Utils().deleteMediaItem(this, File(statusPath))
            }

        }
        return super.onOptionsItemSelected(item)
    }
}