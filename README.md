# Text Extration

A modular framework for extracting text from many different sources (websites, PDFs, images).

## Text Extractors comparison

### PDF

There are two types of PDF:
- "Image only" PDFs that just embed (scanned) images. But they contain no selectable and therefore extractable text. To get the text in the images, first the images 
have to be extracted from the PDF and then OCR applied to them. See section [Images](Images).
- Searchable PDFs: If you open them in a PDF viewer you can select their text or search for it. The following libraries help to extract text from these types of PDFs:

#### Searchable PDFs 

| Extractor | Permissive License | Runs on Android | Advantages | Disadvantages |
|---|---|---|---|---|
|pdftotext|:heavy_check_mark:|:x:|<ul><li>Best PDF extraction result so far</li></ul>|<ul><li>User has to install [Poppler Utils](https://www.xpdfreader.com/download.html)</li><li>Does not run on Android</li></ul>|
|iText 2|:heavy_check_mark:|:heavy_check_mark:|<ul><li>Works also with PDFs with disordered layouts</li><li>Best PDF extraction result of any Java library I found</li><li>Works on older Androids (at least on Android 4.1)</li><li>Almost the same text extraction quality as the newer (and non-free) iText 7</li></ul>|<ul><li></li></ul>|
|[iText](https://github.com/itext/itext7)|:x:|:heavy_check_mark:|<ul><li>Works also with PDFs with disordered layouts</li><li>Best PDF extraction result of any Java library I found</li><li>Works on older Androids (at least on Android 4.1)</li></ul>|<ul><li>Not free / commercial (AGPL / commercial license)</li></ul>|
|[OpenPDF](https://github.com/librepdf/openpdf)|:heavy_check_mark:|(:heavy_check_mark:)|<ul><li>Free</li><li>Quite good and fast</li></ul>|<ul><li>Does not work on PDFs with disordered layouts</li><li>Does not run on older Androids (uses Java 8 features (Optional); works on Android 6 but not on Android 4.1, others not tested)</li></ul>|
|[PDFBox](https://pdfbox.apache.org/) (not added yet)|:heavy_check_mark:|:x:|||
|[PdfBox-Android](https://github.com/TomRoush/PdfBox-Android) (not added yet)|:heavy_check_mark:|:heavy_check_mark:|||


##### iText 2 and iText 7

iText 2 is the older, permissive version of then turned commercial iText.
But as the last free iText version, 2.1.7, has security flaws, I used version 2.1.7.js7 from JasperReports as this version fixes the security issues.
It's slower than iText 7 but in regard to text extraction quality I cannot see any difference between iText 7 and iText 2.


##### OpenPdf
OpenPdf took the last commit with a permissive license of iText and developed it further.
But according to my experience its text extraction capability is worse than that one of iText 7 and iText 2. 

Do not add OpenPdfPdfTextExtractor and iText2PdfTextExtractor to the class path at the same time as both have the same 
package and class names but different method and class signatures -> one of them will crash when using them.


##### (Very opinionated) Recommendation

If you can use pdftotext (Poppler), use pdftotext. It yields the best results both in terms of text extraction quality and speed.

Otherwise use security issues fixed version of iText 2. It's slower than commercial (and really amazing good) iText 7, but in terms of text extraction quality I cannot see any difference between iText 2 and iText 7.

I don't know why, but of some PDFs OpenPdf cannot extract any text at all.

#### How to distinguish between Searchable and "Image only" PDFs?

Kurt Pfeifle gave an superb hint (https://stackoverflow.com/a/3108531): Check how many fonts a PDF uses.
If it uses fonts, it contains searchable text. If it uses no font at all it contains only images.

I added [IPdfTypeDetector](TextExtractorCommon/src/main/kotlin/net/dankito/text/extraction/pdf/IPdfTypeDetector.kt) implementations for Poppler / [pdffonts](PopplerPdfTextExtractor/src/main/kotlin/net/dankito/text/extraction/pdf/pdffontsPdfTypeDetector.kt) and ...


### Images
(All variants with Tesseract 4 have the same extraction quality, which is quite good but not the best.)

|Extractor|Advantages|Disadvantages|
|---|---|---|
|[tess4j](https://github.com/nguyenq/tess4j)|<ul><li>Uses Tesseract 4</li></ul>|<ul><li>User has to install Tesseract</li><li>Extraction result depends a lot on image quality</li><li>Does not run on Android</li></ul>|
|Tesseract 4 over JNI (e. g. from [Bytedeco](https://github.com/bytedeco/javacpp-presets/tree/master/tesseract))|<ul><li>Uses Tesseract 4</li></ul>|<ul><li>If there's an exception in native code whole application crashes (JNI)</li><li>User has to install Tesseract</li><li>Extraction result depends a lot on image quality</li><li>Does not run on Android</li></ul>|
|[Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android)|<ul><li>Uses Tesseract 4</li></ul>|<ul><li>Very slow, took 2 minutes to recognize a single image (0,5 MB)</li><li>Extraction result depends a lot on image quality</li></ul>|
|[Tess4Android](https://github.com/zsmartercn/Tess4Android)|<ul><li>Uses Tesseract 4</li></ul>|<ul><li>Couldn't get it to compile</li></ul>|
|[TextFairy](https://play.google.com/store/apps/details?id=com.renard.ocr) (not added yet)||<ul><li>Uses Tesseract 3</li><li>Quite slow</li><li>Extraction result depends a lot on image quality</li></ul>|
|[Microsoft Cloud Computer Vision API OCR](https://westus.dev.cognitive.microsoft.com/docs/services/5adf991815e1060e6355ad44/operations/56f91f2e778daf14a499e1fc) (not implemented yet)|<ul><li>Best image extraction result I found so far</li></ul>|<ul><li>Requires registration (credit card required; every single user to do this for his/her self)</li><li>Costs $1.50 per 1000 images ([see](https://azure.microsoft.com/en-us/pricing/details/cognitive-services/))</li><li>Data protection insanity, stores all your images and recognized text for years</li>|
|[Google Cloud Vision OCR](https://cloud.google.com/vision/docs/ocr) (neither implemented nor tested yet)||<ul><li>Requires registration (credit card required; every single user to do this for his/her self)</li><li>1000 images per month are free, have to pay for more ([see](https://cloud.google.com/vision/pricing))</li><li>Data protection insanity, stores all your images and recognized text for years</li>|

## License

If not stated otherwise all code is licensed under Apache License, Version 2.0.

**Notice**: Some libraries, like **iText**, have different, partially commercial licenses.