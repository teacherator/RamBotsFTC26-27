
package org.firstinspires.ftc.teamcode;

public class BioBuzzNavigationMath {

    // =========================================================
    // MOVEMENT ACTION TYPES
    // =========================================================

    public enum ActionType {
        TURN_TO_HEADING,
        DRIVE_FORWARD,
        STOP
    }

    // =========================================================
    // ONE EXECUTABLE MOVEMENT ACTION
    // =========================================================

    public static class MovementAction {
        public final ActionType type;
        public final double value;

        public MovementAction(ActionType type, double value) {
            this.type = type;
            this.value = value;
        }
    }

    // =========================================================
    // COMPLETE MOVEMENT PLAN
    // =========================================================

    public static class MovementPlan {

        public final double startX;
        public final double startY;
        public final double startHeading;

        public final double targetX;
        public final double targetY;

        public final double targetAngle;
        public final double signedTurn;
        public final double turnAmount;
        public final String turnDirection;
        public final double distance;

        public final MovementAction[] actions;

        private MovementPlan(
                double startX,
                double startY,
                double startHeading,
                double targetX,
                double targetY,
                double targetAngle,
                double signedTurn,
                double turnAmount,
                String turnDirection,
                double distance,
                MovementAction[] actions
        ) {
            this.startX = startX;
            this.startY = startY;
            this.startHeading = startHeading;

            this.targetX = targetX;
            this.targetY = targetY;

            this.targetAngle = targetAngle;
            this.signedTurn = signedTurn;
            this.turnAmount = turnAmount;
            this.turnDirection = turnDirection;
            this.distance = distance;

            this.actions = actions;
        }
    }

    // =========================================================
    // CALCULATE TARGET HEADING
    //
    // Coordinate convention:
    // 0 degrees   = East
    // 90 degrees  = North
    // 180 degrees = West
    // 270 degrees = South
    // =========================================================

    public static double calculateTargetAngle(
            double robotX,
            double robotY,
            double targetX,
            double targetY
    ) {
        double dx = targetX - robotX;
        double dy = targetY - robotY;

        double angle = Math.toDegrees(
                Math.atan2(dy, dx)
        );

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    // =========================================================
    // CALCULATE SHORTEST SIGNED TURN
    //
    // Positive = LEFT in the field coordinate system.
    // Negative = RIGHT in the field coordinate system.
    // =========================================================

    public static double angleDifference(
            double targetAngle,
            double currentAngle
    ) {
        double difference = targetAngle - currentAngle;

        while (difference > 180) {
            difference -= 360;
        }

        while (difference < -180) {
            difference += 360;
        }

        return difference;
    }

    // =========================================================
    // CALCULATE DISTANCE BETWEEN TWO POINTS
    // =========================================================

    public static double calculateDistance(
            double robotX,
            double robotY,
            double targetX,
            double targetY
    ) {
        double dx = targetX - robotX;
        double dy = targetY - robotY;

        return Math.sqrt(dx * dx + dy * dy);
    }

    // =========================================================
    // CALCULATE TURN POWER
    // =========================================================

    public static double getTurnPower(double error) {
        double power = error * 0.01;

        return Math.max(
                -0.5,
                Math.min(0.5, power)
        );
    }

    // =========================================================
    // DRIVE POWER
    // =========================================================

    public static double getDrivePower() {
        return 0.6;
    }

    // =========================================================
    // CREATE THE MOVEMENT PLAN
    // =========================================================

    public static MovementPlan createMovementPlan(
            double robotX,
            double robotY,
            double robotHeading,
            double targetX,
            double targetY
    ) {

        // Calculate heading toward target.
        double targetAngle = calculateTargetAngle(
                robotX,
                robotY,
                targetX,
                targetY
        );

        // Calculate shortest turn.
        double signedTurn = angleDifference(
                targetAngle,
                robotHeading
        );

        double turnAmount = Math.abs(signedTurn);

        String turnDirection;

        if (signedTurn > 0) {
            turnDirection = "LEFT";
        } else if (signedTurn < 0) {
            turnDirection = "RIGHT";
        } else {
            turnDirection = "NONE";
        }

        // Calculate straight-line distance.
        double distance = calculateDistance(
                robotX,
                robotY,
                targetX,
                targetY
        );

        // Create the ordered actions for AutoNavigation.
        MovementAction[] actions = new MovementAction[] {
                new MovementAction(
                        ActionType.TURN_TO_HEADING,
                        targetAngle
                ),
                new MovementAction(
                        ActionType.DRIVE_FORWARD,
                        distance
                ),
                new MovementAction(
                        ActionType.STOP,
                        0
                )
        };

        return new MovementPlan(
                robotX,
                robotY,
                robotHeading,
                targetX,
                targetY,
                targetAngle,
                signedTurn,
                turnAmount,
                turnDirection,
                distance,
                actions
        );
    }
}

