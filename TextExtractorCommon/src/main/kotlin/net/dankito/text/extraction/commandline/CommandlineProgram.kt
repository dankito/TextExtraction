package net.dankito.text.extraction.commandline

import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor


open class CommandlineProgram(
    osIndependentDefaultExecutableName: String,
    protected val commandExecutor: ICommandExecutor = CommandExecutor()
) {

    open var programExecutablePath: String = ""
        protected set

    protected open var lastCheckIsProgramAvailableResult: Boolean = false

    open val isAvailable: Boolean
        get() = lastCheckIsProgramAvailableResult


    init {
        programExecutablePath = getOsDependentExecutableName(osIndependentDefaultExecutableName)

        checkIsProgramAvailable(programExecutablePath)
    }


    protected open fun getOsDependentExecutableName(executableName: String): String {
        val osName = System.getProperty("os.name")
        val isRunningOnWindows = osName.toLowerCase().startsWith("windows")

        if (isRunningOnWindows && executableName.toLowerCase().endsWith(".exe") == false) {
            return executableName + ".exe"
        }

        return executableName
    }


    protected open fun checkIsProgramAvailable(programExecutablePath: String): Boolean {
        val executionResult = executeCheckIfProgramIsAvailable(getCommandArgsToCheckIfProgramIsAvailable(programExecutablePath))

        lastCheckIsProgramAvailableResult = evaluateIfProgramIsAvailable(executionResult)

        return lastCheckIsProgramAvailableResult
    }

    protected open fun getCommandArgsToCheckIfProgramIsAvailable(programExecutablePath: String): List<String> {
        return listOf(programExecutablePath, "-v")
    }

    protected open fun executeCheckIfProgramIsAvailable(commandArgs: List<String>): ExecuteCommandResult {
        return commandExecutor.executeCommandWithLittleOutput(CommandConfig(commandArgs))
    }

    protected open fun evaluateIfProgramIsAvailable(executionResult: ExecuteCommandResult): Boolean {
        return executionResult.successful
    }

}