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
		// drbase - clean sine with subtle noise modulation
		SynthDef(\drbase, { |freq=440, amp=0.045, dur=4, pan=0|
			var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 4.5, 0.8, curve: -4), doneAction: 2);
			var sig = SinOsc.ar(freq) * amp * env * XFade2.ar(
				WhiteNoise.ar().range(0.99, 1.0),
				Lag.ar(WhiteNoise.ar, 0.2) * 4,
				Line.kr(-1, 1, dur)
			);
			Out.ar(0, Pan2.ar(sig, pan));
		}).add;

		// drpm - phase modulation with amplitude tremolo
		SynthDef(\drpm, { |out=0, freq=440, amp=0.045, dur=10, atk=0.5, rel=4.5, pan=0, mod=7|
			var dec = dur - (atk + rel);
			var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
			var sig = PMOsc.ar(freq, freq * 0.001, 0.1) * amp * env;
			sig = sig * SinOsc.ar(mod).range(0.5, 1.0) * Lag.ar(WhiteNoise.ar().range(0.8, 1.0));
			Out.ar(0, Pan2.ar(sig, pan));
		}).add;

		// drsoft - soft sine with gentle noise crossfade
		SynthDef(\drsoft, { |freq=440, amp=0.045, dur=4, pan=0|
			var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 4.5, curve: -4), doneAction: 2);
			var sig = SinOsc.ar(freq) * amp * env * XFade2.ar(
				WhiteNoise.ar().range(0.99, 1.0),
				Lag.ar(WhiteNoise.ar, 0.2) * 10,
				Line.kr(0.0, 0.25, dur * 0.1)
			);
			Out.ar(0, Pan2.ar(sig, pan));
		}).add;

		// drgend - gendy-based organic texture
		SynthDef(\drgend, { |freq=440, amp=0.045, dur=4, pan=0|
			var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 2.5, curve: -4), doneAction: 2);
			var sig = BBandPass.ar(
				Gendy5.ar(minfreq: freq * 0.5, maxfreq: freq, initCPs: 24),
				freq,
				0.02,
				5
			) * amp * env;
			Out.ar(0, Pan2.ar(sig, pan));
		}).add;

		// drimp - impulse-based comb filter
		SynthDef(\drimp, { |freq=440, amp=0.045, dur=4, pan=0|
			var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 5.5, curve: -4), doneAction: 2);
			var in = Impulse.ar(10) + (WhiteNoise.ar * 1e-8);
			var delayTime = 1 / freq;
			var decayTime = 0.2;
			var sig = CombC.ar(in, 1, delayTime, decayTime);
			sig = LeakDC.ar(sig);
			sig = sig * amp * env;
			Out.ar(0, Pan2.ar(sig, pan));
		}).add;

		// drdfm - dual formant synthesis with beating
		SynthDef(\drdfm, {
			|freq=440, amp=0.045, dur=4, lffreq=0.1, resfreq=10.1,
			resfrom=0.92, resto=1.018, lpf=50, lpfrq=4, beating=1.08,
			resboost=2400, pan=0|
			var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 4.5, curve: -4), doneAction: 2);
			var sig = BPeakEQ.ar(
				DFM1.ar(
					SinOsc.ar([freq, freq * beating] * LFNoise0.ar(lffreq).range(0.98, 1.15), 0, 0.1),
					resboost,
					SinOsc.kr(resfreq).range(resfrom, resto),
					1, 0, 0.003, 0.5
				),
				lpf, lpfrq, -9
			) * 3 * amp;
			Out.ar(0, sig * env);
		}).add;

		"Ora: 6 SynthDefs added (drbase, drpm, drsoft, drgend, drimp, drdfm)".postln;
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
	plot { |name, bounds, discrete = false, numChannels, minval, maxval, separately, parent, labels|
		items.plot(name, bounds, discrete, numChannels, minval, maxval, separately, parent);
		^this;
	}

	// Print items for debugging
	printItems {
		items.postln;
		^this;
	}
}
