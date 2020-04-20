package net.dankito.text.extraction.app.android.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import kotlinx.android.synthetic.main.fragment_extract_text_tab.*
import kotlinx.android.synthetic.main.fragment_extract_text_tab.view.*
import kotlinx.coroutines.*
import net.dankito.filechooserdialog.FileChooserDialog
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.text.extraction.ITextExtractor
import net.dankito.text.extraction.app.android.MainActivity
import net.dankito.text.extraction.app.android.R
import net.dankito.text.extraction.model.ErrorInfo
import net.dankito.text.extraction.model.ErrorType
import net.dankito.text.extraction.model.ExtractionResult
import net.dankito.text.extraction.model.ExtractionResultForExtractor
import net.dankito.utils.Stopwatch
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.io.File


abstract class ExtractTextTabFragment : Fragment(), CoroutineScope by MainScope() {

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
        return getTextExtractor().supportedFileTypes
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
            txtInfoTextExtractedWith.visibility = View.GONE
            txtErrorMessage.visibility = View.GONE

            GlobalScope.launch(Dispatchers.IO) {
                val stopwatch = Stopwatch()

                val extractionResult = extractTextOfFile(file)

                stopwatch.stop()

                withContext(Dispatchers.Main) {
                    activity?.let { context ->
                        showExtractedTextOnUiThread(context, file, extractionResult, stopwatch)
                    }
                }
            }
        }
    }

    protected open fun showExtractedTextOnUiThread(context: Context, fileToExtract: File, extractionResult: ExtractionResult, stopwatch: Stopwatch) {
        prgbrIsExtractingText.visibility = View.GONE
        btnExtractSelectedFile.isEnabled = true
        txtvwExtractionTime.text = stopwatch.formatElapsedTime()

        txtvwExtractedText.text = extractionResult.text ?: ""

        if (extractionResult is ExtractionResultForExtractor) {
            txtInfoTextExtractedWith.visibility = View.VISIBLE
            txtInfoTextExtractedWith.text = context.getString(R.string.fragment_extract_text_tab_info_text_extracted_with, extractionResult.extractor?.name)
        }

        txtErrorMessage.visibility = if (extractionResult.errorOccurred) View.VISIBLE else View.GONE
        extractionResult.error?.let { error ->
            txtErrorMessage.text = getErrorMessage(context, fileToExtract, error)
        }
    }

    protected open fun getErrorMessage(context: Context, fileToExtract: File, error: ErrorInfo): String {
        val extractorName = getTextExtractor().name
        val fileType = fileToExtract.extension

        return when (error.type) {
            ErrorType.FileTypeNotSupportedByExtractor -> context.getString(R.string.fragment_extract_text_tab_error_message_extractor_does_not_support_extracting_file_type, extractorName, fileType)
            ErrorType.ExtractorNotAvailable -> context.getString(R.string.fragment_extract_text_tab_error_message_extractor_not_available, extractorName)
            ErrorType.ParseError -> context.getString(R.string.fragment_extract_text_tab_error_message_could_not_parse_file, error.exception?.localizedMessage)
            ErrorType.NoExtractorFoundForFileType -> context.getString(R.string.fragment_extract_text_tab_error_message_no_extractor_found_for_type, fileType)
        }
    }


    protected open suspend fun extractTextOfFile(file: File): ExtractionResult {
        try {
            val textExtractor = getTextExtractor()

            val stopwatch = Stopwatch()
            val extractedText = textExtractor.extractTextSuspendable(file)

            stopwatch.stopAndLog("Extracting text of file $file", log)

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