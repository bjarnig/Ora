/*

Ora — Guide & Documentation.
A system for generating, transforming, playing and visualizing frequency arrays.

*/


/*

	Setup.

*/

(

s.waitForBoot { Ora.addSynths };

)


/*

	Creating an Ora object.

*/

~o = Ora([100, 200, 350, 500, 800, 1200, 1800]);
~o = Ora(OraGen.inharmonic(100, 10, 1.08));
~o = OraRatios.eerie(80).asOra;
~o.printItems;


/*

	Serial transformations.

*/

(
~f = [100, 200, 350, 500, 800, 1200, 1800];
Ora(~f).retrograde.printItems;
Ora(~f).inversion.printItems;
Ora(~f).retrogradeInversion.printItems;
Ora(~f).rotation(3).printItems;
Ora(~f).transposition(50).printItems;
)


/*

	Parametric transformations.

*/

(
~f = [100, 200, 350, 500, 800, 1200, 1800];
Ora(~f).affineLog(scale: 1.3).printItems;
Ora(~f).centroidDilation(1.5).printItems;
Ora(~f).rotate(0.25).printItems;
Ora(~f).projective(1, 0.2, 0.1, 1).printItems;
Ora(~f.scramble).intervalFlow.printItems;
Ora(~f).blend([150, 300, 450, 600, 750, 900, 1050], 0.5).printItems;
Ora(~f).rankPower(2.0).printItems;
Ora(~f).invert.printItems;
Ora(~f).geomeanLock.printItems;
)


/*

	Spatial transformations.

*/

(
~f = [100, 200, 350, 500, 800, 1200, 1800];
Ora(~f).shift(hz: 50).printItems;
Ora(~f).shift(ratio: 1.5).printItems;
Ora(~f).expand(amount: 2.0).printItems;
Ora(~f).contract(amount: 0.5).printItems;
Ora(~f).stretch(power: 3.0).printItems;
Ora(~f).mirror.printItems;
Ora(~f).warp(curve: 2.0).printItems;
Ora(~f).expand(amount: 3.0).fold(80, 2000).printItems;
Ora(~f).pinch(amount: 0.8).printItems;
Ora(~f).tilt(low: 0.5, high: 1.5).printItems;
)


/*

	Dissonant transformations.

*/

(
~f = [100, 200, 350, 500, 800, 1200, 1800];
Ora(~f).fluctuate(0.08).printItems;
Ora(~f).detune(15).printItems;
Ora(~f).shatter(pieces: 3, spread: 0.06).printItems;
Ora(~f).cluster(numClusters: 3, spread: 40).printItems;
Ora(~f).crunch(0.15).printItems;
Ora(~f).chaos(0.4).printItems;
Ora(~f).wobble(0.3).printItems;
Ora(~f).inharmonic(0.6).printItems;
Ora(~f).fracture(splits: 2, jitter: 0.03).printItems;
Ora(~f).crush(bands: 4).printItems;
Ora(~f).drift(speed: 0.2, depth: 60).printItems;
)


/*

	Array reordering.

*/

(
~f = [100, 200, 350, 500, 800, 1200, 1800];
Ora(~f).reverse.printItems;
Ora(~f).rotateSteps(3).printItems;
Ora(~f).scramble(seed: 42).printItems;
Ora([100, 102, 105, 200, 201]).splay(minGap: 20).printItems;
)


/*

	Chaining transformations.

*/

(
Ora([100, 200, 350, 500, 800, 1200, 1800])
	.centroidDilation(1.3)
	.formantGap(fHole: 600, bw: 150, amount: 0.2)
	.fluctuate(0.03)
	.detune(5)
	.printItems;
)

(
Ora([100, 200, 300, 400, 500, 600])
	.shatter(pieces: 2, spread: 0.04)
	.chaos(0.3).detune(12)
	.fold(60, 3000)
	.printItems;
)


/*

	Formant gap and optimal transport morph.

*/

(
~f = [200, 300, 500, 700, 900, 1100, 1400, 1800, 2200];
Ora(~f).plotCompare(Ora(~f).formantGap(800, 200, 0.25), "Original", "Gap 800Hz");
)

(
~src = [100, 200, 400, 800, 1600, 3200];
~tgt = [150, 350, 550, 750, 1100, 2500];
Ora(~src).otMorph(~tgt, 0.25).plotStems("25%");
Ora(~src).otMorph(~tgt, 0.75).plotStems("75%");
)


/*

	Ratio snap.

*/

(
~messy = OraGen.random(100, 1000, 8);
Ora(~messy).ratioSnap(100, 16, 16, 0.5).printItems;
Ora(~messy).ratioSnap(100, 16, 16, 0.9).printItems;
)


/*

	OraGen: algorithmic frequency generation.

*/

(
OraGen.cluster(440, 8, 30).postln;
OraGen.inharmonic(100, 10, 1.08).postln;
OraGen.golden(200, 8).postln;
OraGen.primes(60, 8).postln;
OraGen.random(100, 2000, 12, 0.8).postln;
OraGen.tritones(80, 8).postln;
OraGen.sieve(100, [8, 5, 3], 40).postln;
OraGen.fibonacci(50, 10).postln;
OraGen.shepard(110, 3, 6).postln;
)


/*

	OraRatios: building frequencies from intervallic ratios.

*/

~r = OraRatios(80, [1.5, 1.33, 1.25, 1.2, 1.17]);
~r.printAnalysis;

~r = OraRatios.fromFreqs([100, 150, 200, 300, 450]);
~r.printAnalysis;

~r.setRatio(2, 1.414);
~r.scaleRatios(1.5);
~r.transpose(200);
~r.freqs;


/*

	OraRatios presets.

*/

(
OraRatios.microCluster(60).freqs.postln;
OraRatios.tritone(60).freqs.postln;
OraRatios.irrational(60).freqs.postln;
OraRatios.detuned(60).freqs.postln;
OraRatios.eerie(60).freqs.postln;
OraRatios.extreme(60).freqs.postln;
OraRatios.harmonic(60, 12).freqs.postln;
OraRatios.justIntonation(60).freqs.postln;
OraRatios.equalTemperament(60, [2, 4, 5, 7, 9, 11, 12]).freqs.postln;
OraRatios.random(60, 8, 1.05, 1.8).freqs.postln;
)


/*

	OraPlay: playback patterns.

*/

~f = [178.38, 255.19, 884.64, 1748.73, 4473.33, 1257.74];

// Ascending cluster
OraPlay(~f, numChannels: 2).cluster(0.04, 4, \drsoft, 0.15);

// Descending
OraPlay(~f, numChannels: 2).reverse(0.04, 4, \drpm, 0.15);

// Random order
OraPlay(~f, numChannels: 2).random(0.04, 4, \drsoft, 0.2);

// From center outward
OraPlay(~f, numChannels: 2).fromCenter(0.04, 5, \drpm, 0.18);

// Dense cloud
OraPlay(~f, numChannels: 2).dense(0.03, 6, \drsoft, 0.04);

// Sparse irregular timing
OraPlay(~f, numChannels: 2).sparse(0.04, 5, \drpm, 0.3, 1.2);

// Simultaneous pairs
OraPlay(~f, numChannels: 2).pairs(0.04, 4, \drsoft, 0.3);

// Wave (ascending then descending)
OraPlay(~f, numChannels: 2).wave(0.04, 4, \drpm, 0.12);


/*

	OraPlay: clusterGrad with interpolated parameters.

*/

(
OraPlay(Ora([100, 200, 350, 500, 800, 1200]).expand(amount: 1.3), numChannels: 2)
	.clusterGrad(
		ampFrom: 0.02, ampTo: 0.08,
		durFrom: 6, durTo: 2,
		synthdef: \drsoft,
		offsetFrom: 0.08, offsetTo: 0.3,
		ampComp: 1.0
	);
)


/*

	Amplitude compensation and shaping.

*/

// Without compensation
OraPlay([100, 500, 2000, 6000, 10000], numChannels: 2)
	.cluster(0.04, 5, \drsoft, 0.15, ampComp: 0);

// With full perceptual compensation
OraPlay([100, 500, 2000, 6000, 10000], numChannels: 2)
	.cluster(0.04, 5, \drsoft, 0.15, ampComp: 1.0);

// Envelope across the array
OraPlay([100, 200, 350, 500, 800, 1200], numChannels: 2)
	.ampCurve(0.05, 5, \drsoft, 0.15, [0.2, 1.0, 0.3, 1.0, 0.2]);

// Frequency-mapped amplitude
OraPlay([100, 200, 350, 500, 800, 1200, 1800], numChannels: 2)
	.ampByFreq(0.05, 5, \drsoft, 0.12, 100, 1800, [1.0, 0.8, 0.5, 0.2]);


/*

	Combining transforms with playback.

*/

(

~o = Ora([100, 200, 300, 400, 500, 600]).shatter(pieces: 2, spread: 0.05).detune(10);
OraPlay(~o, numChannels: 2).clusterGrad(0.03, 0.06, 6, 3, \drsoft, 0.06, 0.2, 0.8);

)

(

~o = Ora(OraGen.inharmonic(80, 12, 1.06)).centroidDilation(1.4).fluctuate(0.02);
OraPlay(~o, numChannels: 2).dense(0.025, 8, \drpm, 0.03, ampComp: 1.0);

)


/*

	OraRatios to OraPlay (end-to-end).

*/

(

~o = OraRatios.eerie(60).asOra.expand(amount: 1.3).detune(8);
OraPlay(~o, numChannels: 2).clusterGrad(0.03, 0.06, 5, 2, \drsoft, 0.1, 0.25, 1.0);

)

(

OraRatios.tritone(80).asOraPlay(numChannels: 2).cluster(0.04, 6, ampComp: 0.8);

)


/*

	OraSpear: importing SPEAR spectral data.

*/

// ~spear = OraSpear("path/to/analysis.txt");
// ~spear.parse;
// ~spear.printInfo;
// ~o = ~spear.asOra(12, \duration, \weightedMean).detune(5);
// OraPlay(~o, numChannels: 2).cluster(0.04, ampComp: 0.8);


/*

	OraSeq: sequencing multiple Ora states.

*/

(

~f = [100, 200, 350, 500, 800, 1200, 1800];
~seq = OraSeq([
	Ora(~f),
	Ora(~f).centroidDilation(1.5),
	Ora(~f).formantGap(500, 150, 0.25),
	Ora(~f).retrograde.detune(8),
	Ora(~f).shatter(2, 0.04).chaos(0.2)
]);
~seq.info;

)

// Play with one method
~seq.playSequence(\cluster, waitTime: 6, args: [0.04, 5, \drsoft, 0.15]);

// Play with different methods per step
(

~seq.playSequenceWithMethods(
	[\cluster, \reverse, \dense, \sparse, \wave],
	waitTime: 6,
	argsArray: [
		[0.04, 5, \drsoft, 0.15],
		[0.04, 5, \drpm, 0.12],
		[0.03, 8, \drsoft, 0.04],
		[0.04, 5, \drpm],
		[0.04, 4, \drsoft, 0.1]
	]
);

)


/*

	Visualization.

*/

Ora([100, 200, 350, 500, 800, 1200, 1800]).plotStems;
Ora(OraGen.inharmonic(80, 16, 1.06)).plotSpectrum("Inharmonic");
Ora([100, 200, 350, 500, 800, 1200, 1800]).plotIntervals;

(
~a = Ora([100, 200, 350, 500, 800, 1200, 1800]);
~a.plotCompare(Ora(~a.asArray).shatter(3, 0.06).detune(15), "Original", "Shattered");
)

Ora(OraGen.inharmonic(80, 9, 1.12)).plotRadial("Inharmonic");
Ora([100, 200, 350, 500, 800, 1200, 1800]).plotPiano;
Ora([100, 200, 350, 500, 800]).plotMatrix;
Ora(OraGen.cluster(440, 8, 20)).plotWaveform("Cluster", duration: 0.05);

OraRatios.eerie(80).plot;
OraRatios.eerie(80).plotRatios;

(
OraSeq([
	Ora([100, 200, 350, 500, 800, 1200]),
	Ora([100, 200, 350, 500, 800, 1200]).expand(amount: 2.0),
	Ora([100, 200, 350, 500, 800, 1200]).contract(amount: 0.4),
	Ora([100, 200, 350, 500, 800, 1200]).shatter(2, 0.05)
]).plotOverlay;
)

(
OraSeq([
	Ora([100, 200, 350, 500, 800, 1200]),
	Ora([100, 200, 350, 500, 800, 1200]).expand(amount: 2.0),
	Ora([100, 200, 350, 500, 800, 1200]).shatter(2, 0.05),
	Ora([100, 200, 350, 500, 800, 1200]).chaos(0.4)
]).plotHeatmap;
)


/*

	Available SynthDefs.

*/

OraPlay([200, 400, 600], numChannels: 2).cluster(0.04, 4, \drbase, 0.2);
OraPlay([200, 400, 600], numChannels: 2).cluster(0.04, 6, \drpm, 0.2);
OraPlay([200, 400, 600], numChannels: 2).cluster(0.04, 5, \drsoft, 0.2);
OraPlay([200, 400, 600], numChannels: 2).cluster(0.04, 4, \drgend, 0.2);
OraPlay([200, 400, 600], numChannels: 2).cluster(0.04, 4, \drimp, 0.2);
OraPlay([200, 400, 600], numChannels: 2).cluster(0.04, 6, \drdfm, 0.2);
OraPlay([200, 400, 600], numChannels: 2).cluster(0.04, 6, \drpmvara, 0.2);


/*

	Using Ora with Pbind.

*/

(
SynthDef(\grain, { |out=0, freq=440, amp=0.02, atk=0.5, rel=1.5|
	var env = EnvGen.kr(Env.perc(atk, rel), doneAction: 2);
	Out.ar(out, SinOsc.ar(freq) * amp * env ! 2);
}).add;
)

(
~o = Ora([200, 350, 500, 800, 1200]).detune(5).fluctuate(0.02);
Pdef(\oraGrain, Pbind(
	\instrument, \grain,
	\dur, Pwhite(0.3, 0.8),
	\freq, Prand(~o.items, inf),
	\amp, Pwhite(0.01, 0.03),
	\atk, Pwhite(0.3, 1.0),
	\rel, Pwhite(1.0, 4.0)
)).play;
)

// Pdef(\oraGrain).stop;


/*

	Full workflow: generate, transform, visualize, play.

*/

(
~o = Ora(OraGen.inharmonic(80, 10, 1.08))
	.centroidDilation(1.3)
	.formantGap(fHole: 500, bw: 120, amount: 0.2)
	.detune(6).fluctuate(0.02);

~o.plotStems("Result");

OraPlay(~o, numChannels: 2).clusterGrad(
	ampFrom: 0.02, ampTo: 0.06,
	durFrom: 8, durTo: 3,
	offsetFrom: 0.1, offsetTo: 0.25,
	ampComp: 1.0
);
)

~o.items

/*

	Full workflow: ratios to dissonant sequence.

*/

(
~base = OraRatios.eerie(60).freqs;

~seq = OraSeq([
	Ora(~base),
	Ora(~base).expand(amount: 1.5).detune(8),
	Ora(~base).crunch(0.3).wobble(0.2),
	Ora(~base).shatter(2, 0.05).chaos(0.3),
	Ora(~base).retrograde.formantGap(400, 100, 0.3)
]);

~seq.plotOverlay;

~seq.playSequenceWithMethods(
	[\cluster, \dense, \sparse, \wave, \reverse],
	waitTime: 8,
	argsArray: [
		[0.04, 6, \drsoft, 0.15],
		[0.025, 8, \drpm, 0.03],
		[0.04, 5, \drsoft],
		[0.04, 4, \drpm, 0.1],
		[0.04, 5, \drsoft, 0.12]
	]
);
)


/*

	Stopping.

*/

~p = OraPlay([100, 200, 300, 400, 500], numChannels: 2);
~p.cluster(0.04, 8, \drsoft, 0.5);
~p.stop;
