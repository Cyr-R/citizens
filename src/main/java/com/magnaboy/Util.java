package com.magnaboy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.logging.Logger;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.Perspective;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public final class Util {
	public final static int TILES_WALKED_PER_GAME_TICK = 1;
	public final static int GAME_TICK_MILLIS = 600;
	private final static Logger logger = Logger.getLogger("Citizens");
	public static Random rng = new Random();
	public final static int JAU_FULL_ROTATION = 2048;
	public static final int[][] BLOCKING_DIRECTIONS_5x5 = {
		{CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST},
		{CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST, CollisionDataFlag.BLOCK_MOVEMENT_SOUTH_WEST},
		{CollisionDataFlag.BLOCK_MOVEMENT_EAST, CollisionDataFlag.BLOCK_MOVEMENT_EAST, 0, CollisionDataFlag.BLOCK_MOVEMENT_WEST, CollisionDataFlag.BLOCK_MOVEMENT_WEST},
		{CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_NORTH, CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST, CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST},
		{CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_NORTH_EAST, CollisionDataFlag.BLOCK_MOVEMENT_NORTH, CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST, CollisionDataFlag.BLOCK_MOVEMENT_NORTH_WEST}};

	public static final int[][] JAU_DIRECTIONS_5X5 = {
		{768, 768, 1024, 1280, 1280},
		{768, 768, 1024, 1280, 1280},
		{512, 512, 0, 1536, 1536},
		{256, 256, 0, 1792, 1792},
		{256, 256, 0, 1792, 1792}};
	public static final int CENTER_INDEX_5X5 = 2;

	// Prevent instantiation
	private Util() {
	}

	public static int getRandom(int min, int max) {
		if (min == max) {
			return min;
		}
		return rng.nextInt((max - min) + 1) + min;
	}

	public static <T> T getRandomItem(T[] items) {
		int index = rng.nextInt(items.length);
		return items[index];
	}

	public static int getRandomItem(int[] items) {
		int index = rng.nextInt(items.length);
		return items[index];
	}

	public static int radToJau(double a) {
		int j = (int) Math.round(a / Perspective.UNIT);
		return j & 2047;
	}

	public static void log(String message) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("citizens.log", true)))) {
			out.println(message);
		} catch (IOException e) {
			System.err.println("Error occurred while logging: " + e.getMessage());
		}
	}

	public static float truncateFloat(int digits, float number) {
		BigDecimal bd = new BigDecimal(Float.toString(number));
		bd = bd.setScale(digits, RoundingMode.DOWN);
		return bd.floatValue();
	}

	public static String worldPointToShortCoord(WorldPoint point) {
		return String.format("%d, %d, %d", point.getX(), point.getY(), point.getPlane());
	}

	public static String intArrayToString(int[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i]);
			if (i < array.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString().replaceAll("\\s+", "");
	}

	public static WorldArea calculateBoundingBox(WorldPoint bottomLeft, WorldPoint topRight) {
		int width = Math.abs(bottomLeft.getX() - topRight.getX());
		int height = Math.abs(bottomLeft.getY() - topRight.getY());
		String debugString = "BottomLeft[" + bottomLeft + "] TopRight[" + topRight + "] Width[" + width + "] Height[" + height + "]";

		if (bottomLeft.getX() > topRight.getX() || bottomLeft.getY() > topRight.getY()) {
			throw new IllegalArgumentException("BottomLeft must be to the bottom/left of topRight. " + debugString);
		}

		if (width <= 1 && height <= 1) {
			throw new IllegalArgumentException("The size of the bounding box must be greater than 1x1. " + debugString);
		}

		return new WorldArea(bottomLeft, width, height);
	}
}
