// ===== OraRatios CLASS EXAMPLES =====

// See all available presets
OraRatios.printPresets;

// ===== ANALYZE EXISTING FREQUENCY ARRAY =====

// Original frequency array
~origFreqs = [65.55270552361, 76.919274874104, 81.833274151755, 100.5178980979, 179.55610944553, 245.38096635828, 1078.1094676809, 1148.556015528, 1492.3905066174];

// Analyze ratios from existing frequencies
~orig = OraRatios.fromFreqs(~origFreqs);
~orig.printAnalysis;

// ===== BUILD FROM RATIOS =====

// Create with custom ratios
~custom = OraRatios.new(65.55, [1.17, 1.06, 1.23, 1.79, 1.37, 4.39, 1.07, 1.30]);
~custom.printAnalysis;
~custom.freqs; // Get the frequency array

// ===== MODIFY RATIOS =====

// Modify a specific ratio
~modified = ~orig.copy;
~modified.setRatio(2, 1.25); // Change 3rd ratio (index 2)
~modified.freqs;

// Transpose to different starting frequency
~transposed = ~orig.copy.transpose(130); // Transpose up an octave
~transposed.freqs;

// Scale all ratios (compress/expand intervals)
~scaled = ~orig.copy.scaleRatios(1.5); // Make intervals 50% wider
~scaled.freqs;

// ===== USE PRESETS =====

// 1. Microtonal clusters
~micro = OraRatios.microCluster(65.55);
~micro.freqs;

// 2. Tritone-based
~tritone = OraRatios.tritone(65.55);
~tritone.freqs;

// 3. Irrational ratios (golden ratio, √3, √5, √7)
~irrat = OraRatios.irrational(65.55);
~irrat.freqs;

// 4. Detuned harmonics
~detuned = OraRatios.detuned(65.55);
~detuned.freqs;

// 5. Eerie mix
~eerie = OraRatios.eerie(65.55);
~eerie.freqs;

// 6. Extreme dissonance
~extreme = OraRatios.extreme(65.55);
~extreme.freqs;

// 7. Harmonic series (consonant)
~harmonic = OraRatios.harmonic(65.55, 8);
~harmonic.freqs;

// 8. Just intonation
~just = OraRatios.justIntonation(65.55);
~just.freqs;

// 9. Equal temperament (major scale)
~major = OraRatios.equalTemperament(65.55, [2, 4, 5, 7, 9, 11, 12]);
~major.freqs;

// 10. Random ratios
~rand = OraRatios.random(65.55, 8, 1.05, 1.8);
~rand.freqs;

// ===== INTEGRATION WITH ORA/ORAPLAY =====

// Convert directly to Ora
~oraObj = ~eerie.asOra;

// Convert directly to OraPlay and play
~player = ~eerie.asOraPlay;
~player.clusterGrad(ampFrom: 0.03, ampTo: 0.06, durFrom: 6, durTo: 3, 
                     offsetFrom: 0.08, offsetTo: 0.2, ampComp: 1.0);

// Chain operations
~p = OraRatios.tritone(50).transpose(80).scaleRatios(0.8).asOraPlay;
~p.cluster(amp: 0.05, ampComp: 1.0);