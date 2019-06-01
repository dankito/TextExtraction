# Text Extration

A modular framework for extracting text from many different sources (websites, PDFs, images).

## Text Extractors comparison

### PDF

|Extractor|Advantages|Disadvantages|
|---|---|---|
|[OpenPDF](https://github.com/librepdf/openpdf)|<ul><li>Free</li><li>Quite good and fast</li></ul>|<ul><li>Does not work on PDFs with disordered layouts</li><li>Does not run on older Androids (uses Java 8 features (Optinal); works on Android 6 but not on Android 4.1, others not tested)</li></ul>|
|[PDFBox](https://pdfbox.apache.org/) (not added yet)|||
|[PdfBox-Android](https://github.com/TomRoush/PdfBox-Android) (not added yet)|||
|[itext](https://github.com/itext/itext7)|<ul><li>Works also with PDFs with disordered layouts</li><li>Best PDF extraction result of any Java library I found</li><li>Works on older Androids (at least on Android 4.1)</li></ul>|<ul><li>Not free / commercial (AGPL / commercial license)</li></ul>|
|pdftotext|<ul><li>Best PDF extraction result so far</li></ul>|<ul><li>User has to install [XPDF](https://www.xpdfreader.com/download.html)</li><li>Does not run on Android</li></ul>|

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

**Notice**: Some libraries, like **itext**, have different, partially commercial licenses.