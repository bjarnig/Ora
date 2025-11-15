OraPlay {
	var <>ora;
	var <>routine;

	*new { |oraObject|
		^super.newCopyArgs(oraObject);
	}

	// Helper: stereo spread (index → pan)
	spread { |n|
		^(0..(n-1)).linlin(0, n-1, -0.85, 0.85);
	}

	// Stop current routine
	stop {
		if (routine.notNil) {
			routine.stop;
			routine = nil;
		};
	}

	// Get the frequency array from Ora object and filter valid range
	getFreqs {
		var freqs = if (ora.isKindOf(Ora)) { ora.items } { ora };
		// Filter frequencies: remove below 40 Hz and above 14000 Hz
		^freqs.select { |f| (f >= 40) and: (f <= 14000) };
	}

	// --- Play cluster ascending (default)
	cluster { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			arr.do { |f, i|
				Synth(synthdef, [\freq, f, \amp, amp, \dur, dur, \pan, pans[i]]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster in reverse (descending)
	reverse { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			arr.reverse.do { |f, i|
				var panIdx = arr.size - 1 - i;
				Synth(synthdef, [\freq, f, \amp, amp, \dur, dur, \pan, pans[panIdx]]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster in random order
	random { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			var shuffled = arr.scramble;
			shuffled.do { |f, i|
				var origIdx = arr.indexOf(f);
				Synth(synthdef, [\freq, f, \amp, amp, \dur, dur, \pan, pans[origIdx]]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster from center outward (alternating)
	fromCenter { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			var mid = (arr.size / 2).floor;
			var indices = [];

			// Build alternating pattern from center
			(arr.size / 2).ceil.do { |i|
				indices = indices.add(mid + i);
				if (mid - i - 1 >= 0) { indices = indices.add(mid - i - 1) };
			};

			indices.do { |idx|
				Synth(synthdef, [\freq, arr[idx], \amp, amp, \dur, dur, \pan, pans[idx]]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster as overlapping cloud (dense)
	dense { |amp=0.035, dur=6, synthdef=\drsoft, offset=0.05|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			arr.do { |f, i|
				Synth(synthdef, [\freq, f, \amp, amp, \dur, dur, \pan, pans[i]]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster in pairs simultaneously
	pairs { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.25|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			(arr.size / 2).floor.do { |i|
				Synth(synthdef, [\freq, arr[i * 2], \amp, amp, \dur, dur, \pan, pans[i * 2]]);
				if ((i * 2 + 1) < arr.size) {
					Synth(synthdef, [\freq, arr[i * 2 + 1], \amp, amp, \dur, dur, \pan, pans[i * 2 + 1]]);
				};
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster as wave (up then down)
	wave { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);

			// Ascending
			arr.do { |f, i|
				Synth(synthdef, [\freq, f, \amp, amp, \dur, dur, \pan, pans[i]]);
				offset.wait;
			};

			(dur * 0.5).wait; // pause at peak

			// Descending
			arr.reverse.do { |f, i|
				var panIdx = arr.size - 1 - i;
				Synth(synthdef, [\freq, f, \amp, amp, \dur, dur, \pan, pans[panIdx]]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster with random sparse timing
	sparse { |amp=0.045, dur=4, synthdef=\drsoft, minWait=0.2, maxWait=0.8|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			arr.do { |f, i|
				Synth(synthdef, [\freq, f, \amp, amp, \dur, dur, \pan, pans[i]]);
				rrand(minWait, maxWait).wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster with frequency-dependent amplitude curve
	ampCurve { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12, ampCurve = #[1.0, 0.5, 1.0]|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			var env, ampValues;

			// Create an envelope from the ampCurve
			env = Env(ampCurve, (ampCurve.size - 1).collect { 1 / (ampCurve.size - 1) });

			// Sample the envelope for each frequency
			ampValues = arr.size.collect { |i|
				var pos = i / (arr.size - 1);
				env.at(pos);
			};

			arr.do { |f, i|
				var thisAmp = amp * ampValues[i];
				Synth(synthdef, [\freq, f, \amp, thisAmp, \dur, dur, \pan, pans[i]]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster with frequency-based amplitude (mapping Hz range)
	ampByFreq { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12, minFreq=200, maxFreq=800, ampCurve = #[1.0, 0.75, 0.5, 0.3]|
		var arr = this.getFreqs;
		routine = Routine({
			var pans = this.spread(arr.size);
			var env, ampValues;

			// Create an envelope from the ampCurve
			env = Env(ampCurve, (ampCurve.size - 1).collect { 1 / (ampCurve.size - 1) });

			// Map each frequency to its position in the specified range
			ampValues = arr.collect { |f|
				var pos = (f - minFreq) / (maxFreq - minFreq);
				pos = pos.clip(0.0, 1.0);
				env.at(pos);
			};

			arr.do { |f, i|
				var thisAmp = amp * ampValues[i];
				Synth(synthdef, [\freq, f, \amp, thisAmp, \dur, dur, \pan, pans[i]]);
				offset.wait;
			};
		}).play;
		^routine;
	}
}