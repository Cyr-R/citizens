package com.magnaboy;

import com.magnaboy.scripting.ScriptAction;
import com.magnaboy.scripting.ScriptFile;
import com.magnaboy.scripting.ScriptLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptedCitizen extends Citizen<ScriptedCitizen> {
	private ScriptFile script;
	private ExecutorService scriptExecutor;
	public ScriptAction currentAction;

	public ScriptedCitizen(CitizensPlugin plugin) {
		super(plugin);
		entityType = EntityType.ScriptedCitizen;
	}

	private void submitAction(ScriptAction action, Runnable task) {
		scriptExecutor.submit(() -> {
			this.currentAction = action;
			task.run();
		});
	}

	public ScriptedCitizen setScript(String scriptName) {
		if (scriptName == null || scriptName.isEmpty()) {
			return this;
		}
		this.script = ScriptLoader.loadScript(scriptName);
		buildRoutine();
		return this;
	}

	@Override
	public boolean spawn() {
		boolean didSpawn = super.spawn();
		return didSpawn;
	}

	@Override
	public boolean despawn() {
		scriptExecutor.shutdownNow();
		return super.despawn();
	}

	@Override
	public void update() {
		super.update();
	}

	private void buildRoutine() {
		if (script == null) {
			return;
		}
		scriptExecutor = Executors.newSingleThreadExecutor();
		for (ScriptAction action : script.actions) {
			addAction(action);
		}
		scriptExecutor.submit(this::buildRoutine);
	}

	private void addAction(ScriptAction action) {
		if (action != null) {
			switch (action.action) {
				case Idle:
					submitAction(action, () -> {
						setWait(action.secondsTilNextAction);
					});
					break;
				case Say:
					addSayAction(action);
					break;
				case WalkTo:
					addWalkAction(action);
					break;
				case Animation:
					addAnimationAction(action);
					break;
				case FaceDirection:
					addRotateAction(action);
					break;
				default:
					Util.log("Unknown action type");
					break;
			}
		}
	}

	private void addSayAction(ScriptAction action) {
		submitAction(action, () -> {
			say(action.message);
			setWait(action.secondsTilNextAction);
		});
	}

	private void addWalkAction(ScriptAction action) {
		submitAction(action, () -> {
			if (action.targetRotation != null) {
				moveTo(action.targetPosition, action.targetRotation.getAngle(), rlObject.getAnimation().getId(), false, false, false);
			} else {
				moveTo(action.targetPosition);
			}

			while (!getWorldLocation().equals(action.targetPosition)) {
				Thread.yield();
			}
			setWait(action.secondsTilNextAction);
		});
	}

	private void addRotateAction(ScriptAction action) {
		submitAction(action, () -> {
//			rlObject.setOrientation(action.targetRotation.getAngle());
			moveTo(worldLocation, action.targetRotation.getAngle(), rlObject.getAnimation().getId(), false, false, false);
			while (!getWorldLocation().equals(action.targetPosition) || rlObject.getOrientation() != action.targetRotation.getAngle()) {
				Thread.yield();
			}
			setWait(action.secondsTilNextAction);
		});
	}

	private void addAnimationAction(ScriptAction action) {
		submitAction(action, () -> {
			AnimationID oldAnimation = idleAnimationId;
			setIdleAnimation(action.animationId);
			//Switching animations is not immedidate, so wait til it switches.
			while (rlObject.getAnimation().getId() != action.animationId.getId()) {
				Thread.yield();
			}

			int lastFrame = 0;
			while (lastFrame <= rlObject.getAnimationFrame()) {
				//While the last frame is less or equal to the current animation frame
				//Check if the frame changed, and if so, set the last frame to that frame number
				//Once the real frame reaches zero the while loop condition becomes false, unlocking the thread.
				if (lastFrame != rlObject.getAnimationFrame()) {
					lastFrame = rlObject.getAnimationFrame();
				}
				Thread.yield();
			}
			if (!action.loopAnimation) {
				setIdleAnimation(oldAnimation);
			}
			setWait(action.secondsTilNextAction);
		});
	}

	private void setWait(Float seconds) {
		if (seconds == null) {
			return;
		}
		//We never want thread.sleep(0)
		seconds = Math.max(0.1f, seconds);
		try {
			Thread.sleep((long) (seconds * 1000L));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
