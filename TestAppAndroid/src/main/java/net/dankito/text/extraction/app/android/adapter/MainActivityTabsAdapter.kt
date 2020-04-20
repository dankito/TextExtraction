package net.dankito.text.extraction.app.android.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import net.dankito.text.extraction.ITextExtractorRegistry
import net.dankito.text.extraction.app.android.fragment.FindBestExtractorExtractTextTabFragment
import net.dankito.text.extraction.app.android.fragment.OpenPdfExtractTextTabFragment
import net.dankito.text.extraction.app.android.fragment.iTextExtractTextTabFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MainActivityTabsAdapter(
    fragmentManager: FragmentManager,
    private val extractorRegistry: ITextExtractorRegistry
) : FragmentPagerAdapter(fragmentManager) {


    override fun getCount(): Int {
        return 3
    }


    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return OpenPdfExtractTextTabFragment()
            1 -> return iTextExtractTextTabFragment()
            2 -> return FindBestExtractorExtractTextTabFragment(extractorRegistry)
        }

        // should never come to here
        return null
    }

}