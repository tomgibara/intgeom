package com.tomgibara.intgeom;

public final class IntCoords {

	// statics

	public static final IntCoords ORIGIN = new IntCoords(0, 0);

	public static IntCoords at(int x, int y) {
		return new IntCoords(x, y);
	}

	public static IntCoords atX(int x) {
		return new IntCoords(x, 0);
	}

	public static IntCoords atY(int y) {
		return new IntCoords(0, y);
	}

	// fields

	public final int x;
	public final int y;

	// constructors

	IntCoords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// methods

	public boolean isOrigin() {
		return x == 0 && y == 0;
	}

	public IntCoords minusOneX() { return new IntCoords(x - 1, y    ); }
	public IntCoords plusOneX () { return new IntCoords(x + 1, y    ); }
	public IntCoords minusOneY() { return new IntCoords(x    , y - 1); }
	public IntCoords plusOneY () { return new IntCoords(x    , y + 1); }

	public IntCoords relativeTo(IntCoords that) {
		if (that == null) throw new IllegalArgumentException("null that");
		return that.isOrigin() ? this : new IntCoords(this.x - that.x, this.y - that.y);
	}

	public IntCoords translatedBy(IntVector vector) {
		if (vector == null) throw new IllegalArgumentException("null vector");
		return vector.isZero() ? this : new IntCoords(x + vector.x, y + vector.y);
	}

	public IntCoords translatedBy(int x, int y) {
		return x == 0 && y == 0 ? this : new IntCoords(this.x + x, this.y + y);
	}

	public IntCoords translatedByNegative(IntVector vector) {
		if (vector == null) throw new IllegalArgumentException("null vector");
		return vector.isZero() ? this : new IntCoords(x - vector.x, y - vector.y);
	}

	public IntCoords translatedByNegative(int x, int y) {
		return x == 0 && y == 0 ? this : new IntCoords(this.x - x, this.y - y);
	}

	public IntCoords scaledUpBy(int s) {
		return s == 1 ? this : new IntCoords(s * x, s * y);
	}

	public IntCoords scaledDownBy(int s) {
		return s == 1 ? this : new IntCoords(x / s, y / s);
	}

	public IntCoords relativeTo(IntRect rect) {
		if (rect == null) throw new IllegalArgumentException("null rect");
		return translatedBy(-rect.minX, rect.minY);
	}

	public IntCoords modulo(IntRect rect) {
		if (rect == null) throw new IllegalArgumentException("null rect");
		if (rect.containsUnit(this)) return this;
		return new IntCoords( Math.floorMod(x - rect.minX, rect.width()) + rect.minX, Math.floorMod(y - rect.minY, rect.height()) + rect.minY );
	}

	public IntVector vectorTo(IntCoords that) {
		if (that == null) throw new IllegalArgumentException("null that");
		return new IntVector(that.x - this.x, that.y - this.y);
	}

	public IntVector vectorFrom(IntCoords that) {
		if (that == null) throw new IllegalArgumentException("null that");
		return new IntVector(this.x - that.x, this.y - that.y);
	}

	public IntVector vectorFromOrigin() {
		return new IntVector(x, y);
	}

	public IntVector vectorToOrigin() {
		return new IntVector(-x, -y);
	}

	// object methods

	@Override
	public int hashCode() {
		return x + 31 * y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof IntCoords)) return false;
		IntCoords that = (IntCoords) obj;
		return this.x == that.x && this.y == that.y;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
