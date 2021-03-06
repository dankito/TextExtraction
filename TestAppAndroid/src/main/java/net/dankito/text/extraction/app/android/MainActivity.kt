package net.dankito.text.extraction.app.android

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import net.dankito.text.extraction.ITextExtractorRegistry
import net.dankito.text.extraction.TextExtractorRegistry
import net.dankito.text.extraction.app.android.adapter.MainActivityTabsAdapter
import net.dankito.text.extraction.pdf.PdfBoxAndroidPdfTextExtractor
import net.dankito.text.extraction.pdf.iText2PdfTextExtractor
import net.dankito.text.extraction.pdf.iTextPdfTextExtractor
import net.dankito.utils.android.permissions.PermissionsService

class MainActivity : AppCompatActivity() {


    private lateinit var extractorRegistry: ITextExtractorRegistry

    val permissionsService = PermissionsService(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLogic()

        initUi()
    }

    private fun initUi() {
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        // Set up the ViewPager with the sections adapter.
        container.adapter = MainActivityTabsAdapter(supportFragmentManager, extractorRegistry)

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
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
        extractorRegistry = TextExtractorRegistry(listOf(
            // do not include OpenPdfPdfTextExtractor and iText2PdfTextExtractor at the same time as both
            // have the same package and class names but different method and class signatures
//            OpenPdfPdfTextExtractor(),
            iText2PdfTextExtractor(),
            iTextPdfTextExtractor(),
            PdfBoxAndroidPdfTextExtractor(applicationContext)
        ))
    }

}
