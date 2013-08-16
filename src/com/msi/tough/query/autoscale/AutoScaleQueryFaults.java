package com.msi.tough.query.autoscale;

import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;

public class AutoScaleQueryFaults extends QueryFaults {
	public static ErrorResponse alreadyExists() {
		return new ErrorResponse(
				"Sender",
				"The named AutoScalingGroup or launch configuration already exists.",
				"AlreadyExists");
	}

	public static ErrorResponse cannotReduceCapacity(final long minSize) {
		return new ErrorResponse(
				"Sender",
				"Currently, desiredSize equals minSize ("
						+ minSize
						+ "). Terminating instance without replacement will violate group's min size constraint. Either set shouldDecrementDesiredCapacity flag to false or lower group's min size.",
				"ValidationError");
	}

	public static ErrorResponse desiredCapacityNegative(final int cap) {
		return new ErrorResponse("Sender", "New SetDesiredCapacity value" + cap
				+ " is negative.", "ValidationError");
	}

	public static ErrorResponse groupDoesNotExist() {
		return new ErrorResponse("Sender",
				"The named AutoScalingGroup does not exist.",
				"ASGroupDoesNotExist");
	}

	public static ErrorResponse invalidNextToken() {
		return new ErrorResponse("Sender", "The NextToken value is invalid.",
				"InvalidNextToken");
	}

	public static ErrorResponse launchConfigDoesNotExist() {
		return new ErrorResponse(
				"Sender",
				"The named AutoScalingGroup or launch configuration does not exist.",
				"LaunchConfigDoesNotExist");
	}

	public static ErrorResponse launchConfigInuse(final String grp) {
		return new ErrorResponse(
				"Sender",
				"Cannot delete launch configuration as-config because it is attached to AutoScalingGroup "
						+ grp, "ResourceInUse");
	}

	public static ErrorResponse limitExceeded() {
		return new ErrorResponse(
				"Sender",
				"The quota for capacity groups or launch configurations for this customer has already been reached.",
				"LimitExceeded");
	}

	public static ErrorResponse policyDoesNotExist() {
		return new ErrorResponse("Sender", "The named Policy does not exist.",
				"PolicyDoesNotExist");
	}

	public static ErrorResponse resourceInUse() {
		return new ErrorResponse(
				"Sender",
				"This is returned when you cannot delete a launch configuration or auto scaling group because it is being used.",
				"ResourceInUse");
	}

	public static ErrorResponse scalingActivityInProgress() {
		return new ErrorResponse(
				"Sender",
				"You cannot delete an AutoScalingGroup while there are scaling activities in progress for that group.",
				"ScalingActivityInProgress");
	}

	public static ErrorResponse scheduledActionDoesNotExist() {
		return new ErrorResponse("Sender",
				"The named ScheduledAction does not exist.",
				"ScheduledActionDoesNotExist");
	}
}
