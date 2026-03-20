// ============================================================
// ORA LIBRARY - SYNTHDEFS
// ============================================================
//
// Default SynthDefs for Ora playback
// Feel free to add your own custom SynthDefs here
//
// ============================================================

// drbase - clean sine with subtle noise modulation
SynthDef(\drbase, { |out=0, freq=440, amp=0.045, dur=4, pan=0|
	var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 4.5, 0.8, curve: -4), doneAction: 2);
	var sig = SinOsc.ar(freq) * amp * env * XFade2.ar(
		WhiteNoise.ar().range(0.99, 1.0),
		Lag.ar(WhiteNoise.ar, 0.2) * 4,
		Line.kr(-1, 1, dur)
	);
	// Mono output for multichannel flexibility
	Out.ar(out, sig);
}).add;

// drpm - phase modulation with amplitude tremolo
SynthDef(\drpm, { |out=0, freq=440, amp=0.045, dur=10, atk=0.5, rel=4.5, pan=0, mod=7|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = PMOsc.ar(freq, freq * 0.001, 0.1) * amp * env;
	sig = sig * SinOsc.ar(mod).range(0.5, 1.0) * Lag.ar(WhiteNoise.ar().range(0.8, 1.0));
	// Mono output for multichannel flexibility
	Out.ar(out, sig);
}).add;

SynthDef(\drpmg, { |out=0, freq=440, amp=0.045, dur=10, atk=0.5, rel=4.5, pan=0, mod=7,gate=1|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env.asr(atk, 1, rel, [-2, 2]), gate, doneAction: 2);
	var sig = PMOsc.ar(freq, freq * 0.001, 0.1) * amp * env;
	sig = sig * SinOsc.ar(mod).range(0.5, 1.0) * Lag.ar(WhiteNoise.ar().range(0.8, 1.0));
	Out.ar(out, sig);
}).add;

SynthDef(\drpmgd, { |out=0, freq=440, amp=0.045, dur=10, atk=0.5, rel=4.5, pan=0, mod=7,gate=1|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env.adsr(atk, atk*2, 0.5, rel, [-2, 2]), gate, doneAction: 2);
	var sig = PMOsc.ar(freq, freq * 0.001, 0.1) * amp * env;
	sig = sig * SinOsc.ar(mod).range(0.5, 1.0) * Lag.ar(WhiteNoise.ar().range(0.8, 1.0));
	Out.ar(out, sig);
}).add;

// drpm - variation a
SynthDef(\drpmvara, { |out=0, freq=440, amp=0.045, dur=10, atk=0.5, rel=4.5, pan=0, mod=15|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = PMOsc.ar(freq, freq * 8.01, 2.1) * amp * env;
	sig = sig * SinOsc.ar(mod).range(0.4, 1.0) * Lag.ar(WhiteNoise.ar().range(0.8, 1.0), 0.05);
	// Mono output for multichannel flexibility
	Out.ar(out, sig);
}).add;

// drsoft - soft sine with gentle noise crossfade
SynthDef(\drsoft, { |out=0, freq=440, amp=0.045, dur=4, atk=0.5, rel=4.5, pan=0|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = SinOsc.ar(freq) * amp * env * XFade2.ar(
		WhiteNoise.ar().range(0.99, 1.0),
		Lag.ar(WhiteNoise.ar, 0.2) * 10,
		Line.kr(0.0, 0.25, dur * 0.1)
	);
	// Mono output for multichannel flexibility
	Out.ar(out, sig);
}).add;

// drgend - gendy-based organic texture
SynthDef(\drgend, { |out=0, freq=440, amp=0.045, dur=4, pan=0|
	var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 2.5, curve: -4), doneAction: 2);
	var sig = BBandPass.ar(
		Gendy5.ar(minfreq: freq * 0.5, maxfreq: freq, initCPs: 24),
		freq,
		0.02,
		5
	) * amp * env;
	// Mono output for multichannel flexibility
	Out.ar(out, sig);
}).add;

// drimp - impulse-based comb filter
SynthDef(\drimp, { |out=0, freq=440, amp=0.045, dur=4, pan=0|
	var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 5.5, curve: -4), doneAction: 2);
	var in = Impulse.ar(10) + (WhiteNoise.ar * 1e-8);
	var delayTime = 1 / freq;
	var decayTime = 0.2;
	var sig = CombC.ar(in, 1, delayTime, decayTime);
	sig = LeakDC.ar(sig);
	sig = sig * amp * env;
	// Mono output for multichannel flexibility
	Out.ar(out, sig);
}).add;

// drdfm - dual formant synthesis with beating
SynthDef(\drdfm, {
	|out=0, freq=440, amp=0.045, dur=4, lffreq=0.1, resfreq=10.1,
	resfrom=0.92, resto=1.018, lpf=50, lpfrq=4, beating=1.08,
	resboost=2400, pan=0|
	var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 4.5, curve: -4), doneAction: 2);
	var sig = BPeakEQ.ar(
		DFM1.ar(
			SinOsc.ar([freq, freq * beating] * LFNoise0.ar(lffreq).range(0.98, 1.15), 0, 0.1),
			resboost,
			SinOsc.kr(resfreq).range(resfrom, resto),
			1, 0, 0.003, 0.5
		),
		lpf, lpfrq, -9
	) * 3 * amp;
	// Mix stereo array to mono for multichannel compatibility
	Out.ar(out, Mix(sig) * env);
}).add;

"Ora: 9 SynthDefs loaded (drbase, drpm, drpmg, drpmgd, drpmvara, drsoft, drgend, drimp, drdfm)".postln;
