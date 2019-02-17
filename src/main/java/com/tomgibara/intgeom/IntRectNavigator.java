package com.tomgibara.intgeom;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class IntRectNavigator<T> {

	public static enum Algorithm {
		NATURAL, // based on distance between centres of edges in specified direction
		STRICT, // as natural, but requires contained bounds perpendicular to direction
		PREFER_STRICT; // first attempts STRICT, then NATURAL
	}

	private static int naturalDistance(IntRect from, IntRect to, IntDir dir) {
		if (from.intersectsRect(to)) return 0;
		IntCoords a = from.edgeCenter(dir);
		IntCoords b = to.edgeCenter(dir.reverse());
		return a.vectorTo(b).l1Norm();
	}

	public static class TaggedRect<T> {

		public final IntRect rect;
		public final T tag;

		public TaggedRect(IntRect rect, T tag) {
			if (rect == null) throw new IllegalArgumentException("null rect");
			this.rect = rect;
			this.tag = tag;
		}

		@Override
		public String toString() {
			return tag + "@" + rect;
		}
	}

	private static final Map<IntDir, Comparator<TaggedRect<?>>> comparators = new EnumMap<>(IntDir.class);
	static {
		for (IntDir dir : IntDir.values()) {
			comparators.put(dir, Comparator.comparing(r -> r.rect, dir.rectComparator));
		}
	}

	private final TaggedRect<T>[] array;
	private final Map<IntDir, TaggedRect<T>[]> arrays;

	public IntRectNavigator(TaggedRect<T>... rects) {
		this(Arrays.asList(rects));
	}

	public IntRectNavigator(Collection<TaggedRect<T>> rects) {
		if (rects == null) throw new IllegalArgumentException("null rects");
		if (rects.contains(null)) throw new IllegalArgumentException("null rect");
		array = (TaggedRect[]) rects.toArray(new TaggedRect<?>[rects.size()]);
		arrays = new EnumMap<>(IntDir.class); // lazily populated
	}

	public Optional<TaggedRect<T>> findFrom(IntRect start, IntDir dir, Algorithm algo) {
		if (start == null) throw new IllegalArgumentException("null start");
		if (dir == null) throw new IllegalArgumentException("null dir");
		if (algo == null) throw new IllegalArgumentException("null algo");

		switch (algo) {
		case NATURAL      : return Optional.ofNullable(findFrom(start, dir, false));
		case STRICT       : return Optional.ofNullable(findFrom(start, dir, true ));
		case PREFER_STRICT:
			TaggedRect<T> rect = findFrom(start, dir, true);
			return rect == null ? Optional.ofNullable(findFrom(start, dir, false)) : Optional.of(rect);
		default:
			throw new IllegalStateException();
		}
	}

	public List<TaggedRect<T>> allTaggedRects() {
		return Collections.unmodifiableList(Arrays.asList(array));
	}

	private TaggedRect<T>[] array(IntDir dir) {
		return arrays.computeIfAbsent(dir, d -> {
			TaggedRect<T>[] a = array.clone();
			Arrays.sort(a, comparators.get(dir));
			return a;
		});
	}

	private TaggedRect<T> findFrom(IntRect start, IntDir dir, boolean strict) {
		IntAxis axis = dir.axis;
		TaggedRect<T>[] array = array(dir);
		TaggedRect<T> best = null;
		int leastDist = Integer.MAX_VALUE;
		for (int i = array.length - 1; i >= 0; i--) {
			TaggedRect<T> tr = array[i];
			int gap = dir.gap(start, tr.rect);
			if (gap < 0) continue; // we've gone backwards, so we're out of candidates (or not??) //TODO fix this
			if (strict && !start.projectAlongAxis(axis).intersects(tr.rect.projectAlongAxis(axis))) continue;
			//TODO should improve on simply being the closest in gap
			int dist = naturalDistance(start, tr.rect, dir);
			if (dist < leastDist) {
				best = tr;
				leastDist = dist;
			}
		}
		return best;
	}
}
