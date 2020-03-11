package net.dankito.text.extraction

import net.dankito.utils.process.CommandConfig
import net.dankito.utils.process.CommandExecutor
import net.dankito.utils.process.ExecuteCommandResult
import net.dankito.utils.process.ICommandExecutor


abstract class ExternalToolTextExtractorBase(
    protected val commandExecutor: ICommandExecutor = CommandExecutor()
) : TextExtractorBase() {

    /**
     * Do not call this for commands that have a large standard or error output!
     *
     * This method will block forever while trying to read a large standard or error output.
     */
    protected open fun executeCommandWithLittleOutput(vararg arguments: String): ExecuteCommandResult {
        return commandExecutor.executeCommandWithLittleOutput(*arguments)
    }


    open fun executeCommand(config: CommandConfig): ExecuteCommandResult {
        return commandExecutor.executeCommand(config)
    }

}