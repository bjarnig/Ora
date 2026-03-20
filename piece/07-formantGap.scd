// formantGap - "Drift" c2-softes-soft, e2-dynmc-soft, f2-bind-soft

(
~fsoftes = [178.38052971582, 255.19458012749, 884.64258085768, 1748.7291446291, 9043.8864701014, 4473.3283429036, 1257.7392833936] * 4;
~fbind = [218.89713515951, 279.28973624342, 806.31578550988, 864.33329702304, 1369.1232279037, 1605.9, 2162.8088779968, 2269.4534277401];
Ora.addSynths;
)

(
OraPlay(
	Ora(~fsoftes.scramble).formantGap(~fsoftes[2], 200, 0.25),
	numChannels:8
).clusterGrad(0.0, 0.2, 1.5, 5, \drimp, 0.03, 2.5, 0.5);
)

(
OraPlay(
	Ora(~fsoftes).formantGap(~fsoftes[5], 300, 0.2),
	numChannels:8
).clusterGrad(0.02, 0.15, 1.2, 4, \drimp, 0.04, 1.5, 0.5);
)

(
NF(\bpFormant, {|wfMult = 0.15, amp = 0.03, seqMult = 10, bpQ = 0.03|
	var bpFreqs = Ora(~fbind).formantGap(500, 150, 0.2).asArray;
	var sd = SampleDur.ir * wfMult;
	var sig;
	sig = DemandEnvGen.ar(
		Dseq([-0.04, 0.04, Dwhite(-0.02, 0.02, 3)], inf),
		Dseq([sd * seqMult, sd * 12 * seqMult,
			sd * Dwhite(seqMult * 8, seqMult * 12, inf)], inf)
	);
	sig = BBandPass.ar(sig, bpFreqs, bpQ);
	sig = sig * LFNoise2.ar(Array.fill(bpFreqs.size, { rrand(0.01, 0.06) })).range(0.0, 1.0);
	sig.scramble * amp * XLine.ar(0.001, 1.0, 15);
}).play(8);
)

NF(\bpFormant).clear(10);

(
{
	OraPlay(Ora(~fbind).formantGap(800, 200, 0.2), numChannels:8)
		.clusterGrad(0.0, 0.3, 1.8, 6, \drsoft, 0.02, 3.5, 0.4);
	12.wait;
	OraPlay(Ora(~fbind).formantGap(1400, 250, 0.25), numChannels:8)
		.clusterGrad(0.02, 0.2, 1.2, 4, \drsoft, 0.04, 2.0, 0.3);
	12.wait;
	OraPlay(Ora(~fbind).contract(amount: 0.7).formantGap(500, 150, 0.2), numChannels:8)
		.clusterGrad(0.02, 0.15, 1.2, 4, \drsoft, 0.05, 1.5, 0.3);
}.fork
)
