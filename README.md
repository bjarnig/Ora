# Ora

A SuperCollider library for generating, transforming, playing, and sequencing frequency arrays.

## Overview

Ora provides six classes for working with frequency sets in composition:

- **Ora** - Transform frequency arrays using serial techniques and parametric operations
- **OraGen** - Generate dissonant and inharmonic pitch sets
- **OraPlay** - Play frequency arrays with diverse textural patterns
- **OraSeq** - Manage and visualize sequences of transformed frequency sets
- **OraRatios** - Build and manipulate frequency arrays from ratio relationships
- **OraSpear** - Import and extract frequencies from SPEAR analysis files

## Installation

1. Copy the entire `Ora` folder to your SuperCollider Extensions directory

2. Recompile the class library (Language > Recompile Class Library)

3. Load the included SynthDefs:

```supercollider
Ora.addSynths;  // Adds 9 SynthDefs optimized for spectral playback
```

## Quick Start

See `quickstart.scd` for an interactive introduction, or try this:

```supercollider
// Load SynthDefs first
Ora.addSynths;

// Create and transform a frequency array
~myOra = Ora.new([200, 300, 400, 500, 600, 700])
    .centroidDilation(1.3)
    .formantGap(fHole: 450, bw: 100, amount: 0.2)
    .transposition(50);

// Visualize the result
~myOra.plot;

// Play it
~player = OraPlay.new(~myOra);
~player.cluster(0.045, 4, \drsoft);

// Or create dissonant clusters
~dissonant = Ora([100, 150, 200, 250, 300])
    .shatter(pieces: 3, spread: 0.08)
    .detune(cents: 20)
    .chaos(amount: 0.3);

~p = OraPlay(~dissonant);
~p.clusterGrad(ampFrom: 0.02, ampTo: 0.05, durFrom: 8, durTo: 2, ampComp: 1.0);
```

## Core Classes

### Ora - Frequency Array Transformation

The main class for transforming frequency arrays. All methods are chainable and return `this`, enabling fluent transformation pipelines.

#### Serial Transformations

- **retrograde()** - Reverse the frequency sequence
- **inversion(pivot)** - Mirror frequencies around a pivot point
- **retrogradeInversion(pivot)** - Reverse and invert
- **rotation(n)** - Circular shift (rotate by n positions)
- **transposition(shift)** - Add a constant shift to all frequencies

#### Parametric Transformations

- **affineLog(scale, shift)** - Scale and shift in log-frequency space
- **centroidDilation(r, curve)** - Expand/compress around the log-centroid
- **rotate(frac)** - Fractional circular rotation with interpolation
- **projective(a, b, c, d)** - Mobius-like spectral warping
- **intervalFlow(scale, ph, k)** - Generate frequencies from interval field
- **blend(targetArray, alpha)** - Linear interpolation to target frequencies
- **rankPower(alpha)** - Power-law distortion by frequency rank
- **invert(center)** - Invert frequencies around a center point
- **affineIndex(m, b, mix)** - Affine index mapping with interpolation
- **geomeanLock(geo)** - Lock geometric mean to preserve spectral centroid
- **otMorph(targetArray, alpha)** - Optimal transport-like morphing
- **ratioSnap(base, maxNum, maxDen, amt)** - Snap to simple frequency ratios
- **formantGap(fHole, bw, amount)** - Create formant-like spectral gaps

#### Spatial Transformations

- **shift(hz, ratio)** - Transpose by Hz offset and/or ratio multiplication
- **expand(center, amount)** - Spread frequencies away from center
- **contract(center, amount)** - Pull frequencies toward center
- **stretch(power, anchor)** - Non-linear expansion using power curve
- **mirror(center)** - Reflect frequencies around center
- **pinch(center, amount)** - Distance-based compression toward center
- **warp(curve, center)** - Non-linear frequency distribution warping
- **fold(low, high)** - Fold frequencies that exceed bounds
- **bounce(low, high)** - Fold with wrapping behavior

#### Array Manipulation

- **rotate(steps)** - Circular rotation of array
- **reverse()** - Reverse array order
- **scramble(seed)** - Random shuffle
- **splay(minGap)** - Ensure minimum frequency spacing
- **tilt(pivot, low, high)** - Progressive scaling from low to high

#### Dissonant Transformations

- **fluctuate(amount, seed)** - Random variation per frequency
- **detune(cents, seed)** - Add subtle detuning in cents
- **cluster(numClusters, spread, seed)** - Pack into tight clusters
- **shatter(pieces, spread, seed)** - Break each frequency into pieces
- **crunch(factor, center)** - Extreme compression
- **chaos(amount, seed)** - Chaotic redistribution
- **wobble(amount, seed)** - Sinusoidal frequency drift
- **inharmonic(amount, seed)** - Destroy harmonic relationships
- **fracture(splits, jitter, seed)** - Split with microtonal jitter
- **crush(bands)** - Quantize to frequency bands
- **drift(speed, depth, seed)** - Phase-based frequency drift

### OraGen - Frequency Set Generation

- **cluster(base, size, spread)** - Microtonal clusters
- **inharmonic(fundamental, size, stretch)** - Non-integer overtone series
- **golden(base, size, direction)** - Golden ratio-based spacing
- **primes(base, size, factor)** - Prime number frequency series
- **random(minFreq, maxFreq, size, density)** - Random frequency sets
- **shepard(base, octaves, notesPerOctave)** - Shepard tone illusions
- **tritones(base, size)** - Tritone stacks
- **sieve(base, moduli, size)** - Xenakis sieve technique
- **fibonacci(base, size)** - Fibonacci sequence frequencies

### OraPlay - Playback Patterns

Transform Ora objects into sound using various playback strategies:

#### Basic Patterns

- **cluster(amp, dur, synthdef, offset, ampComp)** - Ascending sequence
- **reverse(amp, dur, synthdef, offset, ampComp)** - Descending sequence
- **random(amp, dur, synthdef, offset)** - Random order
- **fromCenter(amp, dur, synthdef, offset)** - Expanding from center

#### Advanced Patterns

- **clusterGrad(ampFrom, ampTo, durFrom, durTo, synthdef, offsetFrom, offsetTo, ampComp)** - Gradient cluster with interpolated parameters
- **dense(amp, dur, synthdef, offset, ampComp)** - Overlapping cloud texture
- **sparse(amp, dur, synthdef, minWait, maxWait)** - Irregular sparse timing
- **pairs(amp, dur, synthdef, offset)** - Play in pairs
- **wave(amp, dur, synthdef, offset)** - Ascending then descending

#### Amplitude Shaping

- **ampCurve(amp, dur, synthdef, offset, ampCurve)** - Amplitude envelope over sequence
- **ampByFreq(amp, dur, synthdef, offset, minFreq, maxFreq, ampCurve)** - Frequency-based amplitude mapping
- **ampComp parameter (0-1)** - Perceptual amplitude compensation for high frequencies

All patterns support automatic stereo panning for spatial distribution.

### OraSeq - Sequence Management

Manage collections of Ora transformations and visualize their evolution:

- **add(ora)** - Add an Ora to the sequence
- **at(index)** - Access specific Ora by index
- **current()** / **next()** / **prev()** / **goto(index)** - Navigate sequence
- **plot()** - Multi-window visualization (each Ora separately)
- **plotCombined()** - Single-window stacked visualization
- **playSequence(method, waitTime, args)** - Play through sequence
- **playSequenceWithMethods(methodsArray, waitTime, argsArray)** - Different method per Ora
- **info()** - Print sequence information
- **asArrays()** - Export all frequency arrays

### OraRatios - Ratio-Based Frequency Generation

Build frequency arrays from intervallic ratios for precise tuning control:

#### Creation & Analysis

- **OraRatios.new(startFreq, ratios)** - Create from starting frequency and ratio array
- **OraRatios.fromFreqs(freqArray)** - Analyze existing frequencies to extract ratios
- **printAnalysis()** - Display detailed ratio breakdown
- **buildFreqs()** - Rebuild frequency array from current ratios

#### Manipulation

- **transpose(newStartFreq)** - Change starting frequency, preserving ratios
- **setRatio(index, newRatio)** - Modify specific ratio
- **scaleRatios(scalar)** - Expand/compress all intervals
- **copy()** - Create independent copy

#### Preset Patterns

- **microCluster(startFreq)** - Tight microtonal intervals
- **tritone(startFreq)** - Devil's interval based
- **irrational(startFreq)** - Golden ratio, √3, √5, √7
- **detuned(startFreq)** - Slightly off pure intervals
- **eerie(startFreq)** - Mixed dissonant techniques
- **extreme(startFreq)** - Very tight clusters
- **harmonic(startFreq, numPartials)** - Natural overtone series
- **justIntonation(startFreq)** - Pure interval ratios
- **equalTemperament(startFreq, semitones)** - 12-TET scales
- **random(startFreq, numRatios, minRatio, maxRatio)** - Random ratios

#### Integration

- **asOra()** - Convert to Ora object
- **asOraPlay(minFreq, maxFreq, numChannels)** - Convert to OraPlay object

### OraSpear - SPEAR Analysis Import

Import and extract representative frequencies from SPEAR spectral analysis files:

#### Loading & Parsing

- **OraSpear.new(filepath)** - Create instance with path to SPEAR .txt file
- **parse()** - Parse the SPEAR file and load partial data

#### Selection Methods

- **selectPartials(n, sortBy)** - Select top N partials by criteria:
  - `\duration` - Longest-lasting partials
  - `\meanAmp` - Highest average amplitude
  - `\maxAmp` - Highest peak amplitude
  - `\meanFreq` - Highest average frequency
  - `\startTime` - Earliest-starting partials

#### Frequency Extraction

- **freqs(n, sortBy, method)** - Extract frequencies using method:
  - `\mean` - Average frequency across partial's lifetime
  - `\median` - Median frequency
  - `\min` / `\max` - Minimum/maximum frequency
  - `\start` / `\end` - Frequency at start/end
  - `\weightedMean` - Amplitude-weighted average

#### Analysis & Info

- **printInfo()** - Display file statistics and frequency ranges
- **partialInfo(id)** - Detailed info about specific partial

#### Integration

- **asOra(n, sortBy, method)** - Convert to Ora object with extracted frequencies

#### Example Workflow

```supercollider
// Load and parse SPEAR analysis
~spear = OraSpear.new("path/to/analysis.txt");
~spear.parse;
~spear.printInfo;

// Extract 12 longest partials using weighted mean
~freqs = ~spear.freqs(12, \duration, \weightedMean);

// Or convert directly to Ora
~ora = ~spear.asOra(12, \duration, \weightedMean);

// Transform and play
~ora.detune(cents: 10).wobble(0.3);
~p = OraPlay(~ora);
~p.cluster(amp: 0.05, ampComp: 0.8);
```

**Note:** To create SPEAR analysis files, use the [SPEAR software](http://www.klingbeil.com/spear/) to analyze audio files. Export as `.txt` format for use with OraSpear.

## Included SynthDefs

- **drbase** - Clean sine with subtle noise modulation and evolving character
- **drpm** - Phase modulation with amplitude tremolo
- **drpmg** - Phase modulation with gate control (ASR envelope)
- **drpmgd** - Phase modulation with ADSR envelope
- **drpmvara** - Phase modulation variation with higher modulation depth
- **drsoft** - Soft sine with gentle noise crossfade
- **drgend** - Gendy-based organic texture with bandpass filtering
- **drimp** - Impulse-based comb filter (pitched resonance)
- **drdfm** - Rich dual-formant synthesis with beating

Load them with: `Ora.addSynths;`

## Examples

The library includes an `examples.scd` file with over 50 working examples demonstrating:

- Serial transformations (retrograde, inversion, rotation)
- Parametric transformations (dilation, warping, morphing)
- Frequency set generation (inharmonic, golden ratio, primes, Fibonacci)
- All playback patterns and amplitude shaping techniques
- Sequence management and visualization
- Complete workflows from generation to performance
- Real-time transformation exploration

Open `examples.scd` in SuperCollider and evaluate examples individually to learn the library.

## Workflow

The typical Ora workflow:

1. **Generate** or import initial frequency array
   - Use OraGen for algorithmic generation
   - Use OraRatios for ratio-based tuning
   - Use OraSpear to import from SPEAR analysis
   - Or create manually with arrays
2. **Transform** using Ora methods (chainable)
3. **Visualize** with plot methods
4. **Play** using OraPlay patterns
5. **Sequence** multiple variations with OraSeq

```supercollider
// Complete workflow example
(
// 1. Generate
~freqs = OraGen.inharmonic(100, 12, 1.08);

// 2. Create sequence of transformations
~seq = OraSeq.new([
    Ora.new(~freqs),
    Ora.new(~freqs).centroidDilation(1.3),
    Ora.new(~freqs).formantGap(fHole: 500, bw: 120, amount: 0.25),
    Ora.new(~freqs).retrograde().transposition(30)
]);

// 3. Visualize evolution
~seq.plot;

// 4. Play sequence
~seq.playSequence(\cluster, waitTime: 6);
)

// Dissonant transformations example
(
~o = Ora([100, 200, 300, 400, 500, 600, 700, 800]);

// Create eerie, unstable cluster
~o.cluster(numClusters: 3, spread: 30)
  .detune(cents: 15)
  .wobble(amount: 0.1);

// Play with gradient
~p = OraPlay(~o);
~p.clusterGrad(
    ampFrom: 0.03, 
    ampTo: 0.06, 
    durFrom: 6, 
    durTo: 3,
    offsetFrom: 0.08, 
    offsetTo: 0.2,
    ampComp: 1.0
);
)

// Ratio-based tuning example
(
// Build from golden ratio and tritones
~r = OraRatios.eerie(60);
~r.printAnalysis;

// Transpose and scale
~r.transpose(80).scaleRatios(1.2);

// Convert and play
~p = ~r.asOraPlay;
~p.cluster(amp: 0.05, ampComp: 0.8);
)

// SPEAR analysis import example
(
// Load SPEAR analysis file
~spear = OraSpear.new("path/to/bell.txt");
~spear.parse.printInfo;

// Extract frequencies and transform
~ora = ~spear.asOra(16, \duration, \weightedMean);
~ora.fracture(splits: 2, jitter: 0.03)
    .detune(cents: 8)
    .chaos(amount: 0.2);

// Play with gradient
~p = OraPlay(~ora);
~p.clusterGrad(
    ampFrom: 0.02, ampTo: 0.08,
    durFrom: 8, durTo: 2,
    offsetFrom: 0.15, offsetTo: 0.05,
    ampComp: 1.0
);
)
```

## Technical Notes

- All transformations work in-place and return `this` for method chaining
- Frequency arrays are clipped to safe ranges (1e-6 to 1e9 Hz internally)
- OraPlay filters playback to 40-14000 Hz range
- Transformations operate on the `items` instance variable
- Compatible with plain arrays: `OraPlay.new([100, 200, 300])`

## License

Free to use
