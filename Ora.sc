Ora {
	var <>items;

	*new { |array|
		^super.newCopyArgs().init(array);
	}

	init { |array|
		this.items = array;
	}

	// Class method to load all SynthDefs for Ora playback
	*addSynths {
		var synthsFile = this.filenameSymbol.asString.dirname +/+ "OraSynths.scd";
		synthsFile.load;
	}

	// ============ SERIAL METHODS ============

	// Retrograde: reverse the sequence
	retrograde {
		items = items.reverse;
		^this;
	}

	// Inversion: mirror around a pivot point (default: midpoint)
	inversion { |pivot|
		var p = pivot ?? { (items.minItem + items.maxItem) / 2 };
		items = items.collect { |val| p - (val - p) };
		^this;
	}

	// Retrograde Inversion: reverse + invert
	retrogradeInversion { |pivot|
		this.retrograde();
		this.inversion(pivot);
		^this;
	}

	// Rotation: circular shift (positive = right, negative = left)
	rotation { |n = 1|
		items = items.rotate(n.neg);
		^this;
	}

	// Transposition: shift all values by an amount
	transposition { |shift = 0|
		items = items + shift;
		^this;
	}

	// ============ PARAMETRIC TRANSFORMATIONS ============

	// 1) Affine in log-domain (proportional expansion & offset)
	//    scale > 1 expands intervals, < 1 compresses; shift moves the whole band.
	affineLog { |scale = 1.0, shift = 0.0|
		var f = items.clip(1e-6, 1e9);
		var log = f.log;
		items = (log * scale + shift).exp;
		^this;
	}

	// 2) Centered dilation around log-centroid (smooth index weighting)
	//    r > 1 expands away from centroid, < 1 compresses toward it.
	centroidDilation { |r = 1.0, curve = 1.0|
		var f = items.clip(1e-6, 1e9);
		var log = f.log;
		var c = log.mean;
		var n = f.size;
		var w = (0..n-1).linlin(0, n-1, -1, 1).collect { |x|
			(x.abs.pow(curve)).linlin(0, 1, 0, 1)
		};
		var log2 = log.collect { |x, i| c + (x - c) * (1 + (r - 1) * w[i]) };
		items = log2.exp;
		^this;
	}

	// 3) Fractional circular index rotation (continuous index space interp)
	//    frac in [0..1): 0.25 moves everyone a quarter-turn along the array.
	rotate { |frac = 0.0|
		var f = items;
		var n = f.size.asFloat;
		var out = Array.newClear(f.size);
		f.do { |val, i|
			var src = (i - frac * n) % n;
			var i0 = src.floor.asInteger;
			var i1 = (i0 + 1) % f.size;
			var t = src - i0;
			out[i] = f[i0] * (1 - t) + f[i1] * t;
		};
		items = out;
		^this;
	}

	// 4) Projective (Mobius-like) band warp on normalized band
	//    Subtle non-linear remap of positions → frequency via a,b,c,d; then rescale to original range.
	projective { |a = 1, b = 0, c = 0.0, d = 1.0|
		var f = items;
		var lo = f.minItem, hi = f.maxItem;
		var x = (0..f.size-1).linlin(0, f.size-1, 0, 1);
		var y = x.collect { |u| ((a*u + b) / (c*u + d)).clip(0, 1) };
		items = y.collect { |u| u.linlin(0, 1, lo, hi) };
		^this;
	}

	// 5) Interval-flow integrator (continuous "serial" interval field)
	//    Start at first freq; integrate a shaped interval field across the set.
	intervalFlow { |scale = 0.03, ph = 0.0, k = 3.7|
		var f0 = items.copy;
		var n = f0.size;
		var base = f0[0].max(1e-3);
		var intervals = (0..n-2).collect { |i|
			scale * sin(ph + (2pi/k) * i) * base
		};
		var out = Array.fill(n, { 0.0 });
		var lo, hi, olo, ohi;
		out[0] = base;
		intervals.do { |iv, i| out[i+1] = (out[i] + iv).max(1e-3) };
		// match overall min/max back to original band
		lo = f0.minItem; hi = f0.maxItem;
		olo = out.minItem; ohi = out.maxItem;
		items = out.collect { |x| x.linlin(olo, ohi, lo, hi) };
		^this;
	}

	// 6) Barycentric blend with a target ordering/permutation (alpha ∈ [0..1])
	//    Morph continuously between current set and a target array.
	blend { |targetArray, alpha = 0.5|
		var a = items, b = targetArray;
		if(b.isNil or: { b.size != a.size }) {
			"blend: target size mismatch".warn;
			^this;
		};
		items = a.collect { |x, i| x * (1 - alpha) + b[i] * alpha };
		^this;
	}

	// 7) Rank-power warp (proportional distortion by position)
	//    alpha > 1 spreads top ranks more; < 1 bunches them.
	rankPower { |alpha = 1.0|
		var f = items;
		var lo = f.minItem, hi = f.maxItem;
		var n = f.size;
		var x = (0..n-1).collect { |i| (i/(n-1).max(1)) ** alpha };
		items = x.collect { |u| u.linlin(0, 1, lo, hi) };
		^this;
	}

	// 8) Inversion around a chosen (or automatic) center
	//    Classic serial inversion rendered continuously in Hz (or choose your center).
	invert { |center = nil|
		var f = items;
		var c = center ?? { (f.minItem + f.maxItem) * 0.5 };
		items = f.collect { |x| (2*c - x).max(1e-6) };
		^this;
	}

	// 9) Affine index map (i -> m*i + b) with continuous "unwrapping" mix
	//    m smoothly controls a derived ordering; mix ∈ [0..1] blends with original.
	affineIndex { |m = 1.0, b = 0.0, mix = 1.0|
		var f = items;
		var n = f.size.asFloat;
		var out = Array.newClear(f.size);
		(0..f.size-1).do { |i|
			var src = (m * i + b) % n;
			var i0 = src.floor.asInteger;
			var i1 = (i0 + 1) % f.size;
			var t = src - i0;
			var val = f[i0] * (1 - t) + f[i1] * t;
			out[i] = val;
		};
		items = out.collect { |x, i| x * mix + f[i] * (1 - mix) };
		^this;
	}

	// 10) Geometric-mean lock (keeps product of freqs constant; scales everything)
	//     Useful when you dilate/compress but want "energy" center preserved.
	geomeanLock { |geo = nil|
		var f = items.clip(1e-9, 1e9);
		var g = geo ?? { f.log.mean.exp };
		var cur = f.log.mean.exp;
		var k = (g / cur);
		items = f.collect { |x| x * k };
		^this;
	}

	// 11) Optimal-transport-ish morph (CDF match) to target spectrum
	//     Sort both, interpolate positions, then restore original rank shape.
	otMorph { |targetArray, alpha = 0.5|
		var src = items;
		var tgt = targetArray;
		var orderSrc, orderTgt, srcSorted, tgtSorted, blendedSorted, out;

		if(tgt.isNil or: { tgt.size != src.size }) {
			"OTmorph: target size mismatch".warn;
			^this;
		};

		orderSrc = src.order;
		orderTgt = tgt.order;
		srcSorted = src.copy.sort;
		tgtSorted = tgt.copy.sort;
		blendedSorted = srcSorted.collect { |x, i|
			x * (1 - alpha) + tgtSorted[i] * alpha
		};

		// put the blended sorted values back onto the original source rank order
		out = Array.newClear(src.size);
		orderSrc.do { |idx, i| out[idx] = blendedSorted[i] };

		items = out;
		^this;
	}

	// 12) Ratio-lattice snap (continuous attraction to a simple ratio grid)
	//     Pulls freqs toward nearest rational multiples of a base; amt ∈ [0..1].
	ratioSnap { |base = 100, maxNum = 16, maxDen = 16, amt = 0.3|
		var f = items;
		var ratios = Array.fill(maxNum, { |n|
			(1..maxDen).collect { |d| n/d }
		}).flat.select { |r| r.isNumber }.as(Set).asArray.sort;
		items = f.collect { |x|
			var best = ratios.collect { |r| (base * r) }
				.sortBy { |cand| (cand - x).abs }[0];
			x * (1 - amt) + best * amt
		};
		^this;
	}

	shift { |hz = 0, ratio = 1.0|
		items = items.collect { |f| (f * ratio) + hz };
		^this;
	}

	expand { |center = nil, amount = 1.5|
		var c = center ?? { (items.minItem + items.maxItem) * 0.5 };
		items = items.collect { |f| c + ((f - c) * amount) };
		^this;
	}

	contract { |center = nil, amount = 0.5|
		var c = center ?? { (items.minItem + items.maxItem) * 0.5 };
		items = items.collect { |f| c + ((f - c) * amount) };
		^this;
	}

	fluctuate { |amount = 0.05, seed = nil|
		if (seed.notNil) { thisThread.randSeed = seed };
		items = items.collect { |f| f * rrand(1 - amount, 1 + amount) };
		^this;
	}

	mirror { |center = nil|
		var c = center ?? { (items.minItem + items.maxItem) * 0.5 };
		items = items.collect { |f| (2 * c - f).max(1e-6) };
		^this;
	}

	bounce { |low = nil, high = nil|
		var min = low ?? { items.minItem };
		var max = high ?? { items.maxItem };
		items = items.collect { |f|
			var range = max - min;
			var folded = f.fold(min, max);
			folded;
		};
		^this;
	}

	stretch { |power = 2.0, anchor = nil|
		var f = items;
		var a = anchor ?? { f.minItem };
		var maxDist = (f.maxItem - a).abs;
		items = f.collect { |x|
			var dist = x - a;
			var norm = (dist / maxDist).clip(-1, 1);
			var stretched = norm.sign * (norm.abs ** power);
			a + (stretched * maxDist);
		};
		^this;
	}

	fold { |low = nil, high = nil|
		var min = low ?? { items.minItem };
		var max = high ?? { items.maxItem };
		items = items.collect { |f| f.fold(min, max) };
		^this;
	}

	warp { |curve = 1.0, center = nil|
		var f = items;
		var c = center ?? { (f.minItem + f.maxItem) * 0.5 };
		var min = f.minItem, max = f.maxItem;
		items = f.collect { |x|
			var norm = (x - min) / (max - min);
			var warped = norm ** curve;
			min + (warped * (max - min));
		};
		^this;
	}

	rotate { |steps = 1|
		items = items.rotate(steps);
		^this;
	}

	scramble { |seed = nil|
		if (seed.notNil) { thisThread.randSeed = seed };
		items = items.scramble;
		^this;
	}

	reverse {
		items = items.reverse;
		^this;
	}

	pinch { |center = nil, amount = 0.5|
		var c = center ?? { (items.minItem + items.maxItem) * 0.5 };
		items = items.collect { |f|
			var dist = f - c;
			var factor = 1 / (1 + (amount * dist.abs / c));
			c + (dist * factor);
		};
		^this;
	}

	splay { |minGap = 10|
		var sorted = items.copy.sort;
		var adjusted = [sorted[0]];
		(1..sorted.size-1).do { |i|
			var prev = adjusted[i-1];
			var curr = sorted[i];
			if (curr - prev < minGap) {
				adjusted = adjusted.add(prev + minGap);
			} {
				adjusted = adjusted.add(curr);
			};
		};
		var orderMap = items.order;
		var out = Array.newClear(items.size);
		orderMap.do { |idx, i| out[idx] = adjusted[i] };
		items = out;
		^this;
	}

	tilt { |pivot = nil, low = 0.8, high = 1.2|
		var p = pivot ?? { items.size * 0.5 };
		items = items.collect { |f, i|
			var t = i / (items.size - 1).max(1);
			var factor = t.linlin(0, 1, low, high);
			f * factor;
		};
		^this;
	}

	// Formant gap transformer (can be chained with other methods)
	formantGap { |fHole = 480, bw = 140, amount = 0.18|
		var f0 = items;
		var min = f0.minItem, max = f0.maxItem;
		var g = f0.collect { |f|
			var d = (f - fHole) / bw;
			var repulse = 1 / (1 + (d*d));
			f + (f - fHole) * repulse * amount;
		};
		// keep range stable
		var gmin = g.minItem, gmax = g.maxItem;
		items = g.collect { |x| x.linlin(gmin, gmax, min, max) };
		^this;
	}

	// Get a copy of items as an array (for when you want the array value)
	asArray {
		^items.copy;
	}

	// Plot the items
	plot { |name, bounds, discrete = false, numChannels, minval, maxval, separately = false, parent, labels|
		items.plot(name, bounds, discrete, numChannels, minval, maxval, separately, parent);
		^this;
	}

	// Print items for debugging
	printItems {
		items.postln;
		^this;
	}
}
