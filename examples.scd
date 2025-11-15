// ============================================================
// ORA LIBRARY - COMPREHENSIVE EXAMPLES
// ============================================================
//
// A complete guide to using the Ora library for spectral composition
// 
// Classes:
//   - Ora: Transform frequency arrays
//   - OraGen: Generate frequency sets
//   - OraPlay: Play frequency arrays with patterns
//   - OraSeq: Manage sequences of transformations
//
// ============================================================

// SETUP: Load SynthDefs first (run this once per session)
Ora.addSynths;


// ============================================================
// ORA CLASS - Frequency Array Transformations
// ============================================================

// All array transformations are encapsulated in the Ora class.
// Methods are chainable and operate on the 'items' instance variable.

(
// Create a harmonic series
~hs = Array.series(8, 100, 100);

// Example 1: Using formantGap with chaining
~ora1 = Ora.new(~hs)
    .formantGap(fHole: 520, bw: 160, amount: 0.22)
    .formantGap(fHole: 520, bw: 160, amount: 0.22);

~ora1.items.postln;
)

// Example 2: Serial transformations (chaining)
(
~ora_retro = Ora.new(~ora1.items).retrograde();
~ora_inv = Ora.new(~ora1.items).inversion();
~ora_retroInv = Ora.new(~ora1.items).retrogradeInversion();
~ora_rot = Ora.new(~ora1.items).rotation(2);
~ora_trans = Ora.new(~ora1.items).transposition(50);
)

// Example 3: Complex chaining - formant gaps + serial methods
(
~ora_complex = Ora.new(~hs)
    .formantGap(fHole: 520, bw: 160, amount: 0.22)
    .retrograde()
    .transposition(25)
    .inversion();
)

// Example 4: Parametric transformations with chaining
(
~ora_param = Ora.new(~hs)
    .affineLog(scale: 1.2, shift: 0.1)
    .centroidDilation(r: 1.3, curve: 1.0)
    .rotate(frac: 0.25);
)

// Example 5: Advanced morphing
(
~target = Array.series(8, 200, 150);
~ora_morph = Ora.new(~hs)
    .formantGap(fHole: 480, bw: 140, amount: 0.18)
    .otMorph(~target, alpha: 0.5);
)

// Example 6: Ratio-based transformations
(
~ora_ratios = Ora.new(~hs)
    .ratioSnap(base: 100, maxNum: 16, maxDen: 16, amt: 0.5)
    .geomeanLock();
)

// Plot comparisons
~hs.plot;                    // Original
~ora1.items.plot;            // After double formant gap
~ora_retroInv.items.plot;    // Retrograde inversion
~ora_complex.items.plot;     // Complex chain
~ora_param.items.plot;       // Parametric transformations
~ora_morph.items.plot;       // Morphed result

// Access items at any time
~ora1.items;
~ora_complex.items;


// ============================================================
// ORAPLAY CLASS - Playback Patterns
// ============================================================

// Play transformed frequency arrays with different patterns

(
// Create and transform frequencies with Ora
~myOra = Ora.new([200, 300, 400, 500, 600, 700])
    .centroidDilation(1.2)
    .transposition(50);

// Create a player
~player = OraPlay.new(~myOra);
)

// Example 1: Basic playback methods
~player.cluster(0.045, 4, \drsoft);          // Play ascending
~player.reverse(0.045, 4, \drpm);            // Play descending
~player.random(0.045, 4, \drgend);           // Random order
~player.fromCenter(0.045, 4, \drimp);        // From center outward

// Example 2: Textural patterns
~player.dense(0.035, 8, \drsoft);            // Dense overlapping cloud
~player.sparse(0.045, 4, \drsoft, 0.3, 1.0); // Sparse random timing
~player.pairs(0.045, 4, \drdfm);             // Play in pairs
~player.wave(0.045, 5, \drbase);             // Up then down

// Example 3: Amplitude shaping
~player.ampCurve(0.045, 4, \drsoft, 0.12, [1.0, 0.3, 1.0]);    // Quieter in middle
~player.ampCurve(0.045, 4, \drsoft, 0.12, [0.2, 1.0]);         // Fade in
~player.ampCurve(0.045, 4, \drsoft, 0.12, [1.0, 0.3, 0.3, 1.0]); // U-shape

// Example 4: Frequency-based amplitude
~player.ampByFreq(0.045, 4, \drsoft, 0.12, 200, 800, [1.0, 0.5]);        // High freqs quieter
~player.ampByFreq(0.045, 4, \drsoft, 0.12, 200, 800, [0.3, 1.0, 0.3]);   // Middle freqs louder

// Example 5: Stop playback
~player.stop;

// Example 6: Chaining Ora transformations with OraPlay
(
OraPlay.new(
    Ora.new([200, 250, 310, 340, 390, 440, 500])
        .formantGap(fHole: 400, bw: 100, amount: 0.2)
        .retrograde()
).cluster(0.05, 6, \drsoft);
)

// Example 7: Using plain arrays (no Ora object)
(
~player2 = OraPlay.new([100, 200, 300, 400, 500]);
~player2.dense(0.04, 8, \drgend);
)

// Example 8: Multiple transformations and play
(
~ora_transformed = Ora.new(Array.series(11, 200, 50))
    .centroidDilation(1.3)
    .formantGap(fHole: 480, bw: 140, amount: 0.18)
    .transposition(30);

~p = OraPlay.new(~ora_transformed);
~p.wave(0.045, 5, \drsoft);
)


// ============================================================
// ORASEQ CLASS - Sequence Management
// ============================================================

// Manage sequences of Ora transformations and visualize evolution

// Example 1: Create a sequence with variations
(
~base = [200, 250, 310, 340, 390, 440, 500];

~ora0 = Ora.new(~base);  // Original
~ora1 = Ora.new(~base).centroidDilation(1.2);  // Expanded
~ora2 = Ora.new(~base).centroidDilation(0.8);  // Compressed
~ora3 = Ora.new(~base).formantGap(fHole: 400, bw: 100, amount: 0.3);  // With gap
~ora4 = Ora.new(~base).retrograde().transposition(50);  // Retrograde + transposed

~seq = OraSeq.new([~ora0, ~ora1, ~ora2, ~ora3, ~ora4]);
)

// Example 2: Visualize the evolution
~seq.plot;  // Shows all Oras, each in its own window

// Example 3: Get sequence info
~seq.info;  // Prints info about each Ora in the sequence

// Example 4: Navigate through sequence
~seq.current;           // Get current Ora
~seq.next;              // Move to next
~seq.prev;              // Move to previous
~seq.goto(2);           // Go to specific index

// Example 5: Play through the sequence
~seq.playSequence(\cluster, waitTime: 5);
~seq.playSequence(\dense, waitTime: 6, args: [0.04, 8, \drsoft]);

// Example 6: Play with different methods for each Ora
(
~seq.playSequenceWithMethods(
    [\cluster, \reverse, \fromCenter, \wave, \dense],
    waitTime: 5
);
)

// Example 7: Access individual players
~seq.playerAt(0).cluster(0.045, 4, \drsoft);  // Play first Ora
~seq.playerAt(2).wave(0.05, 5, \drpm);         // Play third Ora
~seq.currentPlayer.random(0.045, 4, \drgend);  // Play current Ora

// Example 8: Build sequence incrementally
(
~seq2 = OraSeq.new();  // Empty sequence
~seq2.add(Ora.new([100, 200, 300, 400]));
~seq2.add(Ora.new([100, 200, 300, 400]).affineLog(1.3));
~seq2.add(Ora.new([100, 200, 300, 400]).rotate(0.25));
~seq2.plot;  // Multi-plot the evolution
)

// Example 9: Complex transformations sequence
(
~harmonic = Array.series(20, 100, 5);

~oraSeq = OraSeq.new([
    Ora.new(~harmonic),
    Ora.new(~harmonic).formantGap(fHole: 520, bw: 160, amount: 0.22),
    Ora.new(~harmonic).formantGap(fHole: 520, bw: 160, amount: 0.22).retrograde(),
    Ora.new(~harmonic).centroidDilation(1.4).transposition(30),
    Ora.new(~harmonic).intervalFlow(scale: 0.05, ph: 1.2),
    Ora.new(~harmonic).rankPower(1.5)
]);

~oraSeq.plot;  // Multi-plot to see the evolution
~oraSeq.info;  // Get detailed info
)

// Example 10: Play through with custom parameters per Ora
(
~oraSeq.playSequenceWithMethods(
    [\cluster, \reverse, \fromCenter, \wave, \dense, \sparse],
    waitTime: 6,
    argsArray: [
        [0.045, 4, \drsoft],      // Ora 0
        [0.045, 4, \drpm],         // Ora 1
        [0.045, 5, \drgend],       // Ora 2
        [0.045, 6, \drsoft],       // Ora 3
        [0.035, 8, \drsoft],       // Ora 4
        [0.045, 4, \drimp]         // Ora 5
    ]
);
)

// Example 11: Stop all playback
~seq.stopAll;
~oraSeq.stopAll;

// Example 12: Plot combined view (alternative visualization)
~oraSeq.plotCombined;  // All Oras in one window

// Example 13: Export sequence as plain arrays
~arrays = ~oraSeq.asArrays;  // Get all frequency arrays
~arrays.do(_.postln);         // Print them


// ============================================================
// ORAGEN CLASS - Frequency Set Generation
// ============================================================

// Generate dissonant pitch arrays for use with Ora

// Example 1: Microtonal Cluster
(
~cluster = OraGen.cluster(base: 440, size: 10, spread: 30);
~cluster.postln;  // Tightly packed around 440 Hz

// Create a player
~player = OraPlay.new(~cluster);
~player.cluster(0.045, 4, \drsoft);
)

// Example 2: Inharmonic Series (bell-like, metallic)
(
~inharmonic = OraGen.inharmonic(fundamental: 100, size: 12, stretch: 1.08);
~inharmonic.postln;  // Non-integer overtones
Ora.new(~inharmonic).plot;
)

// Example 3: Golden Ratio spacing
(
~golden = OraGen.golden(base: 200, size: 8, direction: \up);
~golden.postln;  // Phi-based spacing

~player = OraPlay.new(~golden);
~player.cluster(0.045, 4, \drsoft);
)

// Example 4: Prime Number Series (highly dissonant)
(
~primes = OraGen.primes(base: 80, size: 10, factor: 1.2);
~primes.postln;  // Based on prime numbers
Ora.new(~primes).plot;
)

// Example 5: Random Dissonant Set
(
~randomLow = OraGen.random(minFreq: 200, maxFreq: 1000, size: 15, density: 0.3);
~randomHigh = OraGen.random(minFreq: 200, maxFreq: 1000, size: 15, density: 0.8);
~randomLow.postln;   // More spread out
~randomHigh.postln;  // More clustered
)

// Example 6: Shepard Tones (circular pitch illusion)
(
~shepard = OraGen.shepard(base: 220, octaves: 4, notesPerOctave: 12);
Ora.new(~shepard).plot;
)

// Example 7: Tritone Stack
(
~tritones = OraGen.tritones(base: 100, size: 10);
~tritones.postln;
Ora.new(~tritones).plot;
)

// Example 8: Xenakis Sieve
(
~sieve = OraGen.sieve(base: 100, moduli: [8, 5, 3], size: 30);
~sieve.postln;
Ora.new(~sieve).plot;
)

// Example 9: Fibonacci Frequencies
(
~fibonacci = OraGen.fibonacci(base: 100, size: 12);
~fibonacci.postln;
Ora.new(~fibonacci).plot;
)

// Example 10: Generate, Transform, and Play
(
~dissonant = Ora.new(OraGen.inharmonic(100, 10, 1.1))
    .formantGap(fHole: 500, bw: 100, amount: 0.2)
    .centroidDilation(1.2);

~player = OraPlay.new(~dissonant);
~player.cluster(0.045, 5, \drsoft);
)

// Example 11: Create a sequence of different generated sets
(
~genSeq = OraSeq.new([
    Ora.new(OraGen.cluster(440, 8, 40)),
    Ora.new(OraGen.inharmonic(100, 8, 1.05)),
    Ora.new(OraGen.golden(200, 8, \up)),
    Ora.new(OraGen.primes(80, 8, 1.0)),
    Ora.new(OraGen.tritones(100, 8))
]);

~genSeq.plot;  // Visualize all generated sets
~genSeq.playSequence(\cluster, waitTime: 5);
)

// Example 12: Combine multiple generators
(
~combined = OraGen.cluster(200, 5, 20) ++ OraGen.golden(500, 5, \up);
~combined = ~combined.sort;
Ora.new(~combined).plot;
)


// ============================================================
// COMPLETE WORKFLOW EXAMPLES
// ============================================================

// Example 1: From generation to sequence
(
// Generate base material
~baseFreqs = OraGen.inharmonic(fundamental: 110, size: 16, stretch: 1.06);

// Create variations
~variations = [
    Ora.new(~baseFreqs),
    Ora.new(~baseFreqs).centroidDilation(1.25),
    Ora.new(~baseFreqs).formantGap(fHole: 450, bw: 120, amount: 0.2),
    Ora.new(~baseFreqs).retrograde(),
    Ora.new(~baseFreqs).centroidDilation(0.85).transposition(40),
    Ora.new(~baseFreqs).ratioSnap(base: 110, maxNum: 12, maxDen: 8, amt: 0.4)
];

// Sequence and visualize
~workflow = OraSeq.new(~variations);
~workflow.plot;
~workflow.info;
)

// Play the workflow
~workflow.playSequence(\cluster, waitTime: 7, args: [0.04, 6, \drsoft]);

// Example 2: Morphing between two spectra
(
~spectrumA = OraGen.cluster(base: 300, size: 12, spread: 80);
~spectrumB = OraGen.golden(base: 300, size: 12, direction: \up);

// Create morphing sequence
~morphSeq = (0, 0.125 .. 1.0).collect { |alpha|
    Ora.new(~spectrumA).otMorph(~spectrumB, alpha: alpha);
};

~morph = OraSeq.new(~morphSeq);
~morph.plot;
)

// Play the morph
~morph.playSequence(\dense, waitTime: 3, args: [0.035, 8, \drsoft]);

// Example 3: Exploring parametric space
(
// Base material
~explore = Array.series(10, 150, 35);

// Sample different centroid dilations
~dilationSeq = [0.7, 0.85, 1.0, 1.15, 1.3, 1.5].collect { |ratio|
    Ora.new(~explore).centroidDilation(r: ratio, curve: 1.0);
};

~exploration = OraSeq.new(~dilationSeq);
~exploration.plot;
~exploration.playSequence(\cluster, waitTime: 4);
)

// Example 4: Combined transformations with live control
(
// Create multiple transformation chains
~chains = [
    Ora.new(Array.series(8, 200, 50)).affineLog(1.2).geomeanLock(),
    Ora.new(Array.series(8, 200, 50)).intervalFlow(scale: 0.08, ph: 0.5),
    Ora.new(Array.series(8, 200, 50)).rankPower(1.4).transposition(30),
    Ora.new(Array.series(8, 200, 50)).projective(1, 0.3, 0.2, 1)
];

~chainSeq = OraSeq.new(~chains);
~chainSeq.plot;

// Play with varied methods
~chainSeq.playSequenceWithMethods(
    [\cluster, \fromCenter, \sparse, \wave],
    waitTime: 6
);
)


// ============================================================
// STOP ALL SOUND
// ============================================================

// Stop all running routines
~player.stop;
~seq.stopAll;
~oraSeq.stopAll;
~workflow.stopAll;
~morph.stopAll;
~exploration.stopAll;
~chainSeq.stopAll;

// Kill all synths
s.freeAll;

