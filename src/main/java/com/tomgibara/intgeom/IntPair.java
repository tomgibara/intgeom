package com.tomgibara.intgeom;

public final class IntPair {

	private static final long ONE_X = 0x0000000000000001L;
	private static final long ONE_Y = 0x0000000100000000L;

	public static long fromInts(int x, int y) {
		return (long) y << 32 | (long) x & 0xffffffffL;
	}

	public static long fromCoords(IntCoords coords) {
		return fromInts(coords.x, coords.y);
	}

	public static long fromVector(IntVector vector) {
		return fromInts(vector.x, vector.y);
	}

	public static int xOf(long pair) {
		return (int) pair;
	}

	public static int yOf(long pair) {
		return (int) (pair >> 32);
	}

	public static IntCoords toCoords(long pair) {
		return new IntCoords(xOf(pair), yOf(pair));
	}

	public static IntVector unpackVector(long pair) {
		return new IntVector(xOf(pair), yOf(pair));
	}

	public static long plusOneX (long pair) { return pair + ONE_X; }
	public static long minusOneX(long pair) { return pair - ONE_X; }
	public static long plusOneY (long pair) { return pair + ONE_Y; }
	public static long minusOneY(long pair) { return pair - ONE_Y; }

	public static long translate (long pair, long delta) { return pair + delta;          }
	public static long translateX(long pair, int deltaX) { return pair + deltaX * ONE_X; }
	public static long translateY(long pair, int deltaY) { return pair + deltaY * ONE_Y; }

	public static long movedBy(long pair, IntDir dir) {
		switch (dir) {
		case LESS_X : return minusOneX(pair);
		case MORE_X : return plusOneX (pair);
		case LESS_Y : return minusOneY(pair);
		case MORE_Y : return plusOneY (pair);
		default: throw new IllegalStateException();
		}
	}

	public long movedBy(long pair, IntDir dir, int distance) {
		switch (dir) {
		case LESS_X : return translateX(pair, -distance);
		case MORE_X : return translateX(pair,  distance);
		case LESS_Y : return translateY(pair,  distance);
		case MORE_Y : return translateY(pair, -distance);
		default: throw new IllegalStateException();
		}
	}

	private IntPair() {}
}
