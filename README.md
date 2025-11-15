# Ora

A comprehensive SuperCollider library for generating, transforming, playing, and sequencing frequency arrays for spectral composition and sound design.

## Overview

Ora provides four interconnected classes for working with frequency sets in spectral music composition:

- **Ora** - Transform frequency arrays using serial techniques and parametric operations
- **OraGen** - Generate dissonant and inharmonic pitch sets
- **OraPlay** - Play frequency arrays with diverse textural patterns
- **OraSeq** - Manage and visualize sequences of transformed frequency sets

## Installation

1. Copy the entire `Ora` folder to your SuperCollider Extensions directory:
   - macOS: `~/Library/Application Support/SuperCollider/Extensions/`
   - Linux: `~/.local/share/SuperCollider/Extensions/`
   - Windows: `%APPDATA%\SuperCollider\Extensions\`

2. Recompile the class library (Language > Recompile Class Library)

3. Load the included SynthDefs:

```supercollider
Ora.addSynths;  // Adds 6 SynthDefs optimized for spectral playback
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
```

## Core Classes

### Ora - Frequency Array Transformation

The main class for transforming frequency arrays. All methods are chainable and return `this`, enabling fluent transformation pipelines.

#### Serial Transformations

Classic serial music techniques adapted for frequency space:

- **retrograde()** - Reverse the frequency sequence
- **inversion(pivot)** - Mirror frequencies around a pivot point
- **retrogradeInversion(pivot)** - Reverse and invert
- **rotation(n)** - Circular shift (rotate by n positions)
- **transposition(shift)** - Add a constant shift to all frequencies

#### Parametric Transformations

Continuous transformations that reshape the frequency spectrum:

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

### OraGen - Frequency Set Generation

Generate diverse starting materials for spectral composition:

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

- **cluster(amp, dur, synthdef, offset)** - Ascending sequence
- **reverse(amp, dur, synthdef, offset)** - Descending sequence
- **random(amp, dur, synthdef, offset)** - Random order
- **fromCenter(amp, dur, synthdef, offset)** - Expanding from center

#### Textural Patterns

- **dense(amp, dur, synthdef, offset)** - Overlapping cloud texture
- **sparse(amp, dur, synthdef, minWait, maxWait)** - Irregular sparse timing
- **pairs(amp, dur, synthdef, offset)** - Play in pairs
- **wave(amp, dur, synthdef, offset)** - Ascending then descending

#### Amplitude Shaping

- **ampCurve(amp, dur, synthdef, offset, ampCurve)** - Amplitude envelope over sequence
- **ampByFreq(amp, dur, synthdef, offset, minFreq, maxFreq, ampCurve)** - Frequency-based amplitude mapping

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

## Included SynthDefs

The library includes six specialized SynthDefs optimized for spectral composition:

- **drbase** - Clean sine with subtle noise modulation and evolving character
- **drpm** - Phase modulation with amplitude tremolo
- **drsoft** - Soft sine with gentle noise crossfade
- **drgend** - Gendy-based organic texture with bandpass filtering
- **drimp** - Impulse-based comb filter (pitched resonance)
- **drdfm** - Rich dual-formant synthesis with beating

Load them with: `Ora.addSynths;`

## Examples

The library includes a comprehensive `examples.scd` file with over 50 working examples demonstrating:

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

1. **Generate** or create initial frequency array
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
```

## Design Philosophy

Ora bridges two musical traditions:

1. **Serial Music** - Classical transformations (retrograde, inversion) adapted for frequency space
2. **Spectral Music** - Continuous transformations in log-frequency and perceptual domains

All transformations preserve array size and operate on frequency values (Hz), making them compatible with any synthesis technique in SuperCollider.

## Technical Notes

- All transformations work in-place and return `this` for method chaining
- Frequency arrays are clipped to safe ranges (1e-6 to 1e9 Hz internally)
- OraPlay filters playback to 40-14000 Hz range
- Transformations operate on the `items` instance variable
- Compatible with plain arrays: `OraPlay.new([100, 200, 300])`

## Credits

Developed for spectral composition and sound design in SuperCollider.

## License

Free to use and modify for musical and research purposes.
