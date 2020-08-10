package codes.umair.wastatussaver.activities


import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import codes.umair.wastatussaver.R
import kotlinx.android.synthetic.main.activity_video.*


class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)


        val path = intent!!.getStringExtra("videoPath")
        videoView.setVideoURI(Uri.parse(path))

        // create an object of media controller
        val mediaController = MediaController(this)
        // set media controller object for a video view
        videoView.setMediaController(mediaController)
        videoView.start()
    }

}