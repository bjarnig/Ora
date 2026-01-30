OraRatios {
	var <>ratios;
	var <>startFreq;
	var <>freqs;

	*new { |startFreq=65.55, ratios|
		^super.new.init(startFreq, ratios);
	}

	init { |argStartFreq, argRatios|
		startFreq = argStartFreq;
		ratios = argRatios;
		if (ratios.notNil) {
			this.buildFreqs;
		};
	}

	*fromFreqs { |freqArray|
		var instance = this.new;
		instance.analyzeFreqs(freqArray);
		^instance;
	}

	analyzeFreqs { |freqArray|
		var calculatedRatios = [];
		startFreq = freqArray[0];
		(freqArray.size - 1).do { |i|
			var ratio = freqArray[i + 1] / freqArray[i];
			calculatedRatios = calculatedRatios.add(ratio);
		};
		ratios = calculatedRatios;
		freqs = freqArray;
		^ratios;
	}

	buildFreqs {
		if (ratios.isNil) {
			"OraRatios: No ratios defined. Cannot build frequencies.".error;
			^nil;
		};
		freqs = [startFreq];
		ratios.do { |ratio|
			freqs = freqs.add(freqs.last * ratio);
		};
		^freqs;
	}

	printAnalysis {
		if (freqs.isNil or: { ratios.isNil }) {
			"OraRatios: No frequencies or ratios to analyze.".warn;
			^this;
		};
		"=== OraRatios Analysis ===".postln;
		("Start frequency: " ++ startFreq.round(0.01) ++ " Hz").postln;
		"".postln;
		ratios.do { |ratio, i|
			("Step " ++ (i + 1) ++ ": " ++ 
			 freqs[i].round(0.01) ++ " Hz -> " ++ 
			 freqs[i + 1].round(0.01) ++ " Hz, " ++
			 "ratio: " ++ ratio.round(0.0001)).postln;
		};
		"".postln;
		("Ratios: " ++ ratios.round(0.0001)).postln;
		("Frequencies: " ++ freqs.round(0.01)).postln;
		^this;
	}

	transpose { |newStartFreq|
		startFreq = newStartFreq;
		this.buildFreqs;
		^this;
	}

	setRatio { |index, newRatio|
		if (ratios.isNil) {
			"OraRatios: No ratios defined.".error;
			^this;
		};
		if (index >= ratios.size or: { index < 0 }) {
			("OraRatios: Index " ++ index ++ " out of range (0-" ++ (ratios.size - 1) ++ ")").error;
			^this;
		};
		ratios[index] = newRatio;
		this.buildFreqs;
		^this;
	}

	scaleRatios { |scalar|
		if (ratios.isNil) {
			"OraRatios: No ratios defined.".error;
			^this;
		};
		ratios = ratios.collect { |r| 1 + ((r - 1) * scalar) };
		this.buildFreqs;
		^this;
	}

	copy {
		^OraRatios.new(startFreq, ratios.copy);
	}

	*microCluster { |startFreq=65.55|
		^this.new(startFreq, [1.03, 1.05, 1.04, 1.07, 1.06, 1.08, 1.04, 1.09]);
	}

	*tritone { |startFreq=65.55|
		^this.new(startFreq, [1.414, 1.06, 1.414, 1.1, 1.414, 1.05, 1.414, 1.08]);
	}

	*irrational { |startFreq=65.55|
		var phi = 1.618034;
		var sqrt3 = 3.sqrt;
		var sqrt5 = 5.sqrt;
		^this.new(startFreq, [1.414, phi, 1.05, sqrt3, 1.08, 1.414, 1.1, sqrt5]);
	}

	*detuned { |startFreq=65.55|
		^this.new(startFreq, [1.51, 1.26, 1.99, 1.42, 1.35, 2.03, 1.07, 1.48]);
	}

	*eerie { |startFreq=65.55|
		var phi = 1.618034;
		var sqrt3 = 3.sqrt;
		^this.new(startFreq, [1.06, 1.414, 1.04, phi, 1.09, 2.01, 1.03, sqrt3]);
	}

	*extreme { |startFreq=65.55|
		^this.new(startFreq, [1.02, 1.03, 1.04, 1.03, 1.05, 1.04, 1.06, 1.07]);
	}

	*harmonic { |startFreq=65.55, numPartials=8|
		var ratios = [];
		var prevPartial = 1;
		(numPartials - 1).do { |i|
			var partial = i + 2;
			ratios = ratios.add(partial / prevPartial);
			prevPartial = partial;
		};
		^this.new(startFreq, ratios);
	}

	*justIntonation { |startFreq=65.55|
		^this.new(startFreq, [9/8, 5/4, 4/3, 3/2, 5/3, 15/8, 2/1]);
	}

	*equalTemperament { |startFreq=65.55, semitones|
		if (semitones.isNil) {
			semitones = [2, 4, 5, 7, 9, 11, 12];
		};
		var ratios = [];
		var prevSemi = 0;
		semitones.do { |semi|
			ratios = ratios.add(2 ** ((semi - prevSemi) / 12));
			prevSemi = semi;
		};
		^this.new(startFreq, ratios);
	}

	*random { |startFreq=65.55, numRatios=8, minRatio=1.02, maxRatio=2.0|
		var ratios = numRatios.collect {
			rrand(minRatio, maxRatio);
		};
		^this.new(startFreq, ratios);
	}

	asOra {
		if (freqs.isNil) {
			this.buildFreqs;
		};
		^Ora(freqs);
	}

	asOraPlay { |minFreq=40, maxFreq=14000, numChannels=2|
		if (freqs.isNil) {
			this.buildFreqs;
		};
		^OraPlay(freqs, minFreq, maxFreq, numChannels);
	}

	*printPresets {
		"=== OraRatios Presets ===".postln;
		"OraRatios.microCluster(startFreq)".postln;
		"OraRatios.tritone(startFreq)".postln;
		"OraRatios.irrational(startFreq)".postln;
		"OraRatios.detuned(startFreq)".postln;
		"OraRatios.eerie(startFreq)".postln;
		"OraRatios.extreme(startFreq)".postln;
		"OraRatios.harmonic(startFreq, numPartials)".postln;
		"OraRatios.justIntonation(startFreq)".postln;
		"OraRatios.equalTemperament(startFreq, semitones)".postln;
		"OraRatios.random(startFreq, numRatios, minRatio, maxRatio)".postln;
		^this;
	}
}
