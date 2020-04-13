package net.dankito.text.extraction

import kotlinx.coroutines.delay
import net.dankito.utils.os.OsHelper
import net.dankito.utils.os.OsType
import net.dankito.utils.os.PackageManager
import net.dankito.utils.os.PackageManagerDetector
import net.dankito.utils.process.*
import java.util.*
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
    protected val maxCountParallelExecutions: Int = CpuInfo.CountCores,
    protected val installHintLocalization: ResourceBundle = ResourceBundle.getBundle("Messages"),
    protected val osHelper: OsHelper = OsHelper(),
    protected val packageManagerDetector: PackageManagerDetector = PackageManagerDetector(commandExecutor)
) : TextExtractorBase() {

    companion object {
        const val UnlimitedParallelExecutions = -1

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

        if (maxCountParallelExecutions > 0) {
            countParallelExecutions.decrementAndGet()
        }

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

        if (maxCountParallelExecutions > 0) {
            countParallelExecutions.decrementAndGet()
        }

        return result
    }


    protected open fun getInstallHintForOsType(installHintBaseKey: String): String {
        val installHintOsKey = getInstallHintMessageKeyForOsType()

        if (installHintOsKey != null) {
            return String.format(installHintLocalization.getString(installHintBaseKey + installHintOsKey), programExecutablePath)
        }

        return String.format(installHintLocalization.getString("install.hint.install.program"), programExecutablePath)
    }

    protected open fun getInstallHintMessageKeyForOsType(): String? {
        val osType = osHelper.osType

        return when (osType) {
            OsType.Windows -> "windows"
            OsType.MacOs -> "macos"
            OsType.Linux -> getInstallHintMessageKeyForLinux()
            else -> null
        }
    }

    protected open fun getInstallHintMessageKeyForLinux(): String? {
        packageManagerDetector.findLinuxPackageInstaller()?.let { packageInstaller ->
            return when (packageInstaller) {
                PackageManager.apt -> "linux.debian"
                PackageManager.dnf -> "linux.redhat"
                PackageManager.zypper -> "linux.suse"
                PackageManager.pacman -> "linux.archlinux"
                else -> null
            }
        }

        return null
    }

}