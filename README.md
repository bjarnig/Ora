# Ora

A set of processes for generating, transforming, and sequencing frequency arrays.

## Overview

- **Ora** - Transform frequency arrays using serial techniques and parametric operations
- **OraGen** - Generate dissonant and inharmonic pitch sets
- **OraPlay** - Play frequency arrays with diverse textural patterns
- **OraSeq** - Manage and visualize sequences of transformed frequency sets
- **OraRatios** - Build and manipulate frequency arrays from ratio relationships
- **OraSpear** - Import and extract frequencies from SPEAR analysis files

## Use

```supercollider
// Load
Ora.addSynths;

// Create
~myOra = Ora.new([200, 300, 400, 500, 600, 700])
    .centroidDilation(1.3)
    .formantGap(fHole: 450, bw: 100, amount: 0.2)
    .transposition(50);

// Visualize
~myOra.plot;

// Play
~player = OraPlay.new(~myOra);
~player.cluster(0.045, 4, \drsoft);

// Cluster
~dissonant = Ora([100, 150, 200, 250, 300])
    .shatter(pieces: 3, spread: 0.08)
    .detune(cents: 20)
    .chaos(amount: 0.3);

~p = OraPlay(~dissonant);
~p.clusterGrad(ampFrom: 0.02, ampTo: 0.05, durFrom: 8, durTo: 2, ampComp: 1.0);
```

### Ora - Transformation

Methods are chainable and return `this`, enabling fluent transformation pipelines.

#### Serial

- **retrograde()** - Reverse the sequence
- **inversion(pivot)** - Mirror around a pivot point
- **retrogradeInversion(pivot)** - Reverse and invert
- **rotation(n)** - Circular shift (rotate by n positions)
- **transposition(shift)** - Add a constant shift to all frequencies

#### Parametric

- **affineLog(scale, shift)** - Scale and shift in log-frequency space
- **centroidDilation(r, curve)** - Expand/compress around the log-centroid
- **rotate(frac)** - Fractional circular rotation with interpolation
- **projective(a, b, c, d)** - Spectral warping
- **intervalFlow(scale, ph, k)** - Generate frequencies from interval field
- **blend(targetArray, alpha)** - Linear interpolation to target frequencies
- **rankPower(alpha)** - Power-law distortion by frequency rank
- **invert(center)** - Invert frequencies around a center point
- **affineIndex(m, b, mix)** - Affine index mapping with interpolation
- **geomeanLock(geo)** - Lock geometric mean to preserve spectral centroid
- **otMorph(targetArray, alpha)** - Optimal transport-like morphing
- **ratioSnap(base, maxNum, maxDen, amt)** - Snap to simple frequency ratios
- **formantGap(fHole, bw, amount)** - Create formant-like spectral gaps

#### Spatial

- **shift(hz, ratio)** - Transpose by Hz offset and/or ratio multiplication
- **expand(center, amount)** - Spread frequencies away from center
- **contract(center, amount)** - Pull frequencies toward center
- **stretch(power, anchor)** - Non-linear expansion using power curve
- **mirror(center)** - Reflect frequencies around center
- **pinch(center, amount)** - Distance-based compression toward center
- **warp(curve, center)** - Non-linear frequency distribution warping
- **fold(low, high)** - Fold frequencies that exceed bounds
- **bounce(low, high)** - Fold with wrapping behavior

#### Arrays

- **rotateSteps(steps)** - Circular rotation of array by integer steps
- **reverse()** - Reverse array order
- **scramble(seed)** - Random shuffle
- **splay(minGap)** - Ensure minimum frequency spacing
- **tilt(pivot, low, high)** - Progressive scaling from low to high

#### Dissonant

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

#### Visualization

- **plot()** - Basic array plot
- **plotStems()** - Stem plot with log-scale Y axis and color-coded frequency dots
- **plotSpectrum()** - Log-frequency spectral view showing frequency distribution
- **plotIntervals()** - Dual view showing Hz intervals and ratios between sorted frequencies
- **plotCompare(other, nameA, nameB)** - Side-by-side comparison of two Ora objects

### Generation

- **cluster(base, size, spread)** - Microtonal clusters
- **inharmonic(fundamental, size, stretch)** - Non-integer overtone series
- **golden(base, size, direction)** - Golden ratio-based spacing
- **primes(base, size, factor)** - Prime number frequency series
- **random(minFreq, maxFreq, size, density)** - Random frequency sets
- **shepard(base, octaves, notesPerOctave)** - Shepard tone illusions
- **tritones(base, size)** - Tritone stacks
- **sieve(base, moduli, size)** - Xenakis sieve technique
- **fibonacci(base, size)** - Fibonacci sequence frequencies

### OraPlay

Transform Ora objects into sound using various playback strategies:

#### Basic

- **cluster(amp, dur, synthdef, offset, ampComp)** - Ascending sequence
- **reverse(amp, dur, synthdef, offset, ampComp)** - Descending sequence
- **random(amp, dur, synthdef, offset)** - Random order
- **fromCenter(amp, dur, synthdef, offset)** - Expanding from center

#### Advanced

- **clusterGrad(ampFrom, ampTo, durFrom, durTo, synthdef, offsetFrom, offsetTo, ampComp)** - Gradient cluster with interpolated parameters
- **dense(amp, dur, synthdef, offset, ampComp)** - Overlapping cloud texture
- **sparse(amp, dur, synthdef, minWait, maxWait)** - Irregular sparse timing
- **pairs(amp, dur, synthdef, offset)** - Play in pairs
- **wave(amp, dur, synthdef, offset)** - Ascending then descending

#### Amplitude

- **ampCurve(amp, dur, synthdef, offset, ampCurve)** - Amplitude envelope over sequence
- **ampByFreq(amp, dur, synthdef, offset, minFreq, maxFreq, ampCurve)** - Frequency-based amplitude mapping
- **ampComp parameter (0-1)** - Perceptual amplitude compensation for high frequencies

All patterns support automatic stereo panning for spatial distribution.

### OraSeq

Manage collections of Ora transformations and visualize their evolution:

- **add(ora)** - Add an Ora to the sequence
- **at(index)** - Access specific Ora by index
- **current()** / **next()** / **prev()** / **goto(index)** - Navigate sequence
- **plot()** - Each Ora as a separate stem plot window
- **plotCombined()** - Single-window stacked bar view
- **plotOverlay()** - All Oras overlaid in one window with color-coded stems
- **playSequence(method, waitTime, args)** - Play through sequence
- **playSequenceWithMethods(methodsArray, waitTime, argsArray)** - Different method per Ora
- **info()** - Print sequence information
- **asArrays()** - Export all frequency arrays

### OraRatios

Build frequency arrays from intervallic ratios for precise tuning control:

#### Creation / Analysis

- **OraRatios.new(startFreq, ratios)** - Create from starting frequency and ratios
- **OraRatios.fromFreqs(freqArray)** - Analyze existing frequencies to ratios
- **printAnalysis()** - Display ratio breakdown
- **buildFreqs()** - Rebuild from current ratios
- **plot()** - Plot of generated frequencies
- **plotRatios()** - Intervals and ratios visualization

#### Manipulation

- **transpose(newStartFreq)** - Change initial frequency, preserving ratios
- **setRatio(index, newRatio)** - Modify ratio
- **scaleRatios(scalar)** - Expand/compress intervals
- **copy()** - Create copy

#### Patterns

- **microCluster(startFreq)** - Microtonal intervals
- **irrational(startFreq)** - Irrational ratio
- **detuned(startFreq)** - Off pure intervals
- **eerie(startFreq)** - Dissonant
- **extreme(startFreq)** - Tight clusters
- **harmonic(startFreq, numPartials)** - Overtone series
- **justIntonation(startFreq)** - Pure interval ratios
- **equalTemperament(startFreq, semitones)** - 12-TET scales
- **random(startFreq, numRatios, minRatio, maxRatio)** - Random ratios

#### Integrate

- **asOra()** - Convert to Ora object
- **asOraPlay(minFreq, maxFreq, numChannels)** - Convert to OraPlay object

### OraSpear

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

#### Workflow

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

## SynthDefs

- **drbase** - Clean sine with subtle noise modulation and evolving character
- **drpm** - Phase modulation with amplitude tremolo
- **drpmg** - Phase modulation with gate control (ASR envelope)
- **drpmgd** - Phase modulation with ADSR envelope
- **drpmvara** - Phase modulation variation with higher modulation depth
- **drsoft** - Soft sine with gentle noise crossfade
- **drgend** - Gendy-based organic texture with bandpass filtering
- **drimp** - Impulse-based comb filter (pitched resonance)
- **drdfm** - Rich dual-formant synthesis with beating