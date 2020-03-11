package net.dankito.text.extraction

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.InputStream


abstract class ExternalToolTextExtractorBase : TextExtractorBase() {

    companion object {
        val SplitCommandRegex = "\\s".toRegex()

        private val log = LoggerFactory.getLogger(ExternalToolTextExtractorBase::class.java)
    }


    // Does not work for large outputs !!
//    protected open fun executeCommand(command: String, workingDir: File? = null): ExecuteCommandResult {
    open fun executeCommand(command: String, workingDir: File? = null): ExecuteCommandResult {
        log.info("Executing command '$command' ...")

        try {
            val parts = command.split(SplitCommandRegex)
            val process = ProcessBuilder(parts)
                .directory(workingDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()

            val exitCode = process.waitFor()
            val output = process.inputStream.bufferedReader().readText()
            val errors = process.errorStream.bufferedReader().readText()

            return ExecuteCommandResult(exitCode, output, errors)
        } catch (e: Exception) {
            return parseExceptionToExecuteCommandResult(command, e)
        }
    }

    open fun executeCommandInExtraThreads(command: String, workingDir: File? = null): ExecuteCommandResult {
        log.info("Executing command '$command' ...")

        try {
            val parts = command.split(SplitCommandRegex)
            val processBuilder = ProcessBuilder(parts)

            workingDir?.let {
                processBuilder.directory(workingDir)
            }

            processBuilder.environment().put("OMP_THREAD_LIMIT", "1")

            val process = processBuilder.start()

            val outputGobbler = StreamGobbler(process.inputStream)
            val errorGobbler = StreamGobbler(process.errorStream)

            outputGobbler.start()
            errorGobbler.start()

            val exitCode = process.waitFor()
            val output = outputGobbler.lines
            val errors = errorGobbler.lines

            return ExecuteCommandResult(exitCode, output, errors)
        } catch (e: Exception) {
            return parseExceptionToExecuteCommandResult(command, e)
        }
    }

    open fun executeCommandAsync(command: String, workingDir: File? = null): Deferred<ExecuteCommandResult> {
        return executeCommandAsync(GlobalScope, command, workingDir)
    }

    open fun executeCommandAsync(scope: CoroutineScope, command: String, workingDir: File? = null): Deferred<ExecuteCommandResult> {
        return scope.async(Dispatchers.IO) {
            executeCommandSuspendable(scope, command, workingDir)
        }
    }

    open suspend fun executeCommandSuspendable(scope: CoroutineScope, command: String, workingDir: File? = null): ExecuteCommandResult {
        log.info("Executing command '$command' ...")

        try {
            val parts = command.split(SplitCommandRegex)
            val processBuilder = ProcessBuilder(parts)

            workingDir?.let {
                processBuilder.directory(workingDir)
            }

            processBuilder.environment().put("OMP_THREAD_LIMIT", "1")

            val process = processBuilder.start()

            val outputStream = scope.async { readStream(process.inputStream) }
            val errorStream = scope.async { readStream(process.errorStream) }

            val exitCode = process.waitFor()

            return ExecuteCommandResult(exitCode, outputStream.await(), errorStream.await())
        } catch (e: Exception) {
            return parseExceptionToExecuteCommandResult(command, e)
        }
    }

    private fun readStream(inputStream: InputStream): String {
        val readLines = mutableListOf<String>()

        try {
            val reader = inputStream.bufferedReader()
            var line: String?

            do {
                line = reader.readLine()

                if (line != null) {
                    readLines.add(line)
                }
            } while (line != null)
        } catch (e: Exception) {
            log.error("Error occurred while reading stream", e)
        }

        return readLines.joinToString(System.lineSeparator())
    }

    private fun parseExceptionToExecuteCommandResult(command: String, e: Exception): ExecuteCommandResult {
        log.info("Could not execute command '$command'", e)

        var exitCode = -1

        e.message?.let { exceptionMessage ->
            if (exceptionMessage.contains("error=")) {
                val startIndex = exceptionMessage.indexOf("error=") + "error=".length
                var endIndex = startIndex + 1

                for (i in startIndex + 1..exceptionMessage.length - 1) {
                    if (exceptionMessage[i].isDigit() == false) {
                        endIndex = i
                        break
                    }
                }

                exitCode = exceptionMessage.substring(startIndex, endIndex).toInt()
            }
        }

        return ExecuteCommandResult(exitCode, "", e.localizedMessage)
    }

    class StreamGobbler(protected val inputStream: InputStream) : Thread() {

        companion object {
            private val log = LoggerFactory.getLogger(StreamGobbler::class.java)
        }


        protected val readLines = mutableListOf<String>()

        val lines: String
            get() = readLines.joinToString(System.lineSeparator())


        override fun run() {
            try {
                val reader = inputStream.bufferedReader()
                var line: String?

                do {
                    line = reader.readLine()

                    if (line != null) {
                        readLines.add(line)
                    }
                } while (line != null)
            } catch (e: Exception) {
                log.error("Error occurred while reading stream", e)
            }
        }
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