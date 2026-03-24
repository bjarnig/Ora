// ============================================================
// ORA VISUALIZATION EXPERIMENTS
// ============================================================
//
// Five new ways to look at Ora frequency arrays.
// Evaluate each section independently in SuperCollider.
//
// New methods on Ora:
//   plotRadial    - Circular polar plot (angle=index, radius=log freq)
//   plotPiano     - Pitch grid with cents deviation from 12-TET
//   plotMatrix    - NxN ratio heatmap between all frequency pairs
//   plotWaveform  - Time-domain sum of sinusoids
//
// New method on OraSeq:
//   plotHeatmap   - Frequency density evolution across sequence
//
// ============================================================

// SETUP
Ora.addSynths;

// Create test material
(
~harmonic = Array.series(8, 100, 100);
~ora = Ora.new(~harmonic);
~golden = Ora.new(OraGen.golden(200, 10, \up));
~cluster = Ora.new(OraGen.cluster(440, 12, 60));
~inharmonic = Ora.new(OraGen.inharmonic(100, 10, 1.08));
)


// ============================================================
// 1. RADIAL PLOT
// ============================================================
// Frequencies arranged in a circle.
// Angle = array index, radius = log(frequency).
// The connecting polygon reveals the "shape" of the frequency set.
// Symmetries, clusters, and gaps become immediately visible.

~ora.plotRadial("Harmonic Series");
~golden.plotRadial("Golden Ratio");
~cluster.plotRadial("Cluster");

// Compare how transformations change the shape
(
Ora.new(~harmonic).plotRadial("Original");
Ora.new(~harmonic).centroidDilation(1.5).plotRadial("Dilated 1.5x");
Ora.new(~harmonic).retrograde.plotRadial("Retrograde");
)

// The radial view makes scramble vs. sorted very clear
(
Ora.new(~harmonic).plotRadial("Sorted");
Ora.new(~harmonic).scramble(seed: 42).plotRadial("Scrambled");
)


// ============================================================
// 2. PITCH MAP (PIANO)
// ============================================================
// Maps frequencies onto a pitch axis (MIDI notes / semitones).
// Background grid shows 12-TET semitones (C notes highlighted).
// Each dot shows its cents deviation from the nearest semitone.
// Green dots = close to 12-TET, red dots = far from it.

~ora.plotPiano("Harmonic Series");
~golden.plotPiano("Golden Ratio");

// Inharmonic series: see how far from 12-TET the partials fall
Ora.new(OraGen.inharmonic(100, 12, 1.08)).plotPiano("Inharmonic");

// After ratioSnap: partials pulled toward simple ratios
Ora.new(OraGen.inharmonic(100, 12, 1.08))
    .ratioSnap(base: 100, amt: 0.6)
    .plotPiano("After ratioSnap");

// Just intonation vs equal temperament
OraRatios.justIntonation(261.63).asOra.plotPiano("Just Intonation from C4");
OraRatios.equalTemperament(261.63).asOra.plotPiano("Equal Temperament from C4");


// ============================================================
// 3. RATIO MATRIX
// ============================================================
// NxN grid of frequency ratios (sorted low to high).
// Color hue cycles per octave (octave-related pairs share colors).
// Brightness = interval width (close intervals are brightest).
// For <= 12 frequencies, ratio values are printed in each cell.

~ora.plotMatrix("Harmonic Series");
~golden.plotMatrix("Golden Ratio");
~inharmonic.plotMatrix("Inharmonic");

// Harmonic series has a very structured matrix (simple ratios)
Ora.new(Array.series(8, 100, 100)).plotMatrix("Harmonic 8");

// Fibonacci series: less regular but with patterns
Ora.new(OraGen.fibonacci(50, 8)).plotMatrix("Fibonacci");

// Prime series: highly irregular ratios
Ora.new(OraGen.primes(50, 8)).plotMatrix("Primes");


// ============================================================
// 4. WAVEFORM PREVIEW
// ============================================================
// Sum of sinusoids at the array's frequencies.
// Shows the actual wave shape these frequencies produce together.
// Uses SC's built-in array plot.

~ora.plotWaveform("Harmonic Waveform");
~cluster.plotWaveform("Cluster Waveform");
~inharmonic.plotWaveform("Inharmonic Waveform");

// Different time windows reveal different structure
~ora.plotWaveform("Short: 5ms", duration: 0.005);
~ora.plotWaveform("Medium: 25ms", duration: 0.025);
~ora.plotWaveform("Long: 100ms", duration: 0.1);

// Compare: harmonic (periodic) vs inharmonic (quasi-periodic)
Ora.new(Array.series(8, 100, 100)).plotWaveform("Harmonic (periodic)");
Ora.new(OraGen.golden(100, 8)).plotWaveform("Golden (quasi-periodic)");

// Tight cluster produces beating patterns
Ora.new(OraGen.cluster(440, 6, 15)).plotWaveform("Tight cluster beating", duration: 0.1);


// ============================================================
// 5. SEQUENCE HEATMAP (OraSeq)
// ============================================================
// Each column = one Ora in the sequence.
// Y axis = frequency (log scale).
// Color intensity = frequency density in that bin.
// Like a spectrogram of your transformation chain.

// Transformation evolution
(
~seq = OraSeq.new([
    Ora.new(~harmonic),
    Ora.new(~harmonic).centroidDilation(1.2),
    Ora.new(~harmonic).centroidDilation(1.5),
    Ora.new(~harmonic).formantGap(fHole: 500, bw: 120, amount: 0.3),
    Ora.new(~harmonic).retrograde.transposition(50),
    Ora.new(~harmonic).rankPower(1.5),
    Ora.new(~harmonic).intervalFlow(scale: 0.05),
    Ora.new(~harmonic).chaos(amount: 0.3)
]);

~seq.plotHeatmap("Transformation Evolution");
)

// Morphing sequence: watch frequencies glide between two spectra
(
~specA = OraGen.cluster(300, 12, 80);
~specB = OraGen.golden(300, 12, \up);

~morphSeq = OraSeq.new(
    (0, 0.1 .. 1.0).collect { |alpha|
        Ora.new(~specA).otMorph(~specB, alpha: alpha);
    }
);

~morphSeq.plotHeatmap("Morph: Cluster -> Golden");
)

// Dilation sweep
(
~dilSeq = OraSeq.new(
    [0.5, 0.7, 0.85, 1.0, 1.15, 1.3, 1.5, 2.0].collect { |r|
        Ora.new(~harmonic).centroidDilation(r);
    }
);
~dilSeq.plotHeatmap("Dilation Sweep: 0.5 -> 2.0");
)


// ============================================================
// COMBINED EXPLORATION
// ============================================================
// Open multiple views simultaneously on the same data
// to see it from every angle at once.

(
~freqs = OraGen.inharmonic(80, 10, 1.06);
~o = Ora.new(~freqs);
~o.plotStems("Stems");
~o.plotRadial("Radial");
~o.plotPiano("Pitch Map");
~o.plotMatrix("Matrix");
~o.plotWaveform("Waveform");
)
