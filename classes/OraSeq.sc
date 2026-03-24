OraSeq {
	var <>oras;      // Array of Ora objects
	var <>players;   // Array of OraPlay objects (one per Ora)
	var <>currentIndex;

	*new { |oraArray|
		^super.new.init(oraArray);
	}

	init { |oraArray|
		oras = oraArray ? [];
		players = oras.collect { |ora| OraPlay.new(ora) };
		currentIndex = 0;
	}

	// Add an Ora to the sequence
	add { |ora|
		oras = oras.add(ora);
		players = players.add(OraPlay.new(ora));
		^this;
	}

	// Get a specific Ora by index
	at { |index|
		^oras[index];
	}

	// Get a specific player by index
	playerAt { |index|
		^players[index];
	}

	// Get current Ora
	current {
		^oras[currentIndex];
	}

	// Get current player
	currentPlayer {
		^players[currentIndex];
	}

	// Move to next Ora in sequence
	next {
		currentIndex = (currentIndex + 1) % oras.size;
		^this.current;
	}

	// Move to previous Ora in sequence
	prev {
		currentIndex = (currentIndex - 1).wrap(0, oras.size - 1);
		^this.current;
	}

	// Go to specific index
	goto { |index|
		currentIndex = index.clip(0, oras.size - 1);
		^this.current;
	}

	// Stop all players
	stopAll {
		players.do { |player| player.stop };
	}

	plot { |name = "OraSeq", bounds|
		if (oras.isEmpty) { "OraSeq is empty".warn; ^this };
		oras.do { |ora, i| ora.plotStems(name ++ " [" ++ i ++ "]", bounds) };
		^this;
	}

	plotCombined { |name = "OraSeq Combined", bounds, minval, maxval|
		var allItems, maxSize, paddedData, plotter;
		if (oras.isEmpty) { "OraSeq is empty".warn; ^this };

		allItems = oras.collect { |ora| ora.items };
		maxSize = allItems.collect(_.size).maxItem;
		paddedData = allItems.collect { |items|
			items ++ Array.fill(maxSize - items.size, items.last);
		};

		plotter = Plotter(name, bounds: bounds);
		plotter.value = paddedData;
		plotter.plotMode = \bars;
		plotter.plotColor = Color.new(0.81, 0.93, 0.86);
		if (minval.notNil and: maxval.notNil) {
			plotter.specs = ControlSpec(minval, maxval, \lin);
		};
		plotter.refresh;
		^this;
	}

	plotOverlay { |name = "OraSeq Overlay", bounds|
		var w, width, height, allFreqs, minF, maxF, uv;
		if (oras.isEmpty) { "OraSeq is empty".warn; ^this };

		allFreqs = oras.collect(_.items).flat;
		minF = allFreqs.minItem;
		maxF = allFreqs.maxItem;
		width = bounds !? { bounds.width } ?? { 800 };
		height = bounds !? { bounds.height } ?? { 400 };

		w = Window(name ++ " (" ++ oras.size ++ " Oras)",
			Rect(200, 200, width, height));

		uv = UserView(w, Rect(0, 0, width, height));
		uv.background = Color.new(0.09, 0.11, 0.13);
		uv.drawFunc = {
			var pad = 50, plotW = width - (pad * 2), plotH = height - (pad * 2);
			var logMin = minF.max(1).log, logMax = maxF.max(2).log;
			var pal = [
				Color.new(0.81, 0.93, 0.86), Color.new(0.79, 0.89, 0.79),
				Color.new(0.85, 0.82, 0.85), Color.new(0.45, 0.70, 0.81),
				Color.new(0.95, 0.91, 0.86), Color.new(0.73, 0.86, 0.61),
				Color.new(0.62, 0.71, 0.67), Color.new(0.81, 1.0, 0.90),
				Color.new(0.90, 0.92, 0.92), Color.new(0.74, 0.77, 0.86)
			];

			5.do { |i|
				var y = pad + (plotH * i / 4);
				var freq = exp(logMax - ((logMax - logMin) * i / 4));
				Pen.color = Color.new(0.20, 0.23, 0.28);
				Pen.line(pad @ y, (pad + plotW) @ y);
				Pen.stroke;
				Pen.color = Color.new(0.62, 0.71, 0.67);
				Pen.stringAtPoint(freq.round(0.1).asString ++ " Hz",
					2 @ (y - 6), Font("Menlo", 9));
			};

			oras.do { |ora, oraIdx|
				var c = pal[oraIdx % 10];
				var freqArr = ora.items;

				freqArr.do { |f, i|
					var x = pad + (i / (freqArr.size - 1).max(1) * plotW);
					var normY = (f.max(1).log - logMin) / (logMax - logMin).max(0.001);
					var y = pad + plotH - (normY * plotH);

					Pen.color = Color.new(c.red, c.green, c.blue, 0.6);
					Pen.line(x @ (pad + plotH), x @ y);
					Pen.width = 2;
					Pen.stroke;

					Pen.color = Color.new(c.red, c.green, c.blue, 0.85);
					Pen.fillOval(Rect(x - 2.5, y - 2.5, 5, 5));
				};

				Pen.color = c;
				Pen.fillRect(Rect(width - 80, pad + (oraIdx * 16), 10, 10));
				Pen.color = Color.new(0.62, 0.71, 0.67);
				Pen.stringAtPoint("Ora " ++ oraIdx,
					(width - 65) @ (pad + (oraIdx * 16) - 1), Font("Menlo", 9));
			};
		};
		w.front;
		^this;
	}

	plotHeatmap { |name = "OraSeq Heatmap", bounds, numBins = 40|
		var w, width, height, uv;
		var allFreqs, minF, maxF, logMin, logMax;
		if (oras.isEmpty) { "OraSeq is empty".warn; ^this };

		allFreqs = oras.collect(_.items).flat;
		minF = allFreqs.minItem.max(1);
		maxF = allFreqs.maxItem;
		logMin = minF.log;
		logMax = maxF.log;
		width = bounds !? { bounds.width } ?? { 800 };
		height = bounds !? { bounds.height } ?? { 400 };

		w = Window(name ++ " (" ++ oras.size ++ " steps)",
			Rect(200, 200, width, height));

		uv = UserView(w, Rect(0, 0, width, height));
		uv.background = Color.new(0.09, 0.11, 0.13);
		uv.drawFunc = {
			var pad = 50;
			var plotW = width - (pad * 2);
			var plotH = height - (pad * 2);
			var colW = plotW / oras.size;
			var binH = plotH / numBins;

			oras.do { |ora, oraIdx|
				var x = pad + (oraIdx * colW);
				var freqArr = ora.items;
				var bins = Array.fill(numBins, 0);
				var maxCount;

				freqArr.do { |f|
					var normF = (f.max(1).log - logMin) / (logMax - logMin).max(0.001);
					var binIdx = (normF * (numBins - 1)).round.clip(0, numBins - 1).asInteger;
					bins[binIdx] = bins[binIdx] + 1;
				};

				maxCount = bins.maxItem.max(1);

				numBins.do { |binIdx|
					var y = pad + plotH - ((binIdx + 1) * binH);
					var intensity = bins[binIdx] / maxCount;
					if (intensity > 0) {
						Pen.color = Color.new(
							0.45 * intensity,
							0.70 * intensity,
							0.81 * intensity,
							intensity.linlin(0, 1, 0.4, 0.95)
						);
						Pen.fillRect(Rect(x + 1, y, colW - 2, binH));
					};
				};
			};

			5.do { |i|
				var y = pad + (plotH * i / 4);
				var freq = exp(logMax - ((logMax - logMin) * i / 4));
				Pen.color = Color.new(0.20, 0.23, 0.28);
				Pen.line(pad @ y, (pad + plotW) @ y);
				Pen.stroke;
				Pen.color = Color.new(0.62, 0.71, 0.67);
				Pen.stringAtPoint(freq.round(1).asString ++ " Hz",
					2 @ (y - 6), Font("Menlo", 9));
			};

			oras.do { |ora, i|
				var x = pad + (i * colW) + (colW * 0.3);
				Pen.color = Color.new(0.62, 0.71, 0.67);
				Pen.stringAtPoint(i.asString,
					x @ (height - 14), Font("Menlo", 9));
			};
		};
		w.front;
		^this;
	}

	// Play through the sequence with a specific play method
	playSequence { |playMethod = \cluster, waitTime = 4, args|
		var routine;
		routine = Routine({
			oras.size.do { |i|
				var player = players[i];
				("Playing Ora " ++ i).postln;
				
				// Call the play method on the player
				if (args.notNil) {
					player.perform(playMethod, *args);
				} {
					player.perform(playMethod);
				};
				
				waitTime.wait;
			};
		}).play;
		^routine;
	}

	// Play through sequence with different methods for each
	playSequenceWithMethods { |methodsArray, waitTime = 4, argsArray|
		var routine;
		routine = Routine({
			oras.size.do { |i|
				var player = players[i];
				var method = methodsArray[i] ? \cluster;
				var args = if (argsArray.notNil) { argsArray[i] } { nil };
				
				("Playing Ora " ++ i ++ " with method: " ++ method).postln;
				
				if (args.notNil) {
					player.perform(method, *args);
				} {
					player.perform(method);
				};
				
				waitTime.wait;
			};
		}).play;
		^routine;
	}

	// Get size of sequence
	size {
		^oras.size;
	}

	// Print info about the sequence
	info {
		"===== OraSeq Info =====".postln;
		("Number of Oras: " ++ oras.size).postln;
		("Current index: " ++ currentIndex).postln;
		oras.do { |ora, i|
			("Ora[" ++ i ++ "]: " ++ ora.items.size ++ " frequencies, range: "
				++ ora.items.minItem.round(0.1) ++ " - " ++ ora.items.maxItem.round(0.1) ++ " Hz").postln;
		};
		"=======================".postln;
		^this;
	}

	// Export all Oras as arrays
	asArrays {
		^oras.collect(_.items);
	}

	// Print all frequency arrays
	printAll {
		oras.do { |ora, i|
			("Ora[" ++ i ++ "]:").postln;
			ora.items.postln;
		};
		^this;
	}
}  