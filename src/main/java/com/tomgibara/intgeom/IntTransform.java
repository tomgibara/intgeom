package com.tomgibara.intgeom;

// those affine transforms that are guaranteed to preserve 'integerness'
public final class IntTransform implements IntTransformable<IntTransform> {

	private static final int ORIGIN_PRESERVING = 1; //  m02 & m12 == 0
	private static final int SKEW_PRESERVING = 2;   //  m10 & m01 == 0
	private static final int SCALE_PRESERVING = 4;  //  m00 * m11 - m10 * m01 == 1

	private static final int APPLY_MASK          = ORIGIN_PRESERVING | SKEW_PRESERVING | SCALE_PRESERVING;

	private static final int CHIRAL_PRESERVING = 8;  //  m00 * m11 - m10 * m01 > 0
	private static final int CIRCLE_PRESERVING = 16;  //  m10 == -m01 and m00 == m11
	private static final int RECTILNEAR_PRESERVING = 32; // perpendicular only rotation
	private static final int RIGHT_MASK =    ORIGIN_PRESERVING | /*maybe skew p*/  SCALE_PRESERVING | CHIRAL_PRESERVING | CIRCLE_PRESERVING | RECTILNEAR_PRESERVING;
	private static final int IDENTITY_MASK = RIGHT_MASK | SKEW_PRESERVING;

	private static final IntTransform IDENTITY = new IntTransform( 1,  0,  0,  1,  0,  0, IDENTITY_MASK );
	private static final IntTransform ROT_90 =   new IntTransform( 0, -1,  1,  0,  0,  0, RIGHT_MASK    );
	private static final IntTransform ROT_180 =  new IntTransform(-1,  0,  0, -1,  0,  0, IDENTITY_MASK );
	private static final IntTransform ROT_270 =  new IntTransform( 0,  1,  0, -1,  0,  0, RIGHT_MASK    );

	public static IntTransform identity() {
		return IDENTITY;
	}

	public static IntTransform rotateRightAngles(int quarterTurns) {
		switch (quarterTurns & 3) {
		case  0 : return IDENTITY;
		case  1 : return ROT_90;
		case  2 : return ROT_180;
		case  3 : return ROT_270;
		default : throw new IllegalStateException();
		}
	}

	public static IntTransform translation(int x, int y) {
		if (x == 0 && y == 0) return IDENTITY;
		return new IntTransform(1, 0, 0, 1, x, y, SKEW_PRESERVING | SCALE_PRESERVING | CHIRAL_PRESERVING | CIRCLE_PRESERVING | RECTILNEAR_PRESERVING);
	}

	public static IntTransform rotationAbout(IntCoords pt, int quarterTurns) {
		// trivial cases
		if (pt.isOrigin()) return rotateRightAngles(quarterTurns);
		if ((quarterTurns & 3) == 0) return IDENTITY;
		//TODO make an optimize version of this
		IntTransform a = IntTransform.translation(-pt.x, -pt.y);
		IntTransform b = IntTransform.rotateRightAngles(quarterTurns);
		IntTransform c = IntTransform.translation(pt.x, pt.y);
		return a.apply(b).apply(c);
	}

	public static IntTransform scale(int s) {
		if (s == 0) throw new IllegalArgumentException("non-invertible");
		if (s == 1) return IDENTITY;
		int flags = ORIGIN_PRESERVING | SKEW_PRESERVING | CHIRAL_PRESERVING | CIRCLE_PRESERVING | RECTILNEAR_PRESERVING;
		if (s == -1) flags |= SCALE_PRESERVING;
		return new IntTransform(s, 0, 0, s, 0, 0, flags);
	}

	public static IntTransform scale(int sx, int sy) {
		if (sx == sy) return scale(sx);
		return scaleAbout(IntCoords.ORIGIN, sx, sy);
	}
	
	public static IntTransform scaleAbout(IntCoords pt, int sx, int sy) {
		if (sx == 0f || sy == 0f) throw new IllegalArgumentException("non-invertible");
		if (sx == 1f && sy == 1f) return IDENTITY;
		int flags = SKEW_PRESERVING | RECTILNEAR_PRESERVING;
		if (pt.isOrigin()) flags |= ORIGIN_PRESERVING;
		if (sx > 0 == sy > 0) flags |= CHIRAL_PRESERVING;
		return new IntTransform(sx, 0, 0, sy, (1 - sx) * pt.x, (1 - sy) * pt.y, flags);
	}

	public static IntTransform components(int m00, int m10, int m01, int m11, int m02, int m12) {
		return new IntTransform(m00, m10, m01, m11, m02, m12);
	}

	private final int flags;

	public final int m00; // scale x
	public final int m10; // shear y
	public final int m01; // shear x
	public final int m11; // scale y
	public final int m02; // translate x
	public final int m12; // translate y

	private IntTransform(int m00, int m10, int m01, int m11, int m02, int m12) {
		this.m00 = m00;
		this.m10 = m10;
		this.m01 = m01;
		this.m11 = m11;
		this.m02 = m02;
		this.m12 = m12;

		int det = m00 * m11 - m10 * m01;
		if (det == 0f) throw new IllegalArgumentException("non-invertible transform");
		if (Float.isInfinite(det) || Float.isInfinite(m02) || Float.isInfinite(m12)) throw new IllegalArgumentException("overflowing transform");
		if (Float.isNaN(det) || Float.isNaN(m02) || Float.isNaN(m12)) throw new IllegalArgumentException("invalid transform");

		int flags = 0;
		if (m02 == 0f && m12 == 0f) flags |= ORIGIN_PRESERVING;
		if (m10 == 0f && m01 == 0f) flags |= SKEW_PRESERVING;
		if (Math.abs(det) == 1) flags |= SCALE_PRESERVING;
		if (m10 == -m01 && m00 == m11) flags |= CIRCLE_PRESERVING;
		if (det < 0f) flags |= CHIRAL_PRESERVING;
		if (m10 == 0f && m01 == 0f) flags |= RECTILNEAR_PRESERVING;
		this.flags = flags;
	}

	private IntTransform(int m00, int m10, int m01, int m11, int m02, int m12, int flags) {
		this.m00 = m00;
		this.m10 = m10;
		this.m01 = m01;
		this.m11 = m11;
		this.m02 = m02;
		this.m12 = m12;
		this.flags = flags;
	}

	public boolean isIdentity() {
		return (flags & IDENTITY_MASK) == IDENTITY_MASK && m00 == 1;
	}

	public boolean isOriginPreserving() {
		return (flags & ORIGIN_PRESERVING) == ORIGIN_PRESERVING;
	}

	public boolean isSkewPreserving() {
		return (flags & SKEW_PRESERVING) == SKEW_PRESERVING;
	}

	public boolean isScalePreserving() {
		return (flags & SCALE_PRESERVING) == SCALE_PRESERVING;
	}

	public boolean isRectilinearPreserving() {
		return (flags & RECTILNEAR_PRESERVING) == RECTILNEAR_PRESERVING;
	}

	public boolean isChiralPreserving() {
		return (flags & CHIRAL_PRESERVING) == CHIRAL_PRESERVING;
	}

	public boolean isCirclePreserving() {
		return (flags & CIRCLE_PRESERVING) == CIRCLE_PRESERVING;
	}

	public int[] getComponents() {
		return new int[] { m00, m10, m01, m11, m02, m12 };
	}

	public int getTrace() {
		return m00 + m11;
	}
	
	public int getDeterminant() {
		return m00 * m11 - m10 * m01;
	}
	
	public IntVector getColumn(int index) {
		switch (index) {
		case 0 : return new IntVector(m00, m10);
		case 1 : return new IntVector(m01, m11);
		case 2 : return new IntVector(m02, m12);
		default: throw new IllegalArgumentException("invalid index");
		}
	}
	
	public long transform(long pair) {
		return isIdentity() ? pair : transformImpl(pair);
	}

	public IntCoords transform(IntCoords coords) {
		return isIdentity() ? coords : IntPair.toCoords(transformImpl(IntPair.fromCoords(coords)));
	}

	// this times t
	public IntTransform preApply(IntTransform t) {
		if (isIdentity()) return t;
		if (t.isIdentity()) return this;

		// TODO could apply many optimizing cases
		return new IntTransform(
				m00 * t.m00 + m01 * t.m10,
				m10 * t.m00 + m11 * t.m10,
				m00 * t.m01 + m01 * t.m11,
				m10 * t.m01 + m11 * t.m11,
				m00 * t.m02 + m01 * t.m12 + m02,
				m10 * t.m02 + m11 * t.m12 + m12,
				flags & t.flags
				);
	}

	@Override
	public IntTransform apply(IntTransform t) {
		return t.preApply(this);
	}

	private long transformImpl(long pair) {
		int x = IntPair.xOf(pair);
		int y = IntPair.yOf(pair);

		switch (flags & APPLY_MASK) {
		// non-translations

		case ORIGIN_PRESERVING | SKEW_PRESERVING | SCALE_PRESERVING :
			break;
		case ORIGIN_PRESERVING | SKEW_PRESERVING :
			x = x * m00;
			y = y * m11;
			break;
		case ORIGIN_PRESERVING | SCALE_PRESERVING :
		case ORIGIN_PRESERVING :
			x = x * m00 + y * m01;
			y = x * m10 + y * m11;
			break;

		// translations

		case SKEW_PRESERVING | SCALE_PRESERVING :
			x = x + m02;
			y = y + m12;
			break;
		case SKEW_PRESERVING :
			x = x * m00 + m02;
			y = y * m11 + m12;
			break;
		case SCALE_PRESERVING :
			default :
				x = x * m00 + y * m01 + m02;
				y = x * m10 + y * m11 + m12;
				break;
		}
		return IntPair.fromInts(x,y);
	}

}
