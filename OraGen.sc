OraGen {

	// 1. Microtonal Cluster Generator
	// Creates tightly packed frequencies around a base pitch
	*cluster { |base=440, size=8, spread=50|
		^Array.fill(size, { |i|
			base + (i - (size/2)) * (spread / size);
		}).sort;
	}

	// 2. Inharmonic Series Generator
	// Generates non-integer overtones (bell-like, metallic)
	*inharmonic { |fundamental=100, size=10, stretch=1.05|
		^Array.fill(size, { |i|
			var partialNum = i + 1;
			fundamental * (partialNum ** stretch);
		});
	}

	// 3. Golden Ratio Generator
	// Uses golden ratio (phi) for spacing
	*golden { |base=200, size=10, direction=\up|
		var phi = (1 + 5.sqrt) / 2;  // Golden ratio: ~1.618
		^Array.fill(size, { |i|
			if (direction == \up) {
				base * (phi ** i);
			} {
				base / (phi ** i);
			};
		}).sort;
	}

	// 4. Prime Number Series
	// Multiplies base by prime numbers - highly dissonant
	*primes { |base=100, size=10, factor=1.0|
		var primeNumbers = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71];
		var selectedPrimes = primeNumbers.keep(size);
		^selectedPrimes.collect { |prime|
			base * prime * factor;
		};
	}

	// 5. Random Dissonant Set
	// Random frequencies with controllable clustering/density
	*random { |minFreq=200, maxFreq=2000, size=12, density=0.5|
		var freqs;
		if (density < 0.5) {
			// Low density: more spread out
			freqs = Array.fill(size, {
				rrand(minFreq, maxFreq);
			});
		} {
			// High density: more clustered
			var numClusters = (size * (1 - density)).ceil.max(1);
			var clusterCenters = Array.fill(numClusters, {
				rrand(minFreq, maxFreq);
			});
			var freqsPerCluster = (size / numClusters).ceil;
			var clusterSpread = (maxFreq - minFreq) * (1 - density) * 0.1;
			
			freqs = clusterCenters.collect { |center|
				Array.fill(freqsPerCluster, {
					(center + rrand(clusterSpread.neg, clusterSpread))
						.clip(minFreq, maxFreq);
				});
			}.flat.keep(size);
		};
		^freqs.sort;
	}

	// 6. Shepard Tone Generator
	// Creates circular pitch illusion sets
	*shepard { |base=220, octaves=4, notesPerOctave=12|
		var freqs = [];
		octaves.do { |octave|
			notesPerOctave.do { |note|
				var freq = base * (2 ** octave) * (2 ** (note / notesPerOctave));
				freqs = freqs.add(freq);
			};
		};
		^freqs.sort;
	}

	// 7. BONUS: Tritone Stack
	// Stacks tritones (most dissonant interval in Western music)
	*tritones { |base=100, size=8|
		var ratio = 2 ** (6/12);  // Tritone ratio (sqrt(2))
		^Array.fill(size, { |i|
			base * (ratio ** i);
		});
	}

	// 8. BONUS: Xenakis Sieve
	// Creates pitch sets using modular arithmetic (Xenakis technique)
	*sieve { |base=100, moduli=#[8, 5, 3], size=20|
		var indices = [];
		size.do { |i|
			var passes = moduli.every { |mod|
				(i % mod) == 0;
			};
			if (passes) {
				indices = indices.add(i);
			};
		};
		^indices.collect { |i| base * (1 + (i * 0.1)) };
	}

	// 9. BONUS: Fibonacci Frequencies
	// Uses Fibonacci sequence for frequency ratios
	*fibonacci { |base=100, size=10|
		var fib = [1, 1];
		(size - 2).do {
			fib = fib.add(fib[fib.size-1] + fib[fib.size-2]);
		};
		^fib.keep(size).collect { |f| base * f };
	}
    
}