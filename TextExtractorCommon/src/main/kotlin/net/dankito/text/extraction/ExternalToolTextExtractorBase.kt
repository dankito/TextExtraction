package net.dankito.text.extraction

import net.dankito.text.extraction.commandline.CommandlineProgram
import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor


abstract class ExternalToolTextExtractorBase(
    osIndependentDefaultExecutableName: String,
    protected val commandExecutor: ICommandExecutor = CommandExecutor()
) : TextExtractorBase() {


    protected val commandlineProgram = CommandlineProgram(osIndependentDefaultExecutableName, commandExecutor)

    override val isAvailable: Boolean
        get() = commandlineProgram.isAvailable


    open fun executeCommand(config: CommandConfig): ExecuteCommandResult {
        return commandExecutor.executeCommand(config)
    }

    open suspend fun executeCommandSuspendable(config: CommandConfig): ExecuteCommandResult {
        return commandExecutor.executeCommandSuspendable(config)
    }

}