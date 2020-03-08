package net.dankito.text.extraction

import org.slf4j.LoggerFactory


abstract class ExternalToolTextExtractorBase : TextExtractorBase() {

    companion object {
        private val log = LoggerFactory.getLogger(ExternalToolTextExtractorBase::class.java)
    }


    protected open fun executeCommand(vararg arguments: String): ExecuteCommandResult {
        try {
            // TODO: add "/bin/bash" or "cmd.exe" ?

            val processBuilder = ProcessBuilder(*arguments)

            val process = processBuilder.start()

            val outputReader = process.inputStream.bufferedReader()

            val processOutput = outputReader.readText().trim()

            val errorReader = process.errorStream.bufferedReader()

            val errors = errorReader.readText().trim()

            val exitCode = process.waitFor()
            log.debug("Command ${arguments.joinToString(" ")} exited with code $exitCode")

            outputReader.close()
            errorReader.close()

            return ExecuteCommandResult(exitCode, processOutput, errors)
        } catch (e: Exception) {
            log.error("Could not execute command ${arguments.joinToString(" ")}", e)

            return ExecuteCommandResult(-1, "", e.toString())
        }
    }

}