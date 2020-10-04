package codes.umair.wastatussaver.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import codes.umair.wastatussaver.R
import codes.umair.wastatussaver.adapters.SectionsPageAdapter
import codes.umair.wastatussaver.fragments.RecentStatusFragment
import codes.umair.wastatussaver.fragments.SavedStatusFragment
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import kotlinx.android.synthetic.main.activity_main.*
import spencerstudios.com.jetdblib.JetDB
import umairayub.madialog.MaDialog
import umairayub.madialog.MaDialogListener


class MainActivity : AppCompatActivity() {

    private lateinit var mSectionsPageAdapter: SectionsPageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mSectionsPageAdapter = SectionsPageAdapter(supportFragmentManager)


        tabs.setupWithViewPager(container)
        if (!isReadStorageAllowed()) {
            checkPermission()
        } else {
            // Set up the ViewPager with the sections adapter.
            setupViewPager(container)
        }

    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionsPageAdapter(
            supportFragmentManager
        )
        adapter.addFragment(RecentStatusFragment(), "Recent Status")
        adapter.addFragment(SavedStatusFragment(), "Saved Status")
        adapter.setPrimaryItem(viewPager,0,RecentStatusFragment())
        viewPager.adapter = adapter

    }

    override fun onBackPressed() {
        if (container.currentItem != 0){
            container.setCurrentItem(0,true)
        }else{
            super.onBackPressed()
        }

    }
    private fun checkPermission() {
        val rationale = "Please provide Storage permission to save Status."
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val options = Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning")
        Permissions.check(
            this,
            permissions,
            rationale,
            options,
            object : PermissionHandler() {
                override fun onGranted() {
                    // do your task.
                    // Set up the ViewPager with the sections adapter.
                    setupViewPager(container)
                }

                override fun onDenied(
                    context: Context,
                    deniedPermissions: ArrayList<String>
                ) {
                    // permission denied, block the feature.
                    Toast.makeText(
                        this@MainActivity,
                        "We need Storage Permission to Save/Load Status ",
                        Toast.LENGTH_LONG
                    ).show()

                }
            })
    }

    private fun isReadStorageAllowed(): Boolean {
        //Getting the permission status
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        return result == PackageManager.PERMISSION_GRANTED


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
             R.id.action_help -> {
                help()
            }
            R.id.action_share -> {
                share()
            }
            R.id.action_rateus -> {
                rateUs()
            }
            R.id.action_feedback -> {
                sendFeedback()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun rateUs() {
        MaDialog.Builder(this)
            .setImage(R.drawable.rating)
            .setTitle("Rate WA Status Saver")
            .setMessage(getString(R.string.rateus_message))
            .setPositiveButtonText("Sure")
            .setPositiveButtonListener(object : MaDialogListener{
                override fun onClick() {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")

                            )
                        )
                    }
                }
            })
            .setNegativeButtonText("Not Now")
            .setNegativeButtonListener(object : MaDialogListener {
                override fun onClick() {

                }
            })
            .build()
    }

    private fun sendFeedback() {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:umairayub79@gmail.com")
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Feedback")
        startActivity(Intent.createChooser(emailIntent, "Send Feedback!"))
    }

    private fun share() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, Check out this Awesome Status Saver app.\nThis app Lets you save WhatsApp Status Images and Videos.\nDownload Now : https://play.google.com/store/apps/details?id=$packageName"
        )
        startActivity(Intent.createChooser(shareIntent, "Share!"))
    }

    private fun help() {
        MaDialog.Builder(this)
            .setTitle("Help")
            .setMessage(resources.getString(R.string.help_message))
            .setPositiveButtonText("OK")
            .setPositiveButtonListener (object : MaDialogListener{
                override fun onClick() {

                }
            })
            .build()
    }
}








