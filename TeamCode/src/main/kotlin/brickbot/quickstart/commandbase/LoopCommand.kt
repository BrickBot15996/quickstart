package brickbot.quickstart.commandbase

class LoopCommand @JvmOverloads constructor(
    commandName: String = "",
    private var command: Command,
    private var loopCount: Int
): Command(commandName) {

    private var commandClone: Command = command.clone()

    /**
     * Runs the command
     * @return true if the command finished, false if it needs to be run again
     */
    override fun run(): Boolean {
        if (loopCount <= 0) {
            return true
        }

        if (command.run()) {
            loopCount--
            command = commandClone
            commandClone = commandClone.clone()
        }

        return false
    }

    /**
     * Clones the command
     * @return the cloned command
     */
    override fun clone(): Command {
        return LoopCommand(commandName, command.clone(), loopCount)
    }

}