package net.dankito.text.extraction.pdf


open class ExecuteCommandResult(val exitCode: Int, val output: String, val errors: String) {

    val successful: Boolean = exitCode == 0


    override fun toString(): String {
        return "Successful? $successful ($exitCode): $output. Errors: $errors"
    }

}