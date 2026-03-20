OraPlay {
	var <>ora;
	var <>routine;
	var <>minFreq;
	var <>maxFreq;
	var <>numChannels;

	*new { |oraObject, minFreq = 40, maxFreq = 14000, numChannels = 2|
		^super.newCopyArgs(oraObject).init(minFreq, maxFreq, numChannels);
	}

	init { |argMinFreq, argMaxFreq, argNumChannels|
		this.minFreq = argMinFreq;
		this.maxFreq = argMaxFreq;
		this.numChannels = argNumChannels;
	}

	// Helper: channel spread
	// Returns output channel indices distributed across available channels
	spread { |n|
		^(0..(n-1)).collect { |i|
			(i * this.numChannels / n).floor.clip(0, this.numChannels - 1);
		};
	}

	// Helper: create synth with proper channel routing
	// Outputs mono signal to specified channel
	playSynth { |synthdef, freq, amp, dur, channelValue|
		^Synth(synthdef, [\freq, freq, \amp, amp, \dur, dur, \out, channelValue]);
	}

	stop {
		if (routine.notNil) {
			routine.stop;
			routine = nil;
		};
	}

	// Get the frequency array from Ora object and filter valid range
	getFreqs {
		var freqs = if (this.ora.isKindOf(Ora)) { this.ora.items } { this.ora };
		// Filter frequencies based on minFreq and maxFreq
		^freqs.select { |f| (f >= this.minFreq) and: (f <= this.maxFreq) };
	}

	// Perceptual amplitude compensation based on equal-loudness contours
	// Returns a scaling factor (0.0 to 1.0) to reduce perceived loudness at high frequencies
	// Based on simplified equal-loudness curves (Fletcher-Munson / ISO 226)
	// More aggressive attenuation for high frequencies
	ampCompensation { |freq, amount=1.0|
		var scaleFactor;

		// Aggressive equal-loudness compensation
		// Peak sensitivity around 2-3 kHz (factor = 1.0)
		// Strong attenuation of higher frequencies
		scaleFactor = case
			{ freq < 200 } {
				// Low frequencies: slight reduction
				0.75 + (freq / 200 * 0.25); // 0.75 to 1.0
			}
			{ freq < 1000 } {
				// Low-mid: gradually increase to peak
				0.85 + ((freq - 200) / 800 * 0.1); // 0.85 to 1.0
			}
			{ freq < 3000 } {
				// Peak sensitivity range: minimal adjustment
				1.0;
			}
			{ freq < 6000 } {
				// High-mid: start reducing aggressively
				1.0 - ((freq - 3000) / 3000 * 0.3); // 1.0 to 0.5
			}
			{ freq < 10000 } {
				// High: strong reduction
				0.5 - ((freq - 6000) / 4000 * 0.35); // 0.5 to 0.15
			}
			{ true } {
				// Very high: very strong reduction
				(0.15 - ((freq - 10000) / 10000 * 0.12)).clip(0.2, 0.25);
			};

		// Amount controls how much compensation to apply (0 = none, 1 = full)
		^scaleFactor.linlin(0, 1, 1, scaleFactor).blend(1.0, 1.0 - amount);
	}

	// --- Play cluster ascending (default)
	cluster { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12, ampComp=0|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			arr.do { |f, i|
				var thisAmp = amp;
				// Apply perceptual amplitude compensation if enabled
				if (ampComp > 0) {
					thisAmp = thisAmp * this.ampCompensation(f, ampComp);
				};
				this.playSynth(synthdef, f, thisAmp, dur, channels[i]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster with interpolated amplitude and offset
	clusterGrad { |ampFrom=0.045, ampTo=0.020, durFrom=4, durTo=4, synthdef=\drsoft, offsetFrom=0.12, offsetTo=0.25, ampComp=0|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			var numItems = arr.size;
			arr.do { |f, i|
				var thisAmp, thisDur, thisOffset, compFactor;
				// Linear interpolation for amp, dur, and offset
				if (numItems == 1) {
					// Single item: use From values
					thisAmp = ampFrom;
					thisDur = durFrom;
					thisOffset = offsetFrom;
				} {
					// Multiple items: interpolate from From to To
					var pos = i / (numItems - 1);
					thisAmp = ampFrom + ((ampTo - ampFrom) * pos);
					thisDur = durFrom + ((durTo - durFrom) * pos);
					thisOffset = offsetFrom + ((offsetTo - offsetFrom) * pos);
				};

				// Apply perceptual amplitude compensation if enabled
				if (ampComp > 0) {
					compFactor = this.ampCompensation(f, ampComp);
					thisAmp = thisAmp * compFactor;
				};

				this.playSynth(synthdef, f, thisAmp, thisDur, channels[i]);
				thisOffset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster in reverse (descending)
	reverse { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12, ampComp=0|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			arr.reverse.do { |f, i|
				var chanIdx = arr.size - 1 - i;
				var thisAmp = amp;
				// Apply perceptual amplitude compensation if enabled
				if (ampComp > 0) {
					thisAmp = thisAmp * this.ampCompensation(f, ampComp);
				};
				this.playSynth(synthdef, f, thisAmp, dur, channels[chanIdx]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster in random order
	random { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			var shuffled = arr.scramble;
			shuffled.do { |f, i|
				var origIdx = arr.indexOf(f);
				this.playSynth(synthdef, f, amp, dur, channels[origIdx]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster from center outward (alternating)
	fromCenter { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			var mid = (arr.size / 2).floor;
			var indices = [];

			// Build alternating pattern from center
			(arr.size / 2).ceil.do { |i|
				indices = indices.add(mid + i);
				if (mid - i - 1 >= 0) { indices = indices.add(mid - i - 1) };
			};

			indices.do { |idx|
				this.playSynth(synthdef, arr[idx], amp, dur, channels[idx]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster as overlapping cloud (dense)
	dense { |amp=0.035, dur=6, synthdef=\drsoft, offset=0.05, ampComp=0|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			arr.do { |f, i|
				var thisAmp = amp;
				// Apply perceptual amplitude compensation if enabled
				if (ampComp > 0) {
					thisAmp = thisAmp * this.ampCompensation(f, ampComp);
				};
				this.playSynth(synthdef, f, thisAmp, dur, channels[i]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster in pairs simultaneously
	pairs { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.25|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			(arr.size / 2).floor.do { |i|
				this.playSynth(synthdef, arr[i * 2], amp, dur, channels[i * 2]);
				if ((i * 2 + 1) < arr.size) {
					this.playSynth(synthdef, arr[i * 2 + 1], amp, dur, channels[i * 2 + 1]);
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
			var channels = this.spread(arr.size);

			// Ascending
			arr.do { |f, i|
				this.playSynth(synthdef, f, amp, dur, channels[i]);
				offset.wait;
			};

			(dur * 0.5).wait; // pause at peak

			// Descending
			arr.reverse.do { |f, i|
				var chanIdx = arr.size - 1 - i;
				this.playSynth(synthdef, f, amp, dur, channels[chanIdx]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster with random sparse timing
	sparse { |amp=0.045, dur=4, synthdef=\drsoft, minWait=0.2, maxWait=0.8|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
			arr.do { |f, i|
				this.playSynth(synthdef, f, amp, dur, channels[i]);
				rrand(minWait, maxWait).wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster with frequency-dependent amplitude curve
	ampCurve { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12, ampCurve = #[1.0, 0.5, 1.0]|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
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
				this.playSynth(synthdef, f, thisAmp, dur, channels[i]);
				offset.wait;
			};
		}).play;
		^routine;
	}

	// --- Play cluster with frequency-based amplitude (mapping Hz range)
	ampByFreq { |amp=0.045, dur=4, synthdef=\drsoft, offset=0.12, minFreq=200, maxFreq=800, ampCurve = #[1.0, 0.75, 0.5, 0.3]|
		var arr = this.getFreqs;
		routine = Routine({
			var channels = this.spread(arr.size);
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
				this.playSynth(synthdef, f, thisAmp, dur, channels[i]);
				offset.wait;
			};
		}).play;
		^routine;
	}

}