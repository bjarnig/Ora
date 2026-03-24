// combined transforms + Pbind - "Drift" f2-bind-soft, a2-nsane

(

~fbind = [218.89713515951, 279.28973624342, 806.31578550988, 864.33329702304, 1369.1232279037, 1605.9, 2162.8088779968, 2269.4534277401];
~fnsane = [178.38052971582, 255.19458012749, 884.64258085768, 1748.7291446291, 9043.8864701014, 4473.3283429036, 1257.7392833936] * 0.5;

SynthDef(\softPMGrain, { |out=0, freq=440, amp=0.015, dur=3, atk=1.0, rel=2.0, index=0.1|
	var env = EnvGen.kr(Env.perc(atk, rel), doneAction: 2);
	var sig = PMOsc.ar(freq, freq * rrand(0.99, 1.01), index) * amp * env;
	sig = HPF.ar(sig, 50);
	Out.ar(out, sig);
}).add;

SynthDef(\softGrain, { |out=0, freq=440, amp=0.02, dur=2, atk=0.5, rel=1.5|
	var env = EnvGen.kr(Env.perc(atk, rel), doneAction: 2);
	var sig = SinOsc.ar(freq) * amp * env;
	sig = HPF.ar(sig, 40);
	Out.ar(out, sig);
}).add;

Ora.addSynths;

)

(

{
	var pbo = Ora(~fbind);
	OraPlay(pbo, numChannels:8)
		.clusterGrad(0.0, 0.3, 1.5, 6, \drsoft, 0.02, 3.0, 0.5);
	0.01.wait;
	Tdef(\ghostBind, {
		inf.do {
			Pdef(\ghostJ, Pbind(
				\instrument, \softPMGrain,
				\dur, Prand([Pwhite(0.3, 0.8, 8), Pwhite(1.0, 2.0, 4)], inf),
				\rel, Pwhite(3.0, 6.0),
				\atk, Pwhite(0.5, 1.5),
				\amp, 0.008,
				\index, Pwhite(0.02, 0.1),
				\x, Pseq(pbo.items.scramble, inf),
				\freq, Pdup(Prand([2, 3, 1], inf),
					Pfunc {|ev| ev.x * Prand([0.5, 1, 2], 1).asStream.next }),
				\out, Pwhite(0, 7, rrand(16, 32))
			)).play;
			8.wait;
		};
	}).play;
	25.wait;
	Tdef(\ghostBind).stop;
	Pdef(\ghostJ).stop;
}.fork;

)

(

{
	OraPlay(Ora(~fbind).fluctuate(0.005), numChannels:8)
		.clusterGrad(0.0, 0.25, 1.5, 5, \drsoft, 0.03, 2.5, 0.5);
	0.01.wait;
	Pdef(\pmA, Pbind(
		\instrument, \softPMGrain,
		\dur, Prand([Pwhite(0.5, 1.2, 6), Pwhite(1.0, 2.5, 4)], inf),
		\rel, Pwhite(3.0, 7.0),
		\atk, Pwhite(0.5, 2.0),
		\amp, 0.01,
		\index, Pwhite(0.03, 0.15),
		\freq, Prand(~fbind * Prand([0.5, 1, 2], inf), inf),
		\out, Pwhite(0, 7)
	)).play;
	15.wait;
	Pdef(\pmB, Pbind(
		\instrument, \softPMGrain,
		\dur, Pwhite(1.5, 3.5),
		\rel, Pwhite(4.0, 8.0),
		\atk, Pwhite(1.0, 3.0),
		\amp, 0.008,
		\index, Pwhite(0.02, 0.08),
		\freq, Pseq(Ora(~fbind).inversion.asArray, inf),
		\out, Pwhite(0, 7)
	)).play;
	OraPlay(Ora(~fbind).retrograde.detune(3), numChannels:8)
		.clusterGrad(0.02, 0.18, 1.2, 4, \drsoft, 0.04, 1.5, 0.4);
	12.wait;
	Pdef(\pmA).stop;
	Pdef(\pmB).stop;
}.fork;

)

(

OraPlay(
	Ora((~fnsane.scramble + ~fnsane.scramble) * 0.25),
	numChannels:8
).clusterGrad(0.05, 0.18, 2, 5, \drsoft, 0.05, 1.8);

)
