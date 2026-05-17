// Ora SynthDefs

SynthDef(\drbase, { |out=0, freq=440, amp=0.045, dur=4, pan=0|
	var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 4.5, 0.8, curve: -4), doneAction: 2);
	var sig = SinOsc.ar(freq) * amp * env * XFade2.ar(
		WhiteNoise.ar().range(0.99, 1.0),
		Lag.ar(WhiteNoise.ar, 0.2) * 4,
		Line.kr(-1, 1, dur)
	);
	Out.ar(out, sig);
}).add;

SynthDef(\drpm, { |out=0, freq=440, amp=0.045, dur=10, atk=0.5, rel=4.5, pan=0, mod=7|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = PMOsc.ar(freq, freq * 0.001, 0.1) * amp * env;
	sig = sig * SinOsc.ar(mod).range(0.5, 1.0) * Lag.ar(WhiteNoise.ar().range(0.8, 1.0));
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

SynthDef(\drpmvara, { |out=0, freq=440, amp=0.045, dur=10, atk=0.5, rel=4.5, pan=0, mod=15|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = PMOsc.ar(freq, freq * 8.01, 2.1) * amp * env;
	sig = sig * SinOsc.ar(mod).range(0.4, 1.0) * Lag.ar(WhiteNoise.ar().range(0.8, 1.0), 0.05);
	Out.ar(out, sig);
}).add;

SynthDef(\drsoft, { |out=0, freq=440, amp=0.045, dur=4, atk=0.5, rel=4.5, pan=0|
	var dec = dur - (atk + rel);
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = SinOsc.ar(freq) * amp * env * XFade2.ar(
		WhiteNoise.ar().range(0.99, 1.0),
		Lag.ar(WhiteNoise.ar, 0.2) * 10,
		Line.kr(0.0, 0.25, dur * 0.1)
	);
	Out.ar(out, sig);
}).add;

SynthDef(\drgend, { |out=0, freq=440, amp=0.045, dur=4, pan=0|
	var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 2.5, curve: -4), doneAction: 2);
	var sig = BBandPass.ar(
		Gendy5.ar(minfreq: freq * 0.5, maxfreq: freq, initCPs: 24),
		freq,
		0.02,
		5
	) * amp * env;
	Out.ar(out, sig);
}).add;

SynthDef(\drimp, { |out=0, freq=440, amp=0.045, dur=4, pan=0|
	var env = EnvGen.kr(Env.linen(0.5, dur - 1.0, 5.5, curve: -4), doneAction: 2);
	var in = Impulse.ar(10) + (WhiteNoise.ar * 1e-8);
	var delayTime = 1 / freq;
	var decayTime = 0.2;
	var sig = CombC.ar(in, 1, delayTime, decayTime);
	sig = LeakDC.ar(sig);
	sig = sig * amp * env;
	Out.ar(out, sig);
}).add;

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
	Out.ar(out, Mix(sig) * env);
}).add;

SynthDef(\drclick, { |out=0, freq=440, amp=0.07, dur=0.08, pan=0|
	var env = XLine.kr(1.0, 0.0005, dur, doneAction: 2);
	var sig = FSinOsc.ar(freq, 0.5pi) * amp * env;
	Out.ar(out, sig);
}).add;

SynthDef(\drsoftbreath, { |out=0, freq=440, amp=0.05, dur=4, atk=1.0, rel=4, pan=0, breath=0.3|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var noise = Lag.ar(WhiteNoise.ar, 0.4).range(1.0 - breath, 1.0);
	var swell = EnvGen.kr(Env([0.0, 1.0, 0.6], [atk + (dur * 0.5), (dur * 0.5) + rel], \sin));
	var sig = SinOsc.ar(freq) * amp * env * noise * swell;
	Out.ar(out, sig);
}).add;

SynthDef(\drtap, { |out=0, freq=440, amp=0.05, dur=0.4, pan=0|
	var env = XLine.kr(1.0, 0.0005, dur, doneAction: 2);
	var sig = FSinOsc.ar(freq, 0.5pi) * amp * env;
	sig = sig * Lag.ar(WhiteNoise.ar.range(0.85, 1.0), 0.01);
	Out.ar(out, sig);
}).add;

SynthDef(\drhush, { |out=0, freq=440, amp=0.035, dur=20, atk=5, rel=8, pan=0|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.4, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var slow = Lag.ar(WhiteNoise.ar.range(0.7, 1.0), 0.6);
	var slower = Lag.ar(LFNoise2.ar(0.1).range(0.6, 1.0), 0.4);
	var sig = SinOsc.ar(freq) * amp * env * slow * slower;
	Out.ar(out, sig);
}).add;

SynthDef(\drwhisper, { |out=0, freq=440, amp=0.05, dur=6, atk=1.5, rel=4, pan=0|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var air = BPF.ar(PinkNoise.ar, freq * 1.5, 0.5) * 0.08;
	var sig = (SinOsc.ar(freq) + air) * amp * env;
	sig = sig * Lag.ar(WhiteNoise.ar.range(0.85, 1.0), 0.15);
	Out.ar(out, sig);
}).add;

// Filtered pink noise as tone — narrow Resonz at freq, hollow/breathy sine substitute
SynthDef(\drnoise, { |out=0, freq=440, amp=0.05, dur=6, atk=1, rel=4, pan=0, q=0.01|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = Resonz.ar(PinkNoise.ar, freq, q) * 5 * amp * env;
	sig = sig * Lag.ar(WhiteNoise.ar.range(0.85, 1.0), 0.1);
	Out.ar(out, sig);
}).add;

// Sparse dust hits ringing through a resonator — textural, almost-tone drone
SynthDef(\drring, { |out=0, freq=440, amp=0.04, dur=6, atk=1, rel=4, pan=0, decay=3, density=12|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = Ringz.ar(Dust.ar(density) * 0.2, freq, decay) * amp * env;
	sig = sig * Lag.ar(WhiteNoise.ar.range(0.85, 1.0), 0.1);
	Out.ar(out, sig);
}).add;

// Two sines with tiny drifting detune — slow beating that wanders in rate
SynthDef(\drbeats, { |out=0, freq=440, amp=0.045, dur=10, atk=1, rel=4, pan=0|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var detune = LFNoise2.kr(0.05).range(0.0008, 0.003);
	var sig = SinOsc.ar(freq) + SinOsc.ar(freq + (freq * detune));
	sig = sig * 0.5 * amp * env;
	sig = sig * Lag.ar(WhiteNoise.ar.range(0.9, 1.0), 0.1);
	Out.ar(out, sig);
}).add;

// Sine through gentle tanh saturation — a hint of warmth, micro odd-harmonics
SynthDef(\drbrush, { |out=0, freq=440, amp=0.05, dur=6, atk=1, rel=4, pan=0, drive=1.15|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var sig = (SinOsc.ar(freq) * drive).tanh * amp * env;
	sig = sig * Lag.ar(WhiteNoise.ar.range(0.85, 1.0), 0.1);
	Out.ar(out, sig);
}).add;

// Tremolo whose rate scales with frequency and whose depth evolves across the note
SynthDef(\drwave, { |out=0, freq=440, amp=0.05, dur=8, atk=1, rel=4, pan=0|
	var env = EnvGen.kr(Env([0.0, 1.0, 0.25, 0.0], [atk, dur, rel], [-2, 2]), doneAction: 2);
	var rate = (freq * 0.002) * LFNoise2.kr(0.07).range(0.7, 1.3);
	var depth = EnvGen.kr(Env([0.0, 0.22, 0.08], [dur * 0.6, (dur * 0.4) + rel], \sin));
	var tr = SinOsc.kr(rate).range(1.0 - depth, 1.0);
	var sig = SinOsc.ar(freq) * amp * env * tr;
	sig = sig * Lag.ar(WhiteNoise.ar.range(0.92, 1.0), 0.05);
	Out.ar(out, sig);
}).add;

"Ora: 19 SynthDefs loaded (drbase, drpm, drpmg, drpmgd, drpmvara, drsoft, drgend, drimp, drdfm, drclick, drsoftbreath, drtap, drhush, drwhisper, drnoise, drring, drbeats, drbrush, drwave)".postln;
