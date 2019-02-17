package com.tomgibara.intgeom;

public final class IntMargins {

	// statics

	private static final IntMargins VOID = new IntMargins(0,0,0,0);

	public static IntMargins voided() {
		return VOID;
	}

	public static IntMargins offsets(int minX, int maxX, int minY, int maxY) {
		return minX == 0 && maxX == 0 && minY == 0 && maxY == 0 ? VOID : new IntMargins(minX, maxX, minY, maxY);
	}

	public static IntMargins widths(int minX, int maxX, int minY, int maxY) {
		return minX == 0 && maxX == 0 && minY == 0 && maxY == 0 ? VOID : new IntMargins(-minX, maxX, -minY, maxY);
	}

	public static IntMargins uniform(int width) {
		return width == 0 ? VOID : new IntMargins(-width, width, -width, width);
	}

	public static IntMargins translation(IntVector vector) {
		return vector.isZero() ? VOID : new IntMargins(vector.x, vector.x, vector.y, vector.y);
	}

	// fields

	public final int minX;
	public final int maxX;
	public final int minY;
	public final int maxY;

	// constructors

	private IntMargins(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public boolean isVoid() {
		return this == VOID;
	}

	public boolean isExpanding() {
		return minX <= 0 && maxX >= 0 && minY <= 0 && maxY >= 0;
	}

	public boolean isStrictlyExpanding() {
		return minX < 0 && maxX > 0 && minY < 0 && maxY > 0;
	}

	public boolean isContracting() {
		return minX >= 0 && maxX <= 0 && minY >= 0 && maxY <= 0;
	}

	public boolean isStrictlyContracting() {
		return minX > 0 && maxX < 0 && minY > 0 && maxY < 0;
	}

	public IntMargins inverted() {
		return this == VOID ? this : new IntMargins(-minX, -maxX, -minY, -maxY);
	}

	public IntMargins plus(IntMargins that) {
		if (this == VOID) return that;
		if (that == VOID) return this;
		return offsets(
				this.minX + that.minX,
				this.maxX + that.maxX,
				this.minY + that.minY,
				this.maxY + that.maxY
				);
	}

	public IntMargins minus(IntMargins that) {
		if (this == VOID) return that.inverted();
		if (that == VOID) return this;
		return offsets(
				this.minX - that.minX,
				this.maxX - that.maxX,
				this.minY - that.minY,
				this.maxY - that.maxY
				);
	}

	public IntMargins largerOf(IntMargins that) {
		return offsets(
				Math.min(this.minX, that.minX),
				Math.max(this.maxX, that.maxX),
				Math.min(this.minY, that.minY),
				Math.max(this.maxY, that.maxY)
				);
	}

	public IntMargins smallerOf(IntMargins that) {
		return offsets(
				Math.max(this.minX, that.minX),
				Math.min(this.maxX, that.maxX),
				Math.max(this.minY, that.minY),
				Math.min(this.maxY, that.maxY)
				);
	}

	public IntVector offset() {
		return minX == 0 && minY == 0 ? IntVector.ZERO : new IntVector(-minX, -minY);
	}

	// object methods

	@Override
	public int hashCode() {
		return minX + (31 * maxX + (31 * minY + (31 * maxY)));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof IntMargins)) return false;
		IntMargins that = (IntMargins) obj;
		return
				this.minX == that.minX &&
				this.maxX == that.maxX &&
				this.minY == that.minY &&
				this.maxY == that.maxY;
	}

	@Override
	public String toString() {
		return "mX: " + minX + ", MX:" + maxX + ", mY:" + minY + ", MY:" + maxY; 
	}

}
