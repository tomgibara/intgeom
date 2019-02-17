package com.tomgibara.intgeom;

import java.util.Optional;
import java.util.stream.IntStream;

public final class IntRange {

	public static final IntRange ZERO_RANGE = new IntRange(0, 0);
	public static final IntRange UNIT_RANGE = new IntRange(0, 1);

	public static IntRange atOrigin(int limit) {
		return limit < 0 ? new IntRange(limit, 0) : new IntRange(0, limit);
	}

	public static IntRange bounded(int a, int b) {
		return new IntRange(Math.min(a, b), Math.max(a, b));
	}

	public static IntRange atMinimum(int min, int size) {
		if (size < 0) throw new IllegalArgumentException("negative size");
		return new IntRange(min, min + size);
	}

	public static IntRange atMaximum(int max, int size) {
		if (size < 0) throw new IllegalArgumentException("negative size");
		return new IntRange(max - size, max);
	}

	public final int min;
	public final int max;

	IntRange(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public int unitSize() {
		return max - min;
	}

	public int pointSize() {
		return max - min + 1;
	}

	public boolean contains(IntRange that) {
		return this.min <= that.min && this.max >= that.max;
	}

	public boolean containsPoint(int point) {
		return min <= point && point <= max;
	}

	public boolean containsUnit(int unit) {
		return min <= unit && unit < max;
	}

	public boolean intersects(IntRange that) {
		return this.min < that.max && this.max > that.min;
	}

	public int clampPoint(int point) {
		return Math.min(Math.max(point, min), max);
	}

	public int clampUnit(int unit) {
		if (min == max) throw new IllegalStateException("empty range");
		return Math.min(Math.max(unit, min), max - 1);
	}

	public int pointOffsetInRange(int point) {
		if (!containsPoint(point)) throw new IllegalArgumentException("point not within range");
		return point - min;
	}

	public int unitOffsetInRange(int unit) {
		if (!containsUnit(unit)) throw new IllegalArgumentException("unit not within range");
		return unit - min;
	}

	public int translatePointOffset(int pointOffset) {
		if (pointOffset < 0) throw new IllegalArgumentException("negative pointOffset");
		if (pointOffset > max - min) throw new IllegalArgumentException("pointOffset too large");
		return pointOffset + min;
	}

	public int translateUnitOffset(int unitOffset) {
		if (unitOffset < 0) throw new IllegalArgumentException("negative unitOffset");
		if (unitOffset >= max - min) throw new IllegalArgumentException("unitOffset too large");
		return unitOffset + min;
	}

	public Optional<IntRange> intersection(IntRange that) {
		int mn = Math.max(this.min, that.min);
		int mx = Math.min(this.max, that.max);
		if (mn > mx) return Optional.empty();
		if (mn == this.min && mx == this.max) return Optional.of(this);
		if (mn == that.min && mx == that.max) return Optional.of(that);
		return Optional.of(new IntRange(mn, mx));
	}

	public IntRect projectAlongX(IntRange xRange) {
		return new IntRect(xRange.min, min, xRange.max, max);
	}

	public IntRect projectAlongY(IntRange yRange) {
		return new IntRect(min, yRange.min, max, yRange.max);
	}

	public IntStream pointStream() {
		return IntStream.range(min, max + 1);
	}

	public IntStream unitStream() {
		return IntStream.range(min, max);
	}

	// object methods

	@Override
	public int hashCode() {
		return 31 * max + min;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof IntRange)) return false;
		IntRange that = (IntRange) obj;
		return this.min == that.min && this.max == that.max;
	}

	@Override
	public String toString() {
		return "[" + min + "," + max + "]";
	}
}
