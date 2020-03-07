package net.dankito.text.extraction.model


enum class ErrorType {

    NoExtractorFoundForFileType,

    ExtractorNotAvailable,

    FileTypeNotSupportedByExtractor,

    ParseError

}