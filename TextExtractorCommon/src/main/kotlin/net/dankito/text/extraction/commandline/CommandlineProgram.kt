package net.dankito.text.extraction.commandline

import net.dankito.utils.os.OsHelper
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor
import java.io.File


open class CommandlineProgram(
    osIndependentDefaultExecutableName: String,
    protected val commandExecutor: ICommandExecutor = CommandExecutor(),
    protected val osHelper: OsHelper = OsHelper()
) {

    open var programExecutablePath: String = ""
        protected set

    open var isAvailable: Boolean = false
        protected set


    init {
        setProgramExecutablePathTo(getOsDependentExecutableName(osIndependentDefaultExecutableName))
    }


    protected open fun getOsDependentExecutableName(executableName: String): String {
        if (osHelper.isRunningOnWindows && File(executableName).extension.isEmpty()) {
            return executableName + ".exe"
        }

        return executableName
    }


    open fun setProgramExecutablePathTo(programExecutablePath: String) {
        this.programExecutablePath = programExecutablePath

        checkIsProgramAvailable(programExecutablePath)
    }

    protected open fun checkIsProgramAvailable(programExecutablePath: String): Boolean {
        val executionResult = executeCheckIfProgramIsAvailable(getCommandArgsToCheckIfProgramIsAvailable(programExecutablePath))

        isAvailable = evaluateIfProgramIsAvailable(executionResult)

        return isAvailable
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