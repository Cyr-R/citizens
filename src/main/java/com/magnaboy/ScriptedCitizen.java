package com.magnaboy;

import com.magnaboy.Util.AnimData;
import com.magnaboy.scripting.ScriptAction;
import com.magnaboy.scripting.ScriptFile;
import com.magnaboy.scripting.ScriptLoader;
import net.runelite.api.coords.WorldPoint;

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
			long startTime = System.currentTimeMillis();
			this.currentAction = action;
			task.run();
			long endTime = System.currentTimeMillis();
			log("Action " + action.action + " took " + (endTime - startTime) + " milliseconds");
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
	public boolean despawn() {
		scriptExecutor.shutdownNow();
		return super.despawn();
	}

	public void update() {
		if (scriptExecutor.isShutdown()) {
			buildRoutine();
		}
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
			plugin.clientThread.invokeLater(() -> {
				Util.sysLog("Moving action...");
				moveTo(action.targetPosition, action.targetRotation == null ? null : action.targetRotation.getAngle(),
					false, false);
			});
			while (!getWorldLocation().equals(action.targetPosition) ||
				getAnimationID() != idleAnimationId.getId() ||
				WorldPoint.fromLocal(plugin.client, getLocalLocation()).distanceTo2D(getWorldLocation()) > 1) {
				sleep();
			}

			setWait(action.secondsTilNextAction);
		});
	}

	private void addRotateAction(ScriptAction action) {
		submitAction(action, () -> {
			rlObject.setOrientation(action.targetRotation.getAngle());
			setWait(action.secondsTilNextAction);
		});
	}

	private void sleep() {
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void addAnimationAction(ScriptAction action) {
		submitAction(action, () -> {
			AnimData animData = Util.getAnimData(action.animationId.getId());
			int loopCount = action.timesToLoop == null ? 1 : action.timesToLoop;
			for (int i = 0; i < loopCount; i++) {
				setAnimation(action.animationId.getId());
				try {
					Thread.sleep(animData.realDurationMillis);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			setAnimation(idleAnimationId.getId());
			setWait(action.secondsTilNextAction);
		});
	}

	private void setWait(Float seconds) {
		if (seconds == null) {
			return;
		}
		// We never want thread.sleep(0)
		seconds = Math.max(0.1f, seconds);
		try {
			Thread.sleep((long) (seconds * 1000L));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
