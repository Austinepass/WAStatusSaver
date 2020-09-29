package codes.umair.wastatussaver.activities


import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import codes.umair.wastatussaver.R
import codes.umair.wastatussaver.Utils
import kotlinx.android.synthetic.main.activity_video.*
import java.io.File


class VideoActivity : AppCompatActivity() {
    lateinit var path: String
    var isSaved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        path = intent!!.getStringExtra("videoPath")
        isSaved = intent!!.getBooleanExtra("isSaved", false)

        videoView.setVideoURI(Uri.parse(path))

        // create an object of media controller
        val mediaController = MediaController(this)
        // set media controller object for a video view
        videoView.setMediaController(mediaController)
        videoView.start()
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
                Utils().downloadMediaItem(this, File(path))
            }
            R.id.action_share_all -> {
                val uris = ArrayList<Uri>()
                val contentUri: Uri = Utils().getFileUri(this,File(path))
                uris.add(contentUri)
                Utils().shareMediaItem(this, uris)
            }
            R.id.action_delete_all -> {
                Utils().deleteMediaItem(this, File(path))
            }

        }
        return super.onOptionsItemSelected(item)
    }

}