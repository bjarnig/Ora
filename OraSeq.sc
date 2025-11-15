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

	// Multi-plot: visualize the evolution of Oras in the sequence
	// Each Ora gets its own window, displayed as an envelope
	plot { |name = "OraSeq", bounds, minval, maxval|
		var allItems, colors;
		
		if (oras.isEmpty) {
			"OraSeq is empty, nothing to plot".warn;
			^this;
		};

		// Get all frequency arrays
		allItems = oras.collect { |ora| ora.items };
		
		// Generate colors for each Ora
		colors = allItems.size.collect { |i| 
			Color.hsv(i / allItems.size, 0.7, 0.9)
		};

		// Create separate plotter for each Ora
		oras.do { |ora, i|
			var plotter, freqRange;
			
			// Calculate frequency range for this Ora
			freqRange = "(" ++ ora.items.minItem.round(0.1).asString ++ " - " 
				++ ora.items.maxItem.round(0.1).asString ++ " Hz)";
			
			plotter = Plotter(name ++ " [" ++ i ++ "] " ++ freqRange, bounds: bounds);
			plotter.value = ora.items;
			plotter.plotMode = \levels;  // Connected line style
			plotter.plotColor = colors[i];
			// Let each plot auto-scale to its own range (unless minval/maxval specified)
			if (minval.notNil and: maxval.notNil) {
				plotter.specs = ControlSpec(minval, maxval, \lin);
			};
			plotter.refresh;
		};

		^this;
	}

	// Alternative: All Oras in one window, stacked with step/bars view
	plotCombined { |name = "OraSeq Combined", bounds, minval, maxval|
		var allItems, maxSize, paddedData, plotter;
		
		if (oras.isEmpty) {
			"OraSeq is empty, nothing to plot".warn;
			^this;
		};

		allItems = oras.collect { |ora| ora.items };
		
		// Pad arrays to same length
		maxSize = allItems.collect(_.size).maxItem;
		paddedData = allItems.collect { |items|
			items ++ Array.fill(maxSize - items.size, items.last);
		};

		// Print mapping info to console
		"===== OraSeq Combined Plot =====".postln;
		oras.do { |ora, i|
			var freqRange = ora.items.minItem.round(0.1).asString ++ " - " 
				++ ora.items.maxItem.round(0.1).asString ++ " Hz";
			("Row " ++ i ++ ": " ++ ora.items.size ++ " frequencies, range: " ++ freqRange).postln;
		};
		"================================".postln;

		// Create plotter with step/bars view and pink color (like .plot)
		plotter = Plotter(name, bounds: bounds);
		plotter.value = paddedData;
		plotter.plotMode = \bars;  // Step/bars view
		plotter.plotColor = Color.new(1.0, 0.75, 0.8);  // Pink color
		if (minval.notNil and: maxval.notNil) {
			plotter.specs = ControlSpec(minval, maxval, \lin);
		};
		plotter.refresh;

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