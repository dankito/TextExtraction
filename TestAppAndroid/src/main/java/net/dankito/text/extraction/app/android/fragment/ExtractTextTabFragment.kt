package net.dankito.text.extraction.app.android.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
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

        rootView.edtxtSelectedFile.addTextChangedListener(edtxtSelectedFileTextWatch)

        rootView.btnSelectFile.setOnClickListener { selectFile() }

        rootView.btnExtractSelectedFile.setOnClickListener { extractTextOfFileAndShowResult() }

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
        edtxtSelectedFile.setText(file.absolutePath)

        existingFileSelected(file)
    }

    protected open fun existingFileSelected(file: File) {
        lastSelectedFile = file

        btnExtractSelectedFile.isEnabled = true
    }


    protected open fun extractTextOfFileAndShowResult() {
        lastSelectedFile?.let { file ->
            val startTime = Date().time
            prgbrIsExtractingText.visibility = View.VISIBLE

            extractTextOfFileAsync(file) { extractedText ->
                val durationMillis = Date().time - startTime

                activity?.runOnUiThread {
                    showExtractedTextOnUiThread(extractedText, durationMillis)
                }
            }
        }
    }

    protected open fun showExtractedTextOnUiThread(extractedText: ExtractedText?, durationMillis: Long) {
        prgbrIsExtractingText.visibility = View.GONE
        txtvwExtractionTime.text = String.format("%02d:%02d.%03d min", durationMillis / (60 * 1000),
            (durationMillis / 1000) % 60, durationMillis % 1000)

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


    protected val edtxtSelectedFileTextWatch = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(string: CharSequence?, start: Int, before: Int, count: Int) {
            string?.toString().let { enteredText ->
                try {
                    val file = File(enteredText)

                    if (file.exists()) {
                        existingFileSelected(file)
                    }
                    else {
                        btnExtractSelectedFile.isEnabled = false
                    }
                } catch (e: Exception) {
                    log.warn("Could not convert '$string' to a File object", e)
                }
            }

        }

        override fun afterTextChanged(s: Editable?) { }

    }

}