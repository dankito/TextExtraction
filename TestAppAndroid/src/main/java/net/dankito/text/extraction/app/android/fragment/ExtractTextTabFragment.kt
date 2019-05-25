package net.dankito.text.extraction.app.android.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_extract_text_tab.*
import kotlinx.android.synthetic.main.fragment_extract_text_tab.view.*
import net.dankito.filechooserdialog.FileChooserDialog
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.app.android.MainActivity
import net.dankito.text.extraction.app.android.R
import net.dankito.text.extraction.model.ExtractedText
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File
import java.util.*
import kotlin.concurrent.thread


abstract class ExtractTextTabFragment : Fragment() {

    companion object {
        private val log = LoggerFactory.getLogger(ExtractTextTabFragment::class.java)
    }


    abstract fun createTextExtractor(): ITextExtractor


    protected var lastSelectedFile: File? = null

    protected var textExtractorField: ITextExtractor? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_extract_text_tab, container, false)

        rootView.btnSelectFile.setOnClickListener { selectFile() }

        rootView.txtvwExtractedText.movementMethod = ScrollingMovementMethod() // to make TextView scrollable

        return rootView
    }

    override fun onDestroy() {
        (textExtractorField as? Closeable)?.close()

        super.onDestroy()
    }


    protected open fun selectFile() {
        activity?.let { activity ->
            val config = FileChooserDialogConfig(getExtensionFilter(), lastSelectedFile?.parentFile)

            FileChooserDialog().showOpenSingleFileDialog(activity, (activity as? MainActivity)?.permissionsService, config) { _, file ->
                file?.let {
                    selectedFile(file)
                }
            }
        }
    }

    protected open fun getExtensionFilter(): List<String> {
        return listOf() // may overwritten in subclass
    }


    protected open fun selectedFile(file: File) {
        lastSelectedFile = file
        edtxtSelectedFile.setText(file.absolutePath)

        extractTextOfFileAsync(file) { extractedText ->
            activity?.runOnUiThread {
                showExtractedTextOnUiThread(extractedText)
            }

        }
    }

    protected open fun showExtractedTextOnUiThread(extractedText: ExtractedText?) {
        extractedText?.let {
            txtvwExtractedText.text = extractedText.text
        }

        // TODO: elsewise show error message
    }


    protected open fun extractTextOfFileAsync(file: File, callback: (ExtractedText?) -> Unit) {
        thread { // TODO: use thread pool
            callback(extractTextOfFile(file))
        }
    }

    protected open fun extractTextOfFile(file: File): ExtractedText? {
        try {
            val textExtractor = getTextExtractor()

            val startTime = Date()
            val extractedText = textExtractor.extractText(file)
            val timeElapsed = (Date().time - startTime.time) / 1000

            log.info("Extracting text of file $file took $timeElapsed seconds")

            return extractedText
        } catch (e: Exception) {
            log.error("Could not extract text of file $file", e)
        }

        return null // TODO: add error to ExtractedText instead of returning null
    }

    protected open fun getTextExtractor(): ITextExtractor {
        textExtractorField?.let {
            return it
        }

        val newTextExtractor = createTextExtractor()

        this.textExtractorField = newTextExtractor

        return newTextExtractor
    }

}