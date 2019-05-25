package net.dankito.text.extraction.app.android

import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import net.dankito.text.extraction.app.android.adapter.MainActivityTabsAdapter
import net.dankito.text.extraction.pdf.OpenPdfPdfTextExtractor
import net.dankito.utils.android.permissions.PermissionsService
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val log = LoggerFactory.getLogger(MainActivity::class.java)
    }


    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private val sectionsPagerAdapter = MainActivityTabsAdapter(supportFragmentManager)

    val permissionsService = PermissionsService(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUi()

        initLogic()
    }

    private fun initUi() {
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        // Set up the ViewPager with the sections adapter.
        container.adapter = sectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionsService.onRequestPermissionsResult(requestCode, permissions.map { it }.toTypedArray(), grantResults)

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    private fun initLogic() {
        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        extractTextOfFile(File(downloadDirectory, "<place your pdf file here>"))
    }

    private fun extractTextOfFile(pdfFile: File) {
        try {
            val pdfTextExtractor = OpenPdfPdfTextExtractor()

            val startTime = Date()
            val extractedText = pdfTextExtractor.extractText(pdfFile)
            val timeElapsed = (Date().time - startTime.time) / 1000

            log.info("Extracting text of file $pdfFile took $timeElapsed seconds")

            Toast.makeText(this, "Extracted in $timeElapsed seconds:\n${extractedText.text}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            log.error("Could not extract text of file $pdfFile", e)
        }
    }

}
