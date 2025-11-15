// ============================================================
// ORA LIBRARY - QUICK START GUIDE
// ============================================================
//
// A minimal introduction to get you started with Ora
// For comprehensive examples, see examples.scd
//
// ============================================================

// STEP 1: Load the SynthDefs (run once per session)
// ============================================================
Ora.addSynths;


// STEP 2: Create your first frequency array
// ============================================================

// Option A: Start with a simple array
~freqs = [200, 300, 400, 500, 600, 700];

// Option B: Generate using OraGen
~freqs = OraGen.inharmonic(fundamental: 100, size: 8, stretch: 1.05);


// STEP 3: Transform it with Ora
// ============================================================

~myOra = Ora.new(~freqs)
    .centroidDilation(1.3)           // Expand the spectrum
    .formantGap(fHole: 450, bw: 100, amount: 0.2)  // Create a spectral gap
    .transposition(50);              // Shift up by 50 Hz

// View the transformation
~myOra.items.postln;
~myOra.plot;


// STEP 4: Play it
// ============================================================

~player = OraPlay.new(~myOra);
~player.cluster(0.045, 4, \drsoft);  // Play ascending

// Try different patterns:
~player.reverse(0.045, 4, \drpm);     // Descending
~player.fromCenter(0.045, 4, \drimp); // From center outward
~player.wave(0.045, 5, \drbase);      // Up then down
~player.dense(0.035, 8, \drsoft);     // Overlapping cloud

// Stop playback
~player.stop;


// STEP 5: Create and play a sequence
// ============================================================

(
// Create variations
~seq = OraSeq.new([
    Ora.new(~freqs),                              // Original
    Ora.new(~freqs).centroidDilation(1.2),        // Expanded
    Ora.new(~freqs).retrograde(),                 // Reversed
    Ora.new(~freqs).transposition(40)             // Shifted
]);

// Visualize the evolution
~seq.plot;

// Play through the sequence
~seq.playSequence(\cluster, waitTime: 5);
)

// Stop all
~seq.stopAll;


// ============================================================
// NEXT STEPS
// ============================================================
//
// 1. Explore examples.scd for comprehensive demonstrations
// 2. Read README.md for complete documentation
// 3. Experiment with different transformations and generators
// 4. Create your own transformation chains
//
// ============================================================


// USEFUL TRANSFORMATIONS
// ============================================================

// Serial techniques:
// .retrograde()
// .inversion()
// .retrogradeInversion()
// .rotation(n)
// .transposition(shift)

// Parametric transformations:
// .centroidDilation(r, curve)
// .formantGap(fHole, bw, amount)
// .affineLog(scale, shift)
// .rankPower(alpha)
// .otMorph(targetArray, alpha)


// USEFUL GENERATORS
// ============================================================

// OraGen.cluster(base, size, spread)
// OraGen.inharmonic(fundamental, size, stretch)
// OraGen.golden(base, size, direction)
// OraGen.primes(base, size, factor)
// OraGen.random(minFreq, maxFreq, size, density)


// PLAYBACK METHODS
// ============================================================

// Basic patterns:
// .cluster(amp, dur, synthdef, offset)
// .reverse(amp, dur, synthdef, offset)
// .random(amp, dur, synthdef, offset)
// .fromCenter(amp, dur, synthdef, offset)

// Textural patterns:
// .dense(amp, dur, synthdef, offset)
// .sparse(amp, dur, synthdef, minWait, maxWait)
// .pairs(amp, dur, synthdef, offset)
// .wave(amp, dur, synthdef, offset)

// Amplitude shaping:
// .ampCurve(amp, dur, synthdef, offset, ampCurve)
// .ampByFreq(amp, dur, synthdef, offset, minFreq, maxFreq, ampCurve)


// AVAILABLE SYNTHDEFS
// ============================================================

// \drbase  - Clean sine with subtle noise modulation
// \drpm    - Phase modulation with tremolo
// \drsoft  - Soft sine with gentle noise
// \drgend  - Gendy-based organic texture
// \drimp   - Impulse-based comb filter
// \drdfm   - Dual formant synthesis


// ============================================================
// Have fun exploring spectral composition with Ora!
// ============================================================

