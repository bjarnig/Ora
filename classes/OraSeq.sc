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
		plotter.plotColor = Color.new(1.0, 0.75, 0.8);
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
		uv.background = Color.grey(0.12);
		uv.drawFunc = {
			var pad = 50, plotW = width - (pad * 2), plotH = height - (pad * 2);
			var logMin = minF.max(1).log, logMax = maxF.max(2).log;

			5.do { |i|
				var y = pad + (plotH * i / 4);
				Pen.color = Color.grey(0.22);
				Pen.line(pad @ y, (pad + plotW) @ y);
				Pen.stroke;
				var freq = exp(logMax - ((logMax - logMin) * i / 4));
				Pen.color = Color.grey(0.45);
				Pen.stringAtPoint(freq.round(0.1).asString ++ " Hz",
					2 @ (y - 6), Font("Menlo", 9));
			};

			oras.do { |ora, oraIdx|
				var hue = oraIdx / oras.size.max(1);
				var freqArr = ora.items;
				var alpha = 0.6;

				freqArr.do { |f, i|
					var x = pad + (i / (freqArr.size - 1).max(1) * plotW);
					var normY = (f.max(1).log - logMin) / (logMax - logMin).max(0.001);
					var y = pad + plotH - (normY * plotH);

					Pen.color = Color.hsv(hue, 0.8, 0.95, alpha);
					Pen.line(x @ (pad + plotH), x @ y);
					Pen.width = 2;
					Pen.stroke;

					Pen.color = Color.hsv(hue, 0.9, 1.0, alpha + 0.2);
					Pen.fillOval(Rect(x - 2.5, y - 2.5, 5, 5));
				};

				Pen.color = Color.hsv(hue, 0.8, 0.95);
				Pen.fillRect(Rect(width - 80, pad + (oraIdx * 16), 10, 10));
				Pen.color = Color.grey(0.6);
				Pen.stringAtPoint("Ora " ++ oraIdx,
					(width - 65) @ (pad + (oraIdx * 16) - 1), Font("Menlo", 9));
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