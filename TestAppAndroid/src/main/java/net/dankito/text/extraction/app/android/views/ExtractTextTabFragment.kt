package net.dankito.text.extraction.app.android.views

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.fragment_extract_text_tab.view.*
import net.dankito.filechooserdialog.FileChooserDialog
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.text.extraction.app.android.MainActivity
import net.dankito.text.extraction.app.android.R
import java.io.File


class ExtractTextTabFragment : Fragment() {

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): ExtractTextTabFragment {
            val fragment = ExtractTextTabFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }


    protected var lastSelectedFile: File? = null

    protected lateinit var edtxtSelectedFile: EditText


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_extract_text_tab, container, false)

        edtxtSelectedFile = rootView.edtxtSelectedFile

        rootView.btnSelectFile.setOnClickListener { selectFile() }
        rootView.txtvwExtractedText.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))

        return rootView
    }

    private fun selectFile() {
        activity?.let { activity ->
            val config = FileChooserDialogConfig(getExtensionFilter(), lastSelectedFile?.parentFile)

            FileChooserDialog().showOpenSingleFileDialog(activity, (activity as? MainActivity)?.permissionsService, config) { _, file ->
                file?.let {
                    lastSelectedFile = file
                    edtxtSelectedFile.setText(file.absolutePath)

                    extractTextOfFile(file)
                }
            }
        }
    }

    private fun getExtensionFilter(): List<String> {
        return listOf()
    }

    private fun extractTextOfFile(file: File) {

    }

}