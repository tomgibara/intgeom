package com.tomgibara.intgeom;

public final class IntVector {

	// statics

	public static IntVector ZERO       = new IntVector( 0,  0);
	public static IntVector UNIT_X     = new IntVector( 1,  0);
	public static IntVector UNIT_NEG_X = new IntVector(-1,  0);
	public static IntVector UNIT_Y     = new IntVector( 0,  1);
	public static IntVector UNIT_NEG_Y = new IntVector( 0, -1);

	public static IntVector to(int x, int y) {
		return new IntVector(x, y);
	}

	public static IntVector toX(int x) {
		switch (x) {
		case -1: return UNIT_NEG_X;
		case  0: return ZERO      ;
		case  1: return UNIT_X    ;
		default: return new IntVector(x, 0);
		}
	}

	public static IntVector toY(int y) {
		switch (y) {
		case -1: return UNIT_NEG_Y;
		case  0: return ZERO      ;
		case  1: return UNIT_Y    ;
		default: return new IntVector(0, y);
		}
	}

	public static IntVector toCoords(IntCoords coord) {
		if (coord == null) throw new IllegalArgumentException("null coord");
		return new IntVector(coord.x, coord.y);
	}

	public static IntVector betweenCoords(IntCoords from, IntCoords to) {
		if (from == null) throw new IllegalArgumentException("null from");
		if (to == null) throw new IllegalArgumentException("null to");
		return new IntVector(to.x - from.x, to.y - from.y);
	}

	public static IntVector alongAxis(IntAxis axis, int scalar) {
		if (axis == null) throw new IllegalArgumentException("null axis");
		switch (scalar) {
		case  0: return ZERO;
		case  1: return axis == IntAxis.X ? UNIT_X : UNIT_Y;
		case -1: return axis == IntAxis.X ? UNIT_NEG_X : UNIT_NEG_Y;
		default: return axis == IntAxis.X ? new IntVector(scalar, 0) : new IntVector(0, scalar);
		}
	}

	// fields

	public final int x;
	public final int y;

	// constructors

	IntVector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// methods

	public boolean isZero() {
		return x == 0 && y == 0;
	}

	public boolean isUnit() {
		return getMagnitudeSqr() == 1;
	}

	public boolean isRectilinear() {
		return x == 0 || y == 0;
	}

	public boolean isParallelToAxis(IntAxis axis) {
		return component(axis.other()) == 0;
	}
	public boolean isParallelToXAxis() {
		return y == 0;
	}

	public boolean isParallelToYAxis() {
		return x == 0;
	}

	public int getMagnitudeSqr() {
		return x*x + y*y;
	}

	public int l0Norm() {
		return Math.abs(Integer.signum(x)) + Math.abs(Integer.signum(y));
	}

	public int l1Norm() {
		return Math.abs(x) + Math.abs(y);
	}

	public int component(IntAxis axis) {
		return axis == IntAxis.X ? x : y;
	}

	public IntVector negate() {
		return isZero() ? this : new IntVector(-x, -y);
	}

	public IntVector add(IntVector that) {
		if (this.isZero()) return that;
		if (that.isZero()) return this;
		return new IntVector(this.x + that.x, this.y + that.y);
	}

	public IntVector subtract(IntVector that) {
		if (this.isZero()) return that;
		if (that.isZero()) return this;
		return new IntVector(this.x - that.x, this.y - that.y);
	}

	public IntCoords translatedOrigin() {
		return isZero() ? IntCoords.ORIGIN : new IntCoords(x, y);
	}

	public IntCoords translatedCoords(IntCoords coords) {
		return isZero() ? coords : new IntCoords(coords.x + x, coords.y + y);
	}

	public IntRect translatedRect(IntRect rect) {
		return isZero() ? rect : new IntRect(rect.minX + x, rect.minY + y, rect.maxX + x, rect.maxY + y);
	}

	public IntRect translatedDimensions(IntDimensions dimensions) {
		return new IntRect(x, y, x + dimensions.width, y + dimensions.height);
	}

	public IntRect translatedDimensions(int width, int height) {
		return new IntRect(x, y, x + width, y + height);
	}

	public IntVector scaled(int scale) {
		if (scale == 0) return ZERO;
		if (scale == 1) return this;
		return new IntVector(x * scale, y * scale);
	}

	public int dot(IntVector that) {
		return this.x * that.x + this.y * that.y;
	}

	// object methods

	@Override
	public int hashCode() {
		return x + 31 * y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof IntVector)) return false;
		IntVector that = (IntVector) obj;
		return this.x == that.x && this.y == that.y;
	}

	@Override
	public String toString() {
		return "<" + x + "," + y + ">";
	}
}
