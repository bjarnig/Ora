// OraSpear - SPEAR partial file parser for Ora
// Reads SPEAR .txt files and extracts representative frequencies

OraSpear {
	var <>partials;
	var <>filepath;

	*new { |path|
		^super.new.init(path);
	}

	init { |path|
		this.filepath = path;
		this.partials = [];
	}

	// Parse SPEAR .txt file
	parse {
		var file, line, inData = false, currentPartial;

		file = File(this.filepath, "r");
		if (file.isNil) {
			("OraSpear: Could not open file" + this.filepath).error;
			^this;
		};

		while { line = file.getLine; line.notNil } {
			// Skip header until we hit partials-data
			if (line.contains("partials-data")) {
				inData = true;
			} {
				if (inData and: { line.size > 0 }) {
					var words = line.split($ );
					words = words.select({ |w| w.size > 0 });

					if (words.size == 0) {
						// Skip empty lines
					} {
						// Partial header line has exactly 4 words
						if (words.size == 4) {
							// Save previous partial if exists
							if (currentPartial.notNil) {
								this.partials = this.partials.add(currentPartial);
							};

							// Start new partial
							currentPartial = (
								id: words[0].asInteger,
								count: words[1].asInteger,
								startTime: words[2].asFloat,
								endTime: words[3].asFloat,
								times: [],
								freqs: [],
								amps: []
							);
						} {
							// Data line: multiple time freq amp triplets
							if (currentPartial.notNil and: { (words.size % 3) == 0 }) {
								var i = 0;
								while { i < words.size } {
									currentPartial.times = currentPartial.times.add(words[i].asFloat);
									currentPartial.freqs = currentPartial.freqs.add(words[i+1].asFloat);
									currentPartial.amps = currentPartial.amps.add(words[i+2].asFloat);
									i = i + 3;
								};
							};
						};
					};
				};
			};
		};

		// Add last partial
		if (currentPartial.notNil) {
			this.partials = this.partials.add(currentPartial);
		};

		file.close;
		("OraSpear: Parsed" + this.partials.size + "partials from" + this.filepath.basename).postln;
		^this;
	}

	// Get top N partials by various criteria
	selectPartials { |n = 8, sortBy = \duration|
		var sorted, validPartials;

		// Filter out partials with no data
		validPartials = this.partials.select({ |p| p.freqs.size > 0 });

		case
		{ sortBy == \duration } {
			sorted = validPartials.copy.sort({ |a, b|
				(a.endTime - a.startTime) > (b.endTime - b.startTime)
			});
		}
		{ sortBy == \meanAmp } {
			sorted = validPartials.copy.sort({ |a, b|
				var aMean = if (a.amps.size > 0) { a.amps.mean } { 0 };
				var bMean = if (b.amps.size > 0) { b.amps.mean } { 0 };
				aMean > bMean
			});
		}
		{ sortBy == \maxAmp } {
			sorted = validPartials.copy.sort({ |a, b|
				var aMax = if (a.amps.size > 0) { a.amps.maxItem } { 0 };
				var bMax = if (b.amps.size > 0) { b.amps.maxItem } { 0 };
				aMax > bMax
			});
		}
		{ sortBy == \meanFreq } {
			sorted = validPartials.copy.sort({ |a, b|
				var aMean = if (a.freqs.size > 0) { a.freqs.mean } { 0 };
				var bMean = if (b.freqs.size > 0) { b.freqs.mean } { 0 };
				aMean > bMean
			});
		}
		{ sortBy == \startTime } {
			sorted = validPartials.copy.sort({ |a, b|
				a.startTime < b.startTime
			});
		};

		^sorted[0..min(n-1, sorted.size-1)];
	}

	// Extract frequencies from selected partials
	freqs { |n = 8, sortBy = \duration, method = \mean|
		var selected = this.selectPartials(n, sortBy);
		var frequencies;

		if (selected.isNil or: { selected.size == 0 }) {
			"OraSpear: No partials selected".warn;
			^[];
		};

		frequencies = selected.collect({ |partial|
			if (partial.freqs.size == 0) {
				nil
			} {
				case
				{ method == \mean } { partial.freqs.mean }
				{ method == \median } {
					var sorted = partial.freqs.copy.sort;
					sorted[sorted.size div: 2]
				}
				{ method == \min } { partial.freqs.minItem }
				{ method == \max } { partial.freqs.maxItem }
				{ method == \start } { partial.freqs[0] }
				{ method == \end } { partial.freqs[partial.freqs.size - 1] }
				{ method == \weightedMean } {
					// Amplitude-weighted mean
					var weights = partial.amps;
					var sum = 0, weightSum = 0;
					partial.freqs.do({ |freq, i|
						sum = sum + (freq * weights[i]);
						weightSum = weightSum + weights[i];
					});
					sum / weightSum.max(1e-10)
				};
			}
		});

		// Filter out nil values
		frequencies = frequencies.select({ |f| f.notNil });

		^frequencies.sort;
	}

	// Create Ora object from SPEAR data
	asOra { |n = 8, sortBy = \duration, method = \mean|
		var freqs = this.freqs(n, sortBy, method);
		^Ora.new(freqs);
	}

	// Print info about loaded partials
	printInfo {
		var allFreqs;
		("OraSpear Info:").postln;
		("  File:" + this.filepath.basename).postln;
		("  Total partials:" + this.partials.size).postln;
		
		if (this.partials.size > 0) {
			("  Duration range:" + this.partials.collect(_.startTime).minItem + "->" +
				this.partials.collect(_.endTime).maxItem + "sec").postln;
			
			// Collect all frequencies from all partials
			allFreqs = this.partials.collect({ |p| p.freqs }).flat.select({ |f| f.notNil });
			if (allFreqs.size > 0) {
				("  Frequency range:" + allFreqs.minItem.round(0.01) + "->" +
					allFreqs.maxItem.round(0.01) + "Hz").postln;
			};
		};
		^this;
	}

	// Get info about a specific partial
	partialInfo { |id|
		var partial = this.partials.detect({ |p| p.id == id });
		if (partial.isNil) {
			("OraSpear: Partial" + id + "not found").warn;
			^nil;
		};

		("Partial" + id + ":").postln;
		("  Duration:" + (partial.endTime - partial.startTime) + "sec").postln;
		("  Data points:" + partial[\count]).postln;
		("  Freq range:" + partial.freqs.minItem.round(0.01) + "->" + partial.freqs.maxItem.round(0.01) + "Hz").postln;
		("  Mean freq:" + partial.freqs.mean.round(0.01) + "Hz").postln;
		("  Mean amp:" + partial.amps.mean.round(0.00001)).postln;
		^partial;
	}
}
