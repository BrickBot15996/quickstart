package brickbot.quickstart.recordautonomous

import brickbot.quickstart.follower.Pose

class RobotState(
    var timestamp: Long,
    var gamepad1State: ByteArray,
    var gamepad2State: ByteArray,
    var pose: Pose
)
