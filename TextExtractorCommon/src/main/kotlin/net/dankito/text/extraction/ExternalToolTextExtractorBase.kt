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
     * This method does not read process' standard and error output on extra threads like [executeCommand] does.
     *
     * Standard and error output are being read on same thread after process ended. Therefore if standard and error
     * InputStream reader's buffer is not large enough, the call to Process.waitFor() will hang forever.
     *
     * So it has slightly better performance than [executeCommand] has as it doesn't create two new threads but at the
     * cost that app may hangs forever.
     */
    protected open fun executeCommandWithLittleOutput(vararg arguments: String): ExecuteCommandResult {
        return commandExecutor.executeCommandWithLittleOutput(*arguments)
    }


    open fun executeCommand(config: CommandConfig): ExecuteCommandResult {
        return commandExecutor.executeCommand(config)
    }

    open suspend fun executeCommandSuspendable(config: CommandConfig): ExecuteCommandResult {
        return commandExecutor.executeCommandSuspendable(config)
    }

}