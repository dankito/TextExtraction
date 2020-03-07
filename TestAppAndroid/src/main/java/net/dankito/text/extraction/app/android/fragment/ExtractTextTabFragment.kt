package net.dankito.text.extraction.app.android.fragment

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.fragment_extract_text_tab.*
import kotlinx.android.synthetic.main.fragment_extract_text_tab.view.*
import net.dankito.filechooserdialog.FileChooserDialog
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.app.android.MainActivity
import net.dankito.text.extraction.app.android.R
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
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

        // to prevent that keyboard gets displayed on start
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

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

        extractTextOfFileAndShowResult()
    }

    protected open fun existingFileSelected(file: File) {
        lastSelectedFile = file

        btnExtractSelectedFile.isEnabled = true
    }


    protected open fun extractTextOfFileAndShowResult() {
        lastSelectedFile?.let { file ->
            prgbrIsExtractingText.visibility = View.VISIBLE
            btnExtractSelectedFile.isEnabled = false
            txtErrorMessage.visibility = View.GONE
            val extractorName = getExtractorName() // as to be done before triggering asynchronous extractoin as user may in the mean while changes current tab
            val startTime = Date().time

            extractTextOfFileAsync(file) { extractedText ->
                val durationMillis = Date().time - startTime

                activity?.let { context ->
                    context.runOnUiThread {
                        showExtractedTextOnUiThread(context, file, extractedText, extractorName, durationMillis)
                    }
                }
            }
        }
    }

    protected open fun showExtractedTextOnUiThread(context: Context, fileToExtract: File, extractionResult: ExtractionResult, extractorName: String, durationMillis: Long) {
        prgbrIsExtractingText.visibility = View.GONE
        btnExtractSelectedFile.isEnabled = true
        txtvwExtractionTime.text = String.format("%02d:%02d.%03d min", durationMillis / (60 * 1000),
            (durationMillis / 1000) % 60, durationMillis % 1000)

        txtvwExtractedText.text = extractionResult.text

        txtErrorMessage.visibility = if (extractionResult.errorOccurred) View.GONE else View.VISIBLE
        extractionResult.error?.let { error ->
            txtErrorMessage.text = getErrorMessage(context, fileToExtract, extractorName, error)
        }
    }

    protected open fun getErrorMessage(context: Context, fileToExtract: File, extractorName: String, error: ErrorInfo): String {
        return when (error.type) {
            ErrorType.FileTypeNotSupportedByExtractor -> context.getString(R.string.fragment_extract_text_tab_error_message_extractor_does_not_support_extracting_file_type, extractorName, fileToExtract.extension)
            ErrorType.ExtractorNotAvailable -> context.getString(R.string.fragment_extract_text_tab_error_message_extractor_not_available, extractorName)
            ErrorType.ParseError -> context.getString(R.string.fragment_extract_text_tab_error_message_could_not_parse_file, error.exception?.localizedMessage)
        }
    }

    protected open fun getExtractorName(): String {
        // kind a hack to get Tab name
        return activity?.findViewById<TabLayout>(R.id.tabs)?.let { tabLayout ->
            tabLayout.getTabAt(tabLayout.selectedTabPosition)?.text?.toString() // get text of current selected Tab
        } ?: getTextExtractor().javaClass.simpleName
    }


    protected open fun extractTextOfFileAsync(file: File, callback: (ExtractionResult) -> Unit) {
        thread { // TODO: use thread pool
            callback(extractTextOfFile(file))
        }
    }

    protected open fun extractTextOfFile(file: File): ExtractionResult {
        try {
            val textExtractor = getTextExtractor()

            val startTime = Date()
            val extractedText = textExtractor.extractText(file)
            val timeElapsed = (Date().time - startTime.time) / 1000

            log.info("Extracting text of file $file took $timeElapsed seconds")

            return extractedText
        } catch (e: Exception) {
            log.error("Could not extract text of file $file", e)

            return ExtractionResult(ErrorInfo(ErrorType.ParseError, e)) // TODO: add suitable ErrorType
        }
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