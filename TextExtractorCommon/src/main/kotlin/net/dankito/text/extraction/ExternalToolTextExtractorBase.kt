package net.dankito.text.extraction

import kotlinx.coroutines.delay
import net.dankito.text.extraction.commandline.CommandlineProgram
import net.dankito.utils.process.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


abstract class ExternalToolTextExtractorBase(
    osIndependentDefaultExecutableName: String,
    protected val commandExecutor: ICommandExecutor = CommandExecutor(),
    /**
     * Some programs like Tesseract would completely block your cpus and therefore your system when
     * there are too many parallel executions of it.
     * Set to a value less or equal to zero to disable max parallel executions check and to run
     * unlimited instances in parallel.
     */
    protected val maxCountParallelExecutions: Int = CpuInfo.CountCores
) : TextExtractorBase() {

    companion object {
        const val CountMillisToSleepWhenThereAreTooManyParallelExecutions = 100L
    }


    protected val commandlineProgram = CommandlineProgram(osIndependentDefaultExecutableName, commandExecutor)

    protected val countParallelExecutions = AtomicInteger(0)


    override val isAvailable: Boolean
        get() = commandlineProgram.isAvailable

    open val programExecutablePath: String
        get() = commandlineProgram.programExecutablePath


    open fun setProgramExecutablePathTo(programExecutablePath: String) {
        commandlineProgram.setProgramExecutablePathTo(programExecutablePath)
    }


    open fun executeCommandWithLittleOutput(vararg arguments: String): ExecuteCommandResult {
        return commandExecutor.executeCommandWithLittleOutput(*arguments)
    }

    open fun executeCommandWithLittleOutput(config: CommandConfig): ExecuteCommandResult {
        return commandExecutor.executeCommandWithLittleOutput(config)
    }

    open fun executeCommand(config: CommandConfig): ExecuteCommandResult {
        if (maxCountParallelExecutions > 0) {
            while (countParallelExecutions.get() > maxCountParallelExecutions) {
                try { TimeUnit.MILLISECONDS.sleep(CountMillisToSleepWhenThereAreTooManyParallelExecutions) } catch (ignored: Exception) { }
            }

            countParallelExecutions.incrementAndGet()
        }

        val result = commandExecutor.executeCommand(config)

        countParallelExecutions.decrementAndGet()

        return result
    }

    open suspend fun executeCommandSuspendable(config: CommandConfig): ExecuteCommandResult {
        if (maxCountParallelExecutions > 0) {
            while (countParallelExecutions.get() > maxCountParallelExecutions) {
                delay(CountMillisToSleepWhenThereAreTooManyParallelExecutions)
            }

            countParallelExecutions.incrementAndGet()
        }

        val result = commandExecutor.executeCommandSuspendable(config)

        countParallelExecutions.decrementAndGet()

        return result
    }

}