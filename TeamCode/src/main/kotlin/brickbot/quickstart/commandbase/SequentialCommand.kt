package brickbot.quickstart.commandbase

import kotlin.collections.map

class SequentialCommand @JvmOverloads constructor(
    commandName: String = "",
    private val commands: MutableList<Command>
): Command(commandName) {

    @JvmOverloads
    constructor(commandName: String = "", vararg commands: Command): this(commandName, commands.toMutableList())

    override fun run(): Boolean {
        while (!commands.isEmpty() && commands.first().run()) {
            commands.removeAt(0)
        }

        return commands.isEmpty()
    }

    override fun clone(): Command {
        return ParallelCommand(
            commandName, commands.map { it.clone() }.toMutableList()
        )
    }

}